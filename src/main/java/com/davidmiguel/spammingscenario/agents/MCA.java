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
 * java jade.Boot -gui MCAx:com.davidmiguel.spammingscenario.agents.MCA(N) 
 * - N: number of messages to receive from each SA.
 */
public class MCA extends Agent {

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	private static final long serialVersionUID = 9085335745014921813L;
	
	private int n;

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
	}
}