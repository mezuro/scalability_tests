package org.mezuro.scalability_tests.REST.rangeEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class Delete extends RESTStrategy {

	private String configurationId;
	private String metricConfigurationId;
	private Object readingId;
	private String readingGroupId;
	private String rangeId;

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
		
		parameters.clear();
		HashMap<String, Object> readingGroup = new HashMap<String, Object>();
		readingGroup.put("name", "ReadingGroup");
		parameters.put("reading_group", readingGroup);
		jsonBody = new JSONObject(parameters);
		response = post(buildUrl(READING_GROUP_PATH), jsonBody);
		readingGroupId = ((JSONObject) (response.getBody().getObject().get("reading_group"))).get("id").toString();
		
		parameters.clear();
		HashMap<String, Object> reading = new HashMap<String, Object>();
		reading.put("label", "label");
		reading.put("color", "FF0000");
		reading.put("grade", "0.0");
		reading.put("reading_group_id", readingGroupId.toString());
		parameters.put("reading", reading);
		jsonBody = new JSONObject(parameters);
		response = post(buildUrl(READING_GROUP_PATH + "/" + readingGroupId + "/readings"), jsonBody);
		
		readingId = ((JSONObject) (response.getBody().getObject().get("reading"))).get("id").toString();
	}
	
	@Override
	public String beforeRequest() throws Exception {
		HashMap<String, HashMap<String, Object>> parameters = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> range;
		
		range = new HashMap<String, Object>();
		range.put("beginning", "0.0");
		range.put("end", "1.0");
		range.put("metric_configuration_id", metricConfigurationId.toString());
		range.put("reading_id", readingId.toString());
		parameters.put("kalibro_range", range);

		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(METRIC_CONFIGURATION_PATH + "/" + metricConfigurationId + "/" + KALIBRO_RANGE_PATH), jsonBody);
		
		rangeId = ((JSONObject) (response.getBody().getObject().get("kalibro_range"))).get("id").toString();
		return null;
	}

	@Override
	public String request(String string) throws Exception {
		delete(buildUrl(METRIC_CONFIGURATION_PATH + "/" + metricConfigurationId + "/" + KALIBRO_RANGE_PATH + "/" + rangeId));
		return null;
	}
	
	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(KALIBRO_CONFIGURATION_PATH + "/" + configurationId));
		delete(buildUrl(READING_GROUP_PATH + "/" + readingGroupId));
	}
	
	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}
}
