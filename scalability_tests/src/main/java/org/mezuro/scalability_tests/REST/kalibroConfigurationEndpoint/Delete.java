package org.mezuro.scalability_tests.REST.kalibroConfigurationEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Delete extends RESTStrategy {

	private String configurationId;

	@Override
	public String beforeRequest() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> configuration = new HashMap<String, String>();
		configuration.put("name", "KalibroConfiguration");
		parameters.put("kalibro_configuration", configuration);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(KALIBRO_CONFIGURATION_PATH), jsonBody);
		configurationId = ((JSONObject) (response.getBody().getObject().get("kalibro_configuration"))).get("id").toString();
		return configurationId;
	}

	@Override
	public String request(String string) throws Exception {
		delete(buildUrl(KALIBRO_CONFIGURATION_PATH + "/" + configurationId));
		return string;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}
}
