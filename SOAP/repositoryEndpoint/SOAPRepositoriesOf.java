package repositoryEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class SOAPRepositoriesOf extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private static WSClient projectClient;
	private Item requestSOAPProjectResponse;
	private ItemImpl saveSOAPProject;

	@Override
	public void beforeExperiment() throws Exception {
		saveSOAPProject = new ItemImpl("saveSOAPProject");
		Item project = saveSOAPProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c123235");
		requestSOAPProjectResponse = projectClient.request("saveSOAPProject", saveSOAPProject);
	}

	public SOAPRepositoriesOf() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("SOAPRepositoriesOf", requestSOAPProjectResponse.getContent("projectId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
	}

}
