package repositoryEndpoint;

import org.json.JSONObject;

import support.RESTStrategy;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class ProcessRepository extends RESTStrategy {

	private final String BASE_URI = "http://localhost:";
	private final String PORT = "8082";
	private final String PROJECT_PATH = "/projects";
	private final String REPOSITORY_PATH = "/repositories";
	private String projectId;
	private String repositoryId;
	private int append = 0;

	@Override
	public void beforeExperiment() throws Exception {
		String body = "{\"project\":{\"name\":\"Project\"}}";
		HttpResponse<JsonNode> response = Unirest.post(BASE_URI + PORT + PROJECT_PATH)
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .body(body)
				  .asJson();
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();
	}

	@Override
	public String beforeRequest() throws Exception {
		String body = "{\"repository\":{\"name\":\"Repository"+(append++)+"\","
					+ " \"address\":\"svn://svn.code.sf.net/p/qt-calculator/code/trunk\","
					+ "\"scm_type\":\"SVN\","
					+ "\"kalibro_configuration_id\":\"1\","
					+ "\"project_id\":\""+projectId+"\"}}";
		HttpResponse<JsonNode> response = Unirest.post(BASE_URI + PORT + REPOSITORY_PATH)
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .body(body)
				  .asJson();
		repositoryId = ((JSONObject) (response.getBody().getObject().get("repository"))).get("id").toString();
		return repositoryId;
	}
//
	@Override
	public String request(String string) throws Exception {
		Unirest.get(BASE_URI + PORT + REPOSITORY_PATH + "/" + repositoryId + "/process")
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .asJson();
		return string;
	}

	@Override
	public synchronized void afterRequest(String requestResponse) throws Exception {
		Thread.sleep(1000);
		while (Unirest.get(BASE_URI + PORT + REPOSITORY_PATH + "/" + repositoryId + "/has_ready_processing")
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .asJson().getBody().getObject().get("has_ready_processing").equals("false")) {
			Thread.sleep(1000);
		}
	}

	@Override
	public void afterExperiment() throws Exception {
		Unirest.delete(BASE_URI + PORT + PROJECT_PATH + "/" + projectId)
		  .header("Content-Type", "application/json")
		  .header("accept", "application/json")
		  .asJson();
	}

}
