package org.mezuro.scalability_tests.REST.baseToolEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class AllBaseToolNames extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allBaseToolNames");
	}

}
