package org.mezuro.scalability_tests.REST.readingGroupEndpoint;

import java.util.ArrayList;
import java.util.List;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class SaveReadingGroup extends RESTStrategy {

	private int step = 0;
	private Integer append = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveReadingGroup() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveReadingGroup = new ItemImpl("saveReadingGroup");
		Item readingGroup = saveReadingGroup.addChild("readingGroup");
		readingGroup.addChild("id").setContent("");
		readingGroup.addChild("description").setContent("Description");
		readingGroup.addChild("name").setContent("c" + append++);
		return saveReadingGroup;
	}

	@Override
	public Item request(Item saveReadingGroup) throws Exception {
		return rsClient.request("saveReadingGroup", saveReadingGroup);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("readingGroupId"));
		} catch (NoSuchFieldException e) {
			step++;
		}
	}

	@Override
	public void beforeIteration() {
		idList.clear();
	}

	@Override
	public void afterIteration() throws InvalidOperationNameException, FrameworkException {
		for (String id : idList) {
			rsClient.request("deleteReadingGroup", id);
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