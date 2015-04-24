package readingGroupEndpoint;

import java.util.ArrayList;
import java.util.List;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

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
		return wsClient.request("saveReadingGroup", saveReadingGroup);
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
			wsClient.request("deleteReadingGroup", id);
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
