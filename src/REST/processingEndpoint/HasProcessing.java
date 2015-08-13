package REST.processingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;
import strategy.RESTStrategy;

public class HasProcessing extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private static RSClient projectClient;
	private static RSClient repositoryClient;
	private Item requestProjectResponse;
	private Item requestRepositoryResponse;
	private ItemImpl saveRepository;
	private ItemImpl saveProject;

	public HasProcessing() throws Exception {
		projectClient = new RSClient(PROJECT_WSDL);
		repositoryClient = new RSClient(REPOSITORY_WSDL);
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
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("hasProcessing", requestRepositoryResponse.getContent("repositoryId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryClient.request("deleteRepository", requestRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
	}

}
