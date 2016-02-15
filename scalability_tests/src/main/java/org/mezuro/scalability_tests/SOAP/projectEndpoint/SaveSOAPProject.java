package org.mezuro.scalability_tests.SOAP.projectEndpoint;

import java.util.ArrayList;
import java.util.List;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SaveSOAPProject extends SOAPStrategy {

	private Integer append = 0;
	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveSOAPProject() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveSOAPProject = new ItemImpl("saveSOAPProject");
		Item project = saveSOAPProject.addChild("project");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("p" + append++);
		return saveSOAPProject;
	}

	@Override
	public Item request(Item saveSOAPProject) throws Exception {
		return wsClient.request("saveSOAPProject", saveSOAPProject);
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
			wsClient.request("deleteSOAPProject", id);
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
