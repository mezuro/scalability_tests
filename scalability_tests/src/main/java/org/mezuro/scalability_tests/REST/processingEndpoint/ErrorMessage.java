package org.mezuro.scalability_tests.REST.processingEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class ErrorMessage extends RESTStrategy {
	
	private int processing_id;
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

		boolean hasReadyProcessing;
		do {
			Thread.sleep(1000);
			hasReadyProcessing = get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/has_ready_processing"))
					.getBody().getObject().getBoolean("has_ready_processing");
		} while(!hasReadyProcessing);
		
		HttpResponse<JsonNode> last_processing = get(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/last_ready_processing"));
		processing_id = last_processing.getBody().getObject().getJSONObject("last_ready_processing").getInt("id");
	}

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(PROCESSING_PATH + "/" + processing_id + "/error_message"));
		return null;
	}
	
	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(REPOSITORY_PATH + "/" + repositoryId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}
}
