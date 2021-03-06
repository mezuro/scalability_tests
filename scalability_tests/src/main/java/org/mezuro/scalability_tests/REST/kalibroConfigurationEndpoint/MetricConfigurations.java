package org.mezuro.scalability_tests.REST.kalibroConfigurationEndpoint;

import java.util.Map;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class MetricConfigurations extends RESTStrategy {
	
	@Override
	public String request(String string) throws Exception {
		get(buildUrl(KALIBRO_CONFIGURATION_PATH + "/1/metric_configurations"));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}
}
