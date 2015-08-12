package SOAP.repositoryEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class DeleteSOAPRepository extends SOAPStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private static WSClient projectClient;
	private static WSClient repositoryClient;
	private Item requestSOAPProjectResponse;
	private ItemImpl saveSOAPProject;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteSOAPRepository(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		projectClient = new WSClient(PROJECT_WSDL);
		errors = new ArrayList<Integer>();
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
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveSOAPRepository = new ItemImpl("saveSOAPRepository");
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
			idList.push(repositoryClient.request("saveSOAPRepository", saveSOAPRepository).getContent("repositoryId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteSOAPRepository", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteSOAPRepositoryResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		projectClient.request("deleteSOAPProject", requestSOAPProjectResponse.getContent("projectId"));
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}
