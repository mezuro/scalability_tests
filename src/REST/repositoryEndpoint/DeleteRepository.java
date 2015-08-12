package REST.repositoryEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class DeleteRepository extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private final String REPOSITORY_WSDL = "http://10.0.0.12:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private static RSClient projectClient;
	private static RSClient repositoryClient;
	private Item requestProjectResponse;
	private ItemImpl saveProject;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteRepository(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		projectClient = new RSClient(PROJECT_WSDL);
		errors = new ArrayList<Integer>();
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
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveRepository = new ItemImpl("saveRepository");
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
			idList.push(repositoryClient.request("saveRepository", saveRepository).getContent("repositoryId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("deleteRepository", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteRepositoryResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		projectClient.request("deleteProject", requestProjectResponse.getContent("projectId"));
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}
