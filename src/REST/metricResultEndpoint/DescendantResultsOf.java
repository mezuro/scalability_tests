package REST.metricResultEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;
import strategy.RESTStrategy;

public class DescendantResultsOf extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private final String PROCESSING_WSDL = "http://10.0.0.12:8080/KalibroService/ProcessingEndpoint/?wsdl";
	private final String METRIC_RESULT_WSDL = "http://10.0.0.12:8080/KalibroService/MetricResultEndpoint/?wsdl";
	private static RSClient projectClient;
	private static RSClient repositoryClient;
	private static RSClient processingClient;
	private static RSClient metricResultClient;
	private Item requestProjectResponse;
	private Item requestRepositoryResponse;
	private Item requestProcessingResponse;
	private Item requestMetricResultResponse;
	private ItemImpl saveRepository;
	private ItemImpl saveProject;

	public DescendantResultsOf() throws Exception {
		projectClient = new RSClient(PROJECT_WSDL);
		repositoryClient = new RSClient(REPOSITORY_WSDL);
		processingClient = new RSClient(PROCESSING_WSDL);
		metricResultClient = new RSClient(METRIC_RESULT_WSDL);
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

		Thread.sleep(10000);
		while (processingClient.request("lastProcessingState", requestRepositoryResponse.getContent("repositoryId"))
			.getContent("processState").endsWith("ING")) {
			Thread.sleep(10000);
		}

		requestProcessingResponse = processingClient.request("firstProcessing",
			requestRepositoryResponse.getContent("repositoryId"));

		requestMetricResultResponse = metricResultClient.request("metricResultsOf",
			requestProcessingResponse.getChild("processing").getContent("resultsRootId"));
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("descendantResultsOf",
			requestMetricResultResponse.getChild("metricResult").getContent("id"));
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryClient.request("deleteRepository", requestRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
	}

}
