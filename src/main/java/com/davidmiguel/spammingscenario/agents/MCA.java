package com.davidmiguel.spammingscenario.agents;

import jade.core.Agent;

/**
 * Message Consuming Agent (MCA). Receives and processes the messages sent by
 * SA's. When all messages have been processed, it sends message "done" to the
 * EMA. It knows how many messages from each SA should receive.
 */
public class MCA extends Agent {

	@Override
	protected void setup() {
		System.out.println("Hello World! My name is " + getLocalName());
		doDelete();
	}
}