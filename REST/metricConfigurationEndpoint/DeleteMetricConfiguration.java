package metricConfigurationEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class DeleteMetricConfiguration extends RESTStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/MetricConfigurationEndpoint/?wsdl";
	private static RSClient metricConfigurationClient;
	private int step = 0;
	private int requestsPerStep;
	private Stack<String> idList;
	private List<Integer> errors;
	private int append = 0;

	public DeleteMetricConfiguration(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		metricConfigurationClient = new RSClient(METRIC_CONFIGURATION_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveMetricConfiguration = new ItemImpl("saveMetricConfiguration");
			Item metricConfiguration = saveMetricConfiguration.addChild("metricConfiguration");
			metricConfiguration.addChild("baseToolName").setContent("Analizo");
			metricConfiguration.addChild("id").setContent("");
			metricConfiguration.addChild("aggregationForm").setContent("AVERAGE");
			metricConfiguration.addChild("weight").setContent("10");
			metricConfiguration.addChild("readingGroupId").setContent("1");
			Item metric = metricConfiguration.addChild("metric");
			metric.addChild("scope").setContent("CLASS");
			metric.addChild("compound").setContent("FALSE");
			metric.addChild("description").setContent("");
			metric.addChild("name").setContent("Lines of Code");
			metric.addChild("language").setContent("C");
			metric.addChild("script").setContent("");
			metricConfiguration.addChild("code").setContent("jtp" + append++);
			saveMetricConfiguration.addChild("configurationId").setContent("1");
			idList.push(metricConfigurationClient.request("saveMetricConfiguration", saveMetricConfiguration)
				.getContent(
					"metricConfigurationId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("deleteMetricConfiguration", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteMetricConfigurationResponse"))
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
