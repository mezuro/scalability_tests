package org.mezuro.scalability_tests.REST.readingEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Update extends RESTStrategy{
	
	private String readingGroupId;
	private String readingId;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> readingGroup = new HashMap<String, String>();
		readingGroup.put("name", "ReadingGroup");
		parameters.put("reading_group", readingGroup);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(READING_GROUP_PATH), jsonBody);
		readingGroupId = ((JSONObject) (response.getBody().getObject().get("reading_group"))).get("id").toString();
		
		parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> reading = new HashMap<String, String>();
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
	public String request(String string) throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("grade", "1.0");
		JSONObject jsonBody = new JSONObject(parameters);
		put(buildUrl(READING_GROUP_PATH + "/" + readingGroupId + "/readings/" + readingId), jsonBody);
		return readingId;
	}
	
	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(READING_GROUP_PATH + "/" + readingGroupId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}
