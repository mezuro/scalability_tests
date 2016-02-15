package org.mezuro.scalability_tests.REST.metricConfigurationEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Update extends RESTStrategy {
	
	private String metricConfigurationId;
	private String configurationId;
	private HashMap<String, HashMap<String, Object>> body;
	
	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, Object>> parameters = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> configuration = new HashMap<String, Object>();
		configuration.put("name", "KalibroConfiguration");
		parameters.put("kalibro_configuration", configuration);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(KALIBRO_CONFIGURATION_PATH), jsonBody);
		configurationId = ((JSONObject) (response.getBody().getObject().get("kalibro_configuration"))).get("id").toString();
		
		parameters = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> metricConfiguration = new HashMap<String, Object>();
		HashMap<String, Object> metricSnapshot = new HashMap<String, Object>();
		HashMap<String, String> granularity = new HashMap<String, String>();
		metricConfiguration.put("aggregation_form", "MEAN");
		metricConfiguration.put("weight", "0.0");
		metricConfiguration.put("kalibro_configuration_id", configurationId.toString());
		
		metricSnapshot.put("name", "metric configuration");
		metricSnapshot.put("type", "HotspotMetricSnapshot");
		metricSnapshot.put("metric_collector_name", "MetricFu");
		metricSnapshot.put("code", "tst");
		
		granularity.put("type", "CLASS");
		
		metricSnapshot.put("scope", granularity);
		metricConfiguration.put("metric", metricSnapshot);
		parameters.put("metric_configuration", metricConfiguration);
		jsonBody = new JSONObject(parameters);
		response = post(buildUrl(METRIC_CONFIGURATION_PATH), jsonBody);

		metricConfigurationId = ((JSONObject) (response.getBody().getObject().get("metric_configuration"))).get("id").toString();
	}
	
	@Override
	public String beforeRequest() throws Exception {
		body = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> metricConfiguration = new HashMap<String, Object>();
		metricConfiguration.put("weight", "0.1");
		body.put("metric_configuration", metricConfiguration);
		return null;
	}

	@Override
	public String request(String string) throws Exception {
		JSONObject jsonBody = new JSONObject(body);
		put(buildUrl(METRIC_CONFIGURATION_PATH + "/" + metricConfigurationId), jsonBody);
		return null;
	}
	
	@Override
	public void afterRequest(String requestResponse) throws Exception {
		delete(buildUrl(METRIC_CONFIGURATION_PATH + "/" + metricConfigurationId));
	}


	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(KALIBRO_CONFIGURATION_PATH + "/" + configurationId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}
