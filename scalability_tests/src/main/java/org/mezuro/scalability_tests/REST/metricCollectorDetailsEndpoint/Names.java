package org.mezuro.scalability_tests.REST.metricCollectorDetailsEndpoint;

import java.util.Map;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Names extends RESTStrategy {

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(METRIC_COLLECTOR_DETAILS_PATH + "/names"));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}
}
