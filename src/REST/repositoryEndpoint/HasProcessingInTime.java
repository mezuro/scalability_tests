package REST.repositoryEndpoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import strategy.RESTStrategy;

public class HasProcessingInTime extends RESTStrategy {

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
	}

	@Override
	public String request(String string) throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("date", now());
		JSONObject jsonBody = new JSONObject(parameters);
		post(buildUrl(REPOSITORY_PATH + "/" + repositoryId + "/has_processing/before"), jsonBody);
		return repositoryId;
	}

	private String now() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		df.setTimeZone(tz);
		return df.format(new Date());
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
