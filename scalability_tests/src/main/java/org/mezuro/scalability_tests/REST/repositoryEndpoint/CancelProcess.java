package org.mezuro.scalability_tests.REST.repositoryEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class CancelProcess extends RESTStrategy {

	private String projectId;
	private String repositoryId;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> project = new HashMap<String, String>();
		project.put("name", "Project");
		parameters.put("project", project);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(PROJECT_PATH), jsonBody);
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();

		parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> repository = new HashMap<String, String>();
		repository.put("name", "Repository");
		repository.put("address", "svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.put("scm_type", "SVN");
		repository.put("kalibro_configuration_id", "1");
		repository.put("project_id", projectId.toString());
		parameters.put("repository", repository);
		jsonBody = new JSONObject(parameters);
		response = post(buildUrl(REPOSITORY_PATH), jsonBody);

		repositoryId = ((JSONObject) (response.getBody().getObject().get("repository"))).get("id").toString();
	}
	
	@Override
	public String beforeRequest() throws Exception {
		get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/process"));
		return repositoryId;
	}

	
	@Override
	public String request(String string) throws Exception {
		get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/cancel_process"));
		return string;
	}

	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(REPOSITORY_PATH + "/" + repositoryId));
		delete(buildUrl(PROJECT_PATH + "/" + projectId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}
}
