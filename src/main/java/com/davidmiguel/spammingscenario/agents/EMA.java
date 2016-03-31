package com.davidmiguel.spammingscenario.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * Experiment Master Agent (EMA). Initializes the experiment sending START
 * message to all SA's and measures the total time of processing all messages by
 * all MCA's. The name of the agent must be 'EMA'.
 */
public class EMA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 570376489866952222L;

	public final static String DONE = "done";
	public final static String START = "start";

	/** List of known Spammer Agents(SA) */
	private AID[] SAs;
	/** Number of MCS's */
	private int nMCAs;

	@Override
	protected void setup() {
		addBehaviour(new OneShotBehaviour(this) {
			private static final long serialVersionUID = 1582761767744710850L;

			@Override
			public void action() {
				// Get list of Spammer Agents (SA)
				ServiceDescription sd = new ServiceDescription();
				sd.setType("SA");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, dfd);
					logger.log(Logger.INFO, "Found " + result.length + " SA's");
					SAs = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						SAs[i] = result[i].getName();
					}
				} catch (FIPAException e) {
					logger.log(Logger.SEVERE, "Cannot get SA's", e);
				}
				// Get number of Message Consuming Agents (MCA)
				sd = new ServiceDescription();
				sd.setType("MCA");
				dfd = new DFAgentDescription();
				dfd.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, dfd);
					nMCAs = result.length;
				} catch (FIPAException e) {
					logger.log(Logger.SEVERE, "Cannot get MCA's", e);
				}
				// Send START message to all SA's
				ACLMessage startMsg = new ACLMessage(ACLMessage.REQUEST);
				for (int i = 0; i < SAs.length; ++i) {
					startMsg.addReceiver(SAs[i]);
				}
				startMsg.setContent(EMA.START);
				myAgent.send(startMsg);
				// Add the behaviour listen to done messages
				addBehaviour(new ListenDoneMessagesBehaviour());
			}
		});
	}

	private class ListenDoneMessagesBehaviour extends Behaviour {

		private static final long serialVersionUID = 4075092804919487501L;
		private int done;

		public ListenDoneMessagesBehaviour() {
			super();
			done = 0;
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchContent(EMA.DONE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				++done;
			} else {
				block();
			}
		}

		@Override
		public boolean done() {
			return done == nMCAs;
		}

		@Override
		public int onEnd() {
			System.out.println("I'm done!!!");
			return 0;
		}
	}
}