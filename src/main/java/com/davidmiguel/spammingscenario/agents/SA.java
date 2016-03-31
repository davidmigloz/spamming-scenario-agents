package com.davidmiguel.spammingscenario.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
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
 * Run: java
 * jade.Boot -gui SAx:com.davidmiguel.spammingscenario.agents.SA(N, M) 
 * - N: number of messages. 
 * - M: size of each message.
 */
public class SA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = -3669628420932251804L;

	public final static String LANG = "spam";
	
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
					myAgent.addBehaviour(new SpammerBehaviour());
					start = true;
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

	/**
	 * Sends N messages of size M to each MCA of the platform.
	 */
	private class SpammerBehaviour extends OneShotBehaviour {

		private static final long serialVersionUID = -8492387448755961987L;
		// List of known Message Consuming Agents (MCA)
		private AID[] MCAs;

		@Override
		public void action() {
			// Get list of Message Consuming Agents (MCA)
			ServiceDescription sd = new ServiceDescription();
			sd.setType("MCA");
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, dfd);
				logger.log(Logger.INFO, "Found " + result.length + " MCA's");
				MCAs = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					MCAs[i] = result[i].getName();
				}
			} catch (FIPAException e) {
				logger.log(Logger.SEVERE, "Cannot get MCA's", e);
			}
			// Generate message to spam
			String content = "";
			for (int i = 0; i < m; i++) {
				content += "A";
			}
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < MCAs.length; ++i) {
				msg.addReceiver(MCAs[i]);
			}
			msg.setContent(content);
			msg.setLanguage(LANG);
			// Send message n times
			for (int i = 0; i < n; i++) {
				myAgent.send(msg);
			}
		}
	}
}