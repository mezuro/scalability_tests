package rangeEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class DeleteRange extends RESTStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/MetricConfigurationEndpoint/?wsdl";
	private final String RANGE_WSDL = "http://10.0.0.12:8080/KalibroService/RangeEndpoint/?wsdl";
	private static RSClient metricConfigurationClient;
	private static RSClient rangeClient;
	private ItemImpl saveMetricConfiguration;
	private Item metricConfigurationResponse;
	private int requestsPerStep;
	private Stack<String> idList;
	private int step = 0;
	private List<Integer> errors;
	private Integer append = 0;

	public DeleteRange(int requestsPerStep) throws Exception {
		metricConfigurationClient = new RSClient(METRIC_CONFIGURATION_WSDL);
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		rangeClient = new RSClient(RANGE_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveMetricConfiguration = new ItemImpl("saveMetricConfiguration");
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
		metricConfiguration.addChild("code").setContent("jtp");
		saveMetricConfiguration.addChild("configurationId").setContent("1");
		metricConfigurationResponse = metricConfigurationClient.request("saveMetricConfiguration",
			saveMetricConfiguration);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveRange = new ItemImpl("saveRange");
			Item range = saveRange.addChild("range");
			range.addChild("id").setContent("");
			range.addChild("beginning").setContent(append.toString());
			append++;
			range.addChild("readingId").setContent("1");
			range.addChild("comments").setContent("Comment");
			range.addChild("end").setContent(append.toString());
			saveRange.addChild("metricConfigurationId").setContent(
				metricConfigurationResponse.getContent("metricConfigurationId"));
			idList.push(rangeClient.request("saveRange", saveRange).getContent("rangeId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("deleteRange", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteRangeResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		metricConfigurationClient.request("deleteMetricConfiguration",
			metricConfigurationResponse.getContent("metricConfigurationId"));
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}
