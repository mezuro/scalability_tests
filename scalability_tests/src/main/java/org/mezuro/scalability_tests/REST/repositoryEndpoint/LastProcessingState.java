package org.mezuro.scalability_tests.REST.repositoryEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class LastProcessingState extends RESTStrategy {

	private String repositoryId;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> repository = new HashMap<String, String>();
		parameters.put("repository", repository);
		repository.put("address", "svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.put("name", "name");
		repository.put("scm_type", "SVN");
		repository.put("kalibro_configuration_id", "1");
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(REPOSITORY_PATH), jsonBody);
		repositoryId = ((JSONObject) (response.getBody().getObject().get("repository"))).get("id").toString();

		get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/process"));
	}

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/last_processing_state"));
		return repositoryId;
	}

	@Override
	public void afterExperiment() throws Exception {
		get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/cancel_process"));
		delete(buildUrl(REPOSITORY_PATH + "/" + repositoryId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
