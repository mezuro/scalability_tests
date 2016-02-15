package org.mezuro.scalability_tests.REST.repositoryEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Branches extends RESTStrategy {

	private String url = "https://github.com/mezuro/kalibro_processor.git";
	private String smc_type = "GIT";

	@Override
	public String request(String string) throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("url", url);
		parameters.put("scm_type" , smc_type);
		JSONObject jsonBody = new JSONObject(parameters);
		post(buildUrl(REPOSITORY_PATH + "/branches"), jsonBody);
		return string;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
