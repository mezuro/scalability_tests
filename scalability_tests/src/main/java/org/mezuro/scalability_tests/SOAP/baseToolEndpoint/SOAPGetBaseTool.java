package org.mezuro.scalability_tests.SOAP.baseToolEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPGetBaseTool extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getBaseTool", "Analizo");
	}

}
