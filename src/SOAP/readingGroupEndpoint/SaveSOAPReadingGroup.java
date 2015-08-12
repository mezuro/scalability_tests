package SOAP.readingGroupEndpoint;

import java.util.ArrayList;
import java.util.List;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveSOAPReadingGroup extends SOAPStrategy {

	private int step = 0;
	private Integer append = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveSOAPReadingGroup() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveSOAPReadingGroup = new ItemImpl("saveSOAPReadingGroup");
		Item readingGroup = saveSOAPReadingGroup.addChild("readingGroup");
		readingGroup.addChild("id").setContent("");
		readingGroup.addChild("description").setContent("Description");
		readingGroup.addChild("name").setContent("c" + append++);
		return saveSOAPReadingGroup;
	}

	@Override
	public Item request(Item saveSOAPReadingGroup) throws Exception {
		return wsClient.request("saveSOAPReadingGroup", saveSOAPReadingGroup);
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
	public void beforeStep() {
		idList.clear();
	}

	@Override
	public void afterStep() throws InvalidOperationNameException, FrameworkException {
		for (String id : idList) {
			wsClient.request("deleteSOAPReadingGroup", id);
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
