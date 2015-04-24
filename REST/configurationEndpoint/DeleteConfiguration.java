package configurationEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class DeleteConfiguration extends RESTStrategy {

	private final String CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/ConfigurationEndpoint/?wsdl";
	private static WSClient configurationClient;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteConfiguration(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		configurationClient = new WSClient(CONFIGURATION_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveConfiguration = new ItemImpl("saveConfiguration");
			Item configuration = saveConfiguration.addChild("configuration");
			configuration.addChild("id").setContent("");
			configuration.addChild("description").setContent("desc");
			configuration.addChild("name").setContent("cd" + append++);
			idList.push(configurationClient.request("saveConfiguration", saveConfiguration).getContent(
				"configurationId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteConfiguration", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteConfigurationResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
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
