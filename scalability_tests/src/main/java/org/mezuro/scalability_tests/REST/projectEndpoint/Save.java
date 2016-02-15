package org.mezuro.scalability_tests.REST.projectEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Save extends RESTStrategy {

	private String projectId;
	private int append = 0;
	HashMap<String, String> project;

	@Override
	public String beforeRequest() throws Exception {
		project = new HashMap<String, String>();
		project.put("name", "Project"+(append++));
		return null;
	}

	@Override
	public String request(String saveRepository) throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		parameters.put("project", project);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(PROJECT_PATH), jsonBody);
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();
		return projectId;
	}

	@Override
	public void afterRequest(String requestRepositoryResponse) throws Exception {
		delete(buildUrl(PROJECT_PATH + "/" + projectId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}
}
