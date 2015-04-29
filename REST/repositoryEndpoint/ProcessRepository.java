package repositoryEndpoint;

import java.util.HashMap;

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
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> project = new HashMap<String, String>();
		project.put("name", "Project");
		parameters.put("project", project);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = Unirest.post(BASE_URI + PORT + PROJECT_PATH)
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .body(jsonBody.toString())
				  .asJson();
		projectId = ((JSONObject) (response.getBody().getObject().get("project"))).get("id").toString();
	}

	@Override
	public String beforeRequest() throws Exception {
		HashMap<String, HashMap<String, String>> parameters = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> repository = new HashMap<String, String>();
		repository.put("name", "Repository"+(append++));
		repository.put("address", "svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.put("scm_type", "SVN");
		repository.put("kalibro_configuration_id", "1");
		repository.put("project_id", projectId.toString());
		parameters.put("repository", repository);
		JSONObject jsonBody = new JSONObject(parameters);
		HttpResponse<JsonNode> response = Unirest.post(BASE_URI + PORT + REPOSITORY_PATH)
				  .header("Content-Type", "application/json")
				  .header("accept", "application/json")
				  .body(jsonBody.toString())
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
