package org.mezuro.scalability_tests.SOAP.readingGroupEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class AllSOAPReadingGroups extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allSOAPReadingGroups");
	}

}
