package org.mezuro.scalability_tests.SOAP.metricConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPMetricConfigurationsOf extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("metricSOAPConfigurationsOf", "1");
	}

}
