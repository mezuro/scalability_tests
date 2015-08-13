package SOAP.processingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import strategy.SOAPStrategy;

public class HasSOAPProcessingBefore extends SOAPStrategy {

	private static final String AFTER_DATE = "2030-09-19T12:56:54.364Z";
	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private Item requestSOAPProjectResponse;
	private Item requestSOAPRepositoryResponse;
	private ItemImpl saveSOAPRepository;
	private ItemImpl saveSOAPProject;
	private ItemImpl hasSOAPProcessingBefore;

	public HasSOAPProcessingBefore() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
		repositoryClient = new WSClient(REPOSITORY_WSDL);
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

		hasSOAPProcessingBefore = new ItemImpl("hasSOAPProcessingBefore");
		hasSOAPProcessingBefore.addChild("date").setContent(AFTER_DATE);
		hasSOAPProcessingBefore.addChild("repositoryId").setContent(requestSOAPRepositoryResponse.getContent("repositoryId"));
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("hasSOAPProcessingBefore", hasSOAPProcessingBefore);
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryClient.request("deleteSOAPRepository", requestSOAPRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
	}

}
