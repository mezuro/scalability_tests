package org.mezuro.scalability_tests.REST.rangeEndpoint;

import java.util.Map;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Exists extends RESTStrategy {

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(KALIBRO_RANGE_PATH + "/1/exists"));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}