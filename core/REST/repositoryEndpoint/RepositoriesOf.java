package REST.repositoryEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class RepositoriesOf extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private static RSClient projectClient;
	private Item requestProjectResponse;
	private ItemImpl saveProject;

	@Override
	public void beforeExperiment() throws Exception {
		saveProject = new ItemImpl("saveProject");
		Item project = saveProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c123235");
		requestProjectResponse = projectClient.request("saveProject", saveProject);
	}

	public RepositoriesOf() throws Exception {
		projectClient = new RSClient(PROJECT_WSDL);
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("repositoriesOf", requestProjectResponse.getContent("projectId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
	}

}
