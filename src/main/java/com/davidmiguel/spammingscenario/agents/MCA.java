package com.davidmiguel.spammingscenario.agents;

import java.util.HashMap;
import java.util.Map;

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
 * SA's. When all messages have been processed, it sends message "done" to the
 * EMA. It knows how many messages from each SA should receive. 
 * Run:
 * java jade.Boot -gui MCAx:com.davidmiguel.spammingscenario.agents.MCA(N) 
 * - N: number of messages to receive from each SA.
 * Note: Spammer Agentes (SA's) must be running befoure run MCA's.
 */
public class MCA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 9085335745014921813L;

	private int n;
	private int nSAs; // Number SA's

	@Override
	protected void setup() {
		// Get number of messages to receive from each SA
		Object[] args = getArguments();
		if (args != null && args.length == 1) {
			n = Integer.parseInt((String) args[0]);
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

	private class MessageConsumingBehaviour extends Behaviour {

		private static final long serialVersionUID = -5860119910249641199L;

		private Map<String, Integer> received; // SA -> nº of msg received

		public MessageConsumingBehaviour() {
			super();
			this.received = new HashMap<>(nSAs);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchLanguage(SA.LANG));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Process message
				logger.log(Logger.INFO, "Agent " + getLocalName() + " - Message processed: " + msg.getContent());
				// Update register of received messages
				String sender = msg.getSender().getName();
				if (received.containsKey(sender)) {
					received.put(sender, received.get(sender) + 1);
				} else {
					received.put(sender, 1);
				}
			} else {
				block();
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