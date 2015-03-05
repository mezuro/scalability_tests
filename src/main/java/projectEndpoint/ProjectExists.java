package projectEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class ProjectExists extends Strategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private static WSClient projectClient;
	private Item requestResponse;
	private ItemImpl saveProject;

	public ProjectExists() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
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
		return wsClient.request("projectExists", requestResponse.getContent("projectId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		wsClient.request("deleteProject", requestResponse.getContent("projectId"));
	}
}
