package com.davidmiguel.spammingscenario.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

/**
 * Experiment Master Agent (EMA). Initializes the experiment sending START
 * message to all SA's and measures the total time of processing all messages by
 * all MCA's.
 */
public class EMA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());

	public final static String DONE = "done";
	public final static String START = "start";

	// The list of known spammer agents
	private AID[] SAs;

	@Override
	protected void setup() {
		addBehaviour(new OneShotBehaviour(this) {
			@Override
			public void action() {
				// Get list of spammer agents
				ServiceDescription sd = new ServiceDescription();
				sd.setType("MCA");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, dfd);
					logger.log(Logger.INFO, "Found " + result.length + " spammer agents");
					SAs = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						SAs[i] = result[i].getName();
					}
				} catch (FIPAException e) {
					logger.log(Logger.SEVERE, "Cannot get spammer agents", e);
				}
			}
		});
	}
}