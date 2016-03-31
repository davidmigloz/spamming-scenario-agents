package com.davidmiguel.spammingscenario.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * Spammer Agent (SA). Sends N messages of size M to all MCS's. 
 * Run: 
 * java jade.Boot -gui SAx:com.davidmiguel.spammingscenario.agents.SA(N, M) 
 * - N: number of messages. 
 * - M: size of each message.
 */
public class SA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = -3669628420932251804L;

	private int n;
	private int m;

	@Override
	protected void setup() {
		// Get number of messages to send and its size
		Object[] args = getArguments();
		if (args != null && args.length == 2) {
			n = Integer.parseInt((String) args[0]);
			m = Integer.parseInt((String) args[1]);
			logger.log(Logger.INFO, "Agent " + getLocalName() + " - Target: " + n + " msg / " + m + " size");
		} else {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Incorrect number of arguments");
			doDelete();
		}
		// Register the spamming service in the yellow pages
		ServiceDescription sd = new ServiceDescription();
		sd.setType("SA");
		sd.setName("SAService");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
			doDelete();
		}
		// Listen to START message from EMA
		addBehaviour(new SimpleBehaviour(this) {
			private static final long serialVersionUID = -1344483830624564835L;
			private boolean start = false;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(new AID("EMA", AID.ISLOCALNAME)),
						MessageTemplate.MatchContent(EMA.START));
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					// Start spamming MCA's
					start = true;
					System.out.println("Spamming: " + n + " msg / " + n + " size");
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return start;
			}
		});
	}
}