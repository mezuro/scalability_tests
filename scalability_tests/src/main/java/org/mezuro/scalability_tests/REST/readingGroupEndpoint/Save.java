package org.mezuro.scalability_tests.REST.readingGroupEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Save extends RESTStrategy {
	private String readingGroupId;
	private int append = 0;
	HashMap<String, String> readingGroup;

	@Override
	public String beforeRequest() throws Exception {
		readingGroup = new HashMap<String, String>();
		readingGroup.put("name", "ReadingGroup"+(append++));
		return null;
	}

	@Override
	public String request(String saveRepository) throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		parameters.put("reading_group", readingGroup);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(READING_GROUP_PATH), jsonBody);
		readingGroupId = ((JSONObject) (response.getBody().getObject().get("reading_group"))).get("id").toString();
		return readingGroupId;
	}

	@Override
	public void afterRequest(String requestRepositoryResponse) throws Exception {
		delete(buildUrl(READING_GROUP_PATH + "/" + readingGroupId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}
}
