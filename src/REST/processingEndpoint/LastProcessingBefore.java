package REST.processingEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class LastProcessingBefore extends RESTStrategy {

	private static final String AFTER_DATE = "2030-09-19T12:56:54.364Z";
	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private final String PROCESSING_WSDL = "http://10.0.0.12:8080/KalibroService/ProcessingEndpoint/?wsdl";
	private static RSClient projectClient;
	private static RSClient repositoryClient;
	private RSClient processingClient;
	private Item requestProjectResponse;
	private Item requestRepositoryResponse;
	private ItemImpl saveRepository;
	private ItemImpl saveProject;
	private ItemImpl lastProcessingBefore;

	public LastProcessingBefore() throws Exception {
		projectClient = new RSClient(PROJECT_WSDL);
		repositoryClient = new RSClient(REPOSITORY_WSDL);
		processingClient = new RSClient(PROCESSING_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveProject = new ItemImpl("saveProject");
		Item project = saveProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c123235");
		requestProjectResponse = projectClient.request("saveProject", saveProject);

		saveRepository = new ItemImpl("saveRepository");
		Item repository = saveRepository.addChild("repository");
		repository.addChild("id").setContent("");
		repository.addChild("address").setContent("svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.addChild("processPeriod").setContent("0");
		repository.addChild("description").setContent("desc");
		repository.addChild("name").setContent("name");
		repository.addChild("type").setContent("SUBVERSION");
		repository.addChild("license").setContent("GPL");
		repository.addChild("configurationId").setContent("1");
		saveRepository.addChild("projectId").setContent(requestProjectResponse.getContent("projectId"));
		requestRepositoryResponse = repositoryClient.request("saveRepository", saveRepository);

		repositoryClient.request("processRepository", requestRepositoryResponse.getContent("repositoryId"));

		Thread.sleep(1000);
		while (processingClient.request("lastProcessingState", requestRepositoryResponse.getContent("repositoryId"))
			.getContent("processState").endsWith("ING")) {
			Thread.sleep(10000);
		}

		lastProcessingBefore = new ItemImpl("lastProcessingBefore");
		lastProcessingBefore.addChild("date").setContent(AFTER_DATE);
		lastProcessingBefore.addChild("repositoryId").setContent(requestRepositoryResponse.getContent("repositoryId"));
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("lastProcessingBefore", lastProcessingBefore);
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryClient.request("deleteRepository", requestRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
	}

}
