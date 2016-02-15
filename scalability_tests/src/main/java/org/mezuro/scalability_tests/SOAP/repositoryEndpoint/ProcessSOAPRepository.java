package org.mezuro.scalability_tests.SOAP.repositoryEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class ProcessSOAPRepository extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://localhost:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://localhost:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private final String PROCESSING_WSDL = "http://localhost:8080/KalibroService/SOAPProcessingEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private static WSClient processingClient;
	private Item requestSOAPProjectResponse;
	private Item requestSOAPRepositoryResponse;
	private ItemImpl saveSOAPRepository;
	private ItemImpl saveSOAPProject;
	private int append = 0;

	public ProcessSOAPRepository() throws Exception {
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
	}

	@Override
	public Item beforeRequest() throws Exception {
		saveSOAPRepository = new ItemImpl("saveSOAPRepository");
		Item repository = saveSOAPRepository.addChild("repository");
		repository.addChild("id").setContent("");
		repository.addChild("address").setContent("svn://svn.code.sf.net/p/qt-calculator/code/trunk");
		repository.addChild("processPeriod").setContent("0");
		repository.addChild("description").setContent("desc");
		repository.addChild("name").setContent("name" + append++);
		repository.addChild("type").setContent("SUBVERSION");
		repository.addChild("license").setContent("GPL");
		repository.addChild("configurationId").setContent("1");
		saveSOAPRepository.addChild("projectId").setContent(requestSOAPProjectResponse.getContent("projectId"));
		requestSOAPRepositoryResponse = repositoryClient.request("saveSOAPRepository", saveSOAPRepository);
		return requestSOAPRepositoryResponse;
	}

	@Override
	public Item request(Item item) throws Exception {
		wsClient.request("processSOAPRepository", item.getContent("repositoryId"));
		return item;
	}

//	@Override
//	public void afterRequest(Item requestResponse) throws Exception {
//		repositoryClient.request("cancelSOAPProcessingOfSOAPRepository", requestResponse.getContent("repositoryId"));
//	}

	@Override
	public synchronized void afterRequest(Item requestResponse) throws Exception {
		Thread.sleep(1000);
		while (processingClient.request("hasReadySOAPProcessing", requestResponse.getContent("repositoryId"))
			.getContent("exists").equals("false")) {
			Thread.sleep(5000);
		}
	}

//	@Override
//	public void afterExperiment() throws Exception {
//		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
//	}

}
