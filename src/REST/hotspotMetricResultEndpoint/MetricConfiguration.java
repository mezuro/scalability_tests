package REST.hotspotMetricResultEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class MetricConfiguration extends RESTStrategy {

	private String repositoryId;
	private int hotspot_metric_result_id;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> repository = new HashMap<String, String>();
		parameters.put("repository", repository);
		repository.put("address", "https://github.com/mezuro/kalibro_client.git");
		repository.put("name", "name");
		repository.put("scm_type", "GIT");
		repository.put("kalibro_configuration_id", "2");
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
		HttpResponse<JsonNode> hotspot_metric_results = get(buildUrl(MODULE_RESULT_PATH + "/" + root_module_result_id + "/hotspot_metric_results"));
		hotspot_metric_result_id = hotspot_metric_results.getBody().getObject().getJSONArray("hotspot_metric_results").getJSONObject(0).getInt("id");
	}

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(HOTSPOT_METRIC_RESULT_PATH + "/" + hotspot_metric_result_id + "/metric_configuration"));
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
