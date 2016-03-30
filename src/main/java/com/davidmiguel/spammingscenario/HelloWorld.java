package com.davidmiguel.spammingscenario;

import jade.core.Agent;

public class HelloWorld extends Agent {

	@Override
	protected void setup() {
		System.out.println("Hello World! My name is " + getLocalName());
		doDelete();
	}
}