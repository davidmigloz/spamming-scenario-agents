package com.davidmiguel.spammingscenario.agents;

import jade.core.Agent;

/**
 * Experiment Master Agent (EMA). Initializes the experiment sending
 * "start message" to all SA and measures the total time of processing all
 * messages by all MCA's.
 */
public class EMA extends Agent {

	@Override
	protected void setup() {
		System.out.println("Hello World! My name is " + getLocalName());
		doDelete();
	}
}