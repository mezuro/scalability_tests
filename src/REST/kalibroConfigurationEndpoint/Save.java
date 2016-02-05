package REST.kalibroConfigurationEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class Save extends RESTStrategy {

	private String configurationId;
	HashMap<String, String> configuration;

	@Override
	public String beforeRequest() throws Exception {
		configuration = new HashMap<String, String>();
		configuration.put("name", "KalibroConfigurations");
		return null;
	}

	@Override
	public String request(String saveRepository) throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		parameters.put("kalibro_configuration", configuration);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(KALIBRO_CONFIGURATION_PATH), jsonBody);
		configurationId = ((JSONObject) (response.getBody().getObject().get("kalibro_configuration"))).get("id").toString();
		return configurationId;
	}

	@Override
	public void afterRequest(String requestRepositoryResponse) throws Exception {
		delete(buildUrl(KALIBRO_CONFIGURATION_PATH + "/" + configurationId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}
}
