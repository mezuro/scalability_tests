package org.mezuro.scalability_tests.REST.projectEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Update extends RESTStrategy {
	
	private String projectId;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> project = new HashMap<String, String>();
		project.put("name", "Project");
		parameters.put("project", project);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(PROJECT_PATH), jsonBody);
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();
	}

	@Override
	public String request(String string) throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();		
		parameters.put("description", "New description");
		put(buildUrl(PROJECT_PATH + "/" + projectId), new JSONObject(parameters));
		return null;
	}
	
	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(PROJECT_PATH + "/" + projectId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
