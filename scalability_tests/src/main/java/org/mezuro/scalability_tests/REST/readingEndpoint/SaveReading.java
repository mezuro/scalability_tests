package org.mezuro.scalability_tests.REST.readingEndpoint;

import java.util.ArrayList;
import java.util.List;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class SaveReading extends RESTStrategy {

	private int step = 0;
	private Integer append = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveReading() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveReading = new ItemImpl("saveReading");
		saveReading.addChild("groupId").setContent("1");
		Item reading = saveReading.addChild("reading");
		reading.addChild("id").setContent("");
		reading.addChild("color").setContent("161212");
		reading.addChild("grade").setContent("5");
		reading.addChild("label").setContent("a" + append++);
		return saveReading;
	}

	@Override
	public Item request(Item saveReading) throws Exception {
		return rsClient.request("saveReading", saveReading);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("readingId"));
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
			rsClient.request("deleteReading", id);
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
