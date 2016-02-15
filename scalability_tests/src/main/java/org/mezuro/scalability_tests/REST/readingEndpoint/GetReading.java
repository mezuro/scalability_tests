package org.mezuro.scalability_tests.REST.readingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class GetReading extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getReading", "1");
	}

}
