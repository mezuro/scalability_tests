package org.mezuro.scalability_tests.SOAP.repositoryEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class CancelSOAPProcessingOfSOAPRepository extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private Item requestSOAPProjectResponse;
	private Item requestSOAPRepositoryResponse;
	private ItemImpl saveSOAPRepository;
	private ItemImpl saveSOAPProject;

	public CancelSOAPProcessingOfSOAPRepository() throws Exception {
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
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("cancelSOAPProcessingOfSOAPRepository", requestSOAPRepositoryResponse.getContent("repositoryId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		wsClient.request("deleteSOAPRepository", requestSOAPRepositoryResponse.getContent("repositoryId"));
		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
	}

}
