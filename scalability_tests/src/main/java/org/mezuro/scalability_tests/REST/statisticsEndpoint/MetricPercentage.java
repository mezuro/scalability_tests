package org.mezuro.scalability_tests.REST.statisticsEndpoint;

import java.util.Map;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class MetricPercentage extends RESTStrategy {
	
	@Override
	public String request(String string) throws Exception {
		get(buildUrl("statistics/metric_percentage"), "metric_code", "flay");
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}
