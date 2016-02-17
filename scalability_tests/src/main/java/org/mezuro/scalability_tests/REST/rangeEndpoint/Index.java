package org.mezuro.scalability_tests.REST.rangeEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class Index extends RESTStrategy {

	private String configurationId;
	private String metricConfigurationId;

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
	public String request(String string) throws Exception {
		get(buildUrl(METRIC_CONFIGURATION_PATH + "/" + metricConfigurationId + "/" + KALIBRO_RANGE_PATH));
		return null;
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
