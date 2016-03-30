package com.davidmiguel.spammingscenario.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

/**
 * Message Consuming Agent (MCA). Receives and processes the messages sent by
 * SA's. When all messages have been processed, it sends message "done" to the
 * EMA. It knows how many messages from each SA should receive.
 */
public class MCA extends Agent {

	private Logger logger = Logger.getMyLogger(getClass().getName());

	@Override
	protected void setup() {
		// Register the message consuming service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MCA");
		sd.setName("spamming-scenario");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			logger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
			doDelete();
		}
	}
}