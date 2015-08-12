package SOAP.moduleResultEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class SOAPHistoryOfModule extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private final String PROCESSING_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProcessingEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private static WSClient processingClient;
	private Item requestSOAPProjectResponse;
	private Item requestSOAPRepositoryResponse;
	private Item requestSOAPProcessingResponse;
	private ItemImpl saveSOAPRepository;
	private ItemImpl saveSOAPProject;

	public SOAPHistoryOfModule() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
		repositoryClient = new WSClient(REPOSITORY_WSDL);
		processingClient = new WSClient(PROCESSING_WSDL);

	}

	@Override
	public void beforeExperiment() throws Exception {
		saveSOAPProject = new ItemImpl("saveSOAPProject");
		Item project = saveSOAPProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c123235");
		requestSOAPProjectResponse = projectClient.request("saveSOAPProject", saveSOAPProject);

		saveSOAPRepository = new ItemImpl("saveSOAPRepository");
		Item repository = saveSOAPRepository.addChild("repository");
		repository.addChild("id").setContent("");
		repository.addChild("address").setContent("svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.addChild("processPeriod").setContent("0");
		repository.addChild("description").setContent("desc");
		repository.addChild("name").setContent("name");
		repository.addChild("type").setContent("SUBVERSION");
		repository.addChild("license").setContent("GPL");
		repository.addChild("configurationId").setContent("1");
		saveSOAPRepository.addChild("projectId").setContent(requestSOAPProjectResponse.getContent("projectId"));
		requestSOAPRepositoryResponse = repositoryClient.request("saveSOAPRepository", saveSOAPRepository);

		repositoryClient.request("processSOAPRepository", requestSOAPRepositoryResponse.getContent("repositoryId"));

		Thread.sleep(10000);
		while (processingClient.request("lastSOAPProcessingState", requestSOAPRepositoryResponse.getContent("repositoryId"))
			.getContent("processState").endsWith("ING")) {
			Thread.sleep(10000);
		}

		requestSOAPProcessingResponse = processingClient.request("firstSOAPProcessing",
			requestSOAPRepositoryResponse.getContent("repositoryId"));
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("historyOfModule",
			requestSOAPProcessingResponse.getChild("processing").getContent("resultsRootId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryClient.request("deleteSOAPRepository", requestSOAPRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
	}

}
