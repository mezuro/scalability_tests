package org.mezuro.scalability_tests.REST.readingGroupEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class AllReadingGroups extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allReadingGroups");
	}

}