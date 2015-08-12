package REST.projectEndpoint;

import java.util.ArrayList;
import java.util.List;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveProject extends RESTStrategy {

	private Integer append = 0;
	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveProject() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveProject = new ItemImpl("saveProject");
		Item project = saveProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("p" + append++);
		return saveProject;
	}

	@Override
	public Item request(Item saveProject) throws Exception {
		return rsClient.request("saveProject", saveProject);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("projectId"));
		} catch (NoSuchFieldException e) {
			step++;
		}
	}

	@Override
	public void beforeStep() {
		idList.clear();
	}

	@Override
	public void afterStep() throws InvalidOperationNameException, FrameworkException {
		for (String id : idList) {
			rsClient.request("deleteProject", id);
		}
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		int cont = 0;
		for (int step_errors : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + step_errors);
		}
	}
}
