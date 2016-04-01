package com.davidmiguel.spammingscenario.agents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * Message Consuming Agent (MCA). Receives and processes the messages sent by
 * SA's. When all messages have been processed, it sends message DONE to the
 * EMA. It knows how many messages from each SA should receive. 
 * It looks for a message from a specific agent first, and when there are none,
 * it precesses the remaining messages in FIFO order.
 * Run: 
 * java jade.Boot -container MCAx:com.davidmiguel.spammingscenario.agents.MCAv2(N,SA) 
 * - N: number of messages to receive from each SA. 
 * - SA: name of the SA to read its messages first.
 * Note: Spammer Agentes (SA's) must be running befoure run MCA's.
 */
public class MCAv2 extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 9085335745014921813L;

	/** Number of messages sent by each SA */
	private int n;
	/** Priority SA */
	private String pSA;
	/** Number SA's */
	private int nSAs;

	@Override
	protected void setup() {
		// Get number of messages to receive from each SA
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			n = Integer.parseInt((String) args[0]);
			pSA = (String) args[1];
			logger.log(Logger.INFO, "Agent " + getLocalName() + " - Target: " + n + " msg");
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Register the message consuming service in the yellow pages
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MCA");
		sd.setName("MCAService");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
			doDelete();
		}
		// Get number of Spamer Agents (SA)
		sd = new ServiceDescription();
		sd.setType("SA");
		dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			nSAs = result.length;
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Cannot get SA's", e);
		}
		// Add the behaviour consuming spam messages
		addBehaviour(new MessageConsumingBehaviour());
	}

	/**
	 * Receive and process each message sent by Spammer Agents. Send DONE
	 * message to EMA when all messages have been received.
	 */
	private class MessageConsumingBehaviour extends Behaviour {

		private static final long serialVersionUID = -5860119910249641199L;
		/** SA -> nº of msg received by it */
		private Map<String, Integer> received; 
		/** Messages to read */
		private Queue<ACLMessage> toRead;

		public MessageConsumingBehaviour() {
			super();
			this.received = new HashMap<>(nSAs);
			this.toRead = new LinkedList<>();
		}

		@Override
		public void action() {
			// Receive spam messages
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchLanguage(SA.LANGUAGE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Check if it is from the priority SA
				if(msg.getSender().getName().equals(pSA)){
					// Process message
					processMessage(msg);					
				} else {
					// Put the message in the queue
					toRead.add(msg);
				}
			} else if(toRead.size() > 0) {
				// Consume message from the FIFO queue
				msg = toRead.poll();
				processMessage(msg);
			} else {
				block();
			}
		}
		
		/**
		 * Process message. In our case, just log it.
		 */
		private void processMessage(ACLMessage msg){
			logger.log(Logger.INFO, "Agent " + getLocalName() + " - Message processed: " + msg.getContent());
			updateRegister(msg);
		}
		
		/**
		 * Update register of received messages
		 */
		private void updateRegister(ACLMessage msg) {
			String sender = msg.getSender().getName();
			if (received.containsKey(sender)) {
				received.put(sender, received.get(sender) + 1);
			} else {
				received.put(sender, 1);
			}
		}

		@Override
		public boolean done() {
			// Check all expected agents are registered
			if (received.size() != nSAs) {
				return false;
			}
			// Check if all messages have been received
			for (int i : received.values()) {
				if (i != n) {
					return false;
				}
			}
			// Send DONE message to EMA
			ACLMessage doneMsg = new ACLMessage(ACLMessage.INFORM);
			doneMsg.addReceiver(new AID("EMA", AID.ISLOCALNAME));
			doneMsg.setContent(EMA.DONE);
			myAgent.send(doneMsg);
			return true;
		}
	}
}