package com.davidmiguel.spammingscenario.agents;

import jade.core.Agent;

/**
 * Spammer Agent (SA). Sends N messages of size M to all MCS's.
 */
public class SA extends Agent {

	@Override
	protected void setup() {
		System.out.println("Hello World! My name is " + getLocalName());
		doDelete();
	}
}