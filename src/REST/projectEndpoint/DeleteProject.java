package REST.projectEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;
import strategy.RESTStrategy;

public class DeleteProject extends RESTStrategy {

	private final String PROJECT_WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private static RSClient projectClient;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteProject(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		projectClient = new RSClient(PROJECT_WSDL);
	}

	@Override
	public void beforeIteration() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveProject = new ItemImpl("saveProject");
			Item project = saveProject.addChild("project");
			project.addChild("id").setContent("");
			project.addChild("description").setContent("Description");
			project.addChild("name").setContent("d" + append++);
			idList.push(projectClient.request("saveProject", saveProject).getContent("projectId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("deleteProject", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteProjectResponse"))
			step++;
	}

	@Override
	public void afterIteration() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}