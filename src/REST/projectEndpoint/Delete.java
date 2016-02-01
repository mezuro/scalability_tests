package REST.projectEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class Delete extends RESTStrategy {

	private String projectId;

	@Override
	public String beforeRequest() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> project = new HashMap<String, String>();
		project.put("name", "Project");
		parameters.put("project", project);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(PROJECT_PATH), jsonBody);
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();
		return projectId;
	}

	@Override
	public String request(String string) throws Exception {
		delete(buildUrl(PROJECT_PATH + "/" + projectId));
		return string;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
