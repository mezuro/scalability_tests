package configurationEndpoint;

import java.util.ArrayList;
import java.util.List;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveConfiguration extends Strategy {

	private int step = 0;
	private Integer append = 0;
	private List<Integer> errors;
	private List<String> idList;

	public SaveConfiguration() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
		Item saveConfiguration = new ItemImpl("saveConfiguration");
		Item project = saveConfiguration.addChild("configuration");
		project.addChild("id").setContent("");
		project.addChild("description").setContent("Description");
		project.addChild("name").setContent("c" + append++);
		return saveConfiguration;
	}

	@Override
	public Item request(Item saveConfiguration) throws Exception {
		return wsClient.request("saveConfiguration", saveConfiguration);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("configurationId"));
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
			wsClient.request("deleteConfiguration", id);
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
