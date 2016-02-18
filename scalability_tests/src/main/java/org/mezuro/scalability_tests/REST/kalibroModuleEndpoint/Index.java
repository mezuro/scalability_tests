package org.mezuro.scalability_tests.REST.kalibroModuleEndpoint;

import java.util.Map;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Index extends RESTStrategy {

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(KALIBRO_MODULE_PATH));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}
}
