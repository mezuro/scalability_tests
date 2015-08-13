package REST.projectEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;
import strategy.RESTStrategy;

public class GetProject extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private static RSClient projectClient;
	private Item requestResponse;
	private ItemImpl saveProject;

	public GetProject() throws Exception {
		projectClient = new RSClient(PROJECT_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveProject = new ItemImpl("saveProject");
		Item project = saveProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("ProjectTest");
		requestResponse = projectClient.request("saveProject", saveProject);
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getProject", requestResponse.getContent("projectId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		rsClient.request("deleteProject", requestResponse.getContent("projectId"));
	}

}
