package REST.readingEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class Save extends RESTStrategy {

	private String readingGroupId;
	private int append = 0;
	private HashMap<String, String> reading;
	private String readingId;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> readingGroup = new HashMap<String, String>();
		readingGroup.put("name", "ReadingGroup");
		parameters.put("reading_group", readingGroup);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(READING_GROUP_PATH), jsonBody);
		readingGroupId = ((JSONObject) (response.getBody().getObject().get("reading_group"))).get("id").toString();
		
		reading = new HashMap<String, String>();
		reading.put("label", "label"+(append++));
		reading.put("color", "FF0000");
		reading.put("grade", "0.0");
		reading.put("reading_group_id", readingGroupId.toString());	
	}
	
	@Override
	public String request(String string) throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		parameters.put("reading", reading);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = post(buildUrl(READING_GROUP_PATH + "/" + readingGroupId + "/readings"), jsonBody);
		
		readingId = ((JSONObject) (response.getBody().getObject().get("reading"))).get("id").toString();
		return readingId;
	}
	
	@Override
	public void afterRequest(String requestResponse) throws Exception {
		delete(buildUrl(READING_GROUP_PATH + "/" + readingGroupId + "/readings/" + readingId));
	}
	
	@Override
	public void afterExperiment() throws Exception {
		delete(buildUrl(READING_GROUP_PATH + "/" + readingGroupId));
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}
