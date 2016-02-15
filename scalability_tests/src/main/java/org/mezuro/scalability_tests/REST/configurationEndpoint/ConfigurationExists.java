package org.mezuro.scalability_tests.REST.configurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class ConfigurationExists extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("configurationExists", "1");
	}

}
