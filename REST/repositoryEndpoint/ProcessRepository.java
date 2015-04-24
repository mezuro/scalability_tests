package repositoryEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class ProcessRepository extends RESTStrategy {

	private final String PROJECT_WSDL = "http://localhost:8080/KalibroService/ProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://localhost:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private final String PROCESSING_WSDL = "http://localhost:8080/KalibroService/ProcessingEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private static WSClient processingClient;
	private Item requestProjectResponse;
	private Item requestRepositoryResponse;
	private ItemImpl saveRepository;
	private ItemImpl saveProject;
	private int append = 0;

	public ProcessRepository() throws Exception {
		projectClient = new WSClient(PROJECT_WSDL);
		repositoryClient = new WSClient(REPOSITORY_WSDL);
		processingClient = new WSClient(PROCESSING_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveProject = new ItemImpl("saveProject");
		Item project = saveProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c123235");
		requestProjectResponse = projectClient.request("saveProject", saveProject);
	}

	@Override
	public Item beforeRequest() throws Exception {
		saveRepository = new ItemImpl("saveRepository");
		Item repository = saveRepository.addChild("repository");
		repository.addChild("id").setContent("");
		repository.addChild("address").setContent("svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.addChild("processPeriod").setContent("0");
		repository.addChild("description").setContent("desc");
		repository.addChild("name").setContent("name" + append++);
		repository.addChild("type").setContent("SUBVERSION");
		repository.addChild("license").setContent("GPL");
		repository.addChild("configurationId").setContent("1");
		saveRepository.addChild("projectId").setContent(requestProjectResponse.getContent("projectId"));
		requestRepositoryResponse = repositoryClient.request("saveRepository", saveRepository);
		return requestRepositoryResponse;
	}

	@Override
	public Item request(Item item) throws Exception {
		wsClient.request("processRepository", item.getContent("repositoryId"));
		return item;
	}

//	@Override
//	public void afterRequest(Item requestResponse) throws Exception {
//		repositoryClient.request("cancelProcessingOfRepository", requestResponse.getContent("repositoryId"));
//	}

	@Override
	public synchronized void afterRequest(Item requestResponse) throws Exception {
		Thread.sleep(1000);
		while (processingClient.request("hasReadyProcessing", requestResponse.getContent("repositoryId"))
			.getContent("exists").equals("false")) {
			Thread.sleep(5000);
		}
	}

//	@Override
//	public void afterExperiment() throws Exception {
//		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
//	}

}
