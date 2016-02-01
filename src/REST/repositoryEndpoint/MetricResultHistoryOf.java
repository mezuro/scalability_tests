package REST.repositoryEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class MetricResultHistoryOf extends RESTStrategy {

	private String repositoryId;
	private int kalibro_module_id;

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
		int root_module_result_id = last_processing.getBody().getObject().getJSONObject("last_ready_processing").getInt("root_module_result_id");
		kalibro_module_id = get(buildUrl(MODULE_RESULT_PATH + "/" + root_module_result_id + "/kalibro_module")).getBody().getObject().getJSONObject("kalibro_module").getInt("id");
	}

	@Override
	public String request(String string) throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", repositoryId);
		parameters.put("kalibro_module_id", String.valueOf(kalibro_module_id));
		parameters.put("metric_name", "Average Method Lines of Code");
		JSONObject jsonParameters = new JSONObject(parameters);
		post(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/metric_result_history_of"), jsonParameters);
		return repositoryId;
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
