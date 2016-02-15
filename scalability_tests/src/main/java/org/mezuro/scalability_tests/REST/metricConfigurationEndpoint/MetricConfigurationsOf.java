package org.mezuro.scalability_tests.REST.metricConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class MetricConfigurationsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("metricConfigurationsOf", "1");
	}

}
