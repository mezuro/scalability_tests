package projectEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class SOAPProjectExists extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private static WSClient projectClient;
	private Item requestResponse;
	private ItemImpl saveSOAPProject;

	public SOAPProjectExists() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveSOAPProject = new ItemImpl("saveSOAPProject");
		Item project = saveSOAPProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("SOAPProjectTest");
		requestResponse = projectClient.request("saveSOAPProject", saveSOAPProject);
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("projectExists", requestResponse.getContent("projectId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		wsClient.request("deleteSOAPProject", requestResponse.getContent("projectId"));
	}
}
