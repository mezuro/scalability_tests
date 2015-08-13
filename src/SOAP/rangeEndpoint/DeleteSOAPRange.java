package SOAP.rangeEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import strategy.SOAPStrategy;

public class DeleteSOAPRange extends SOAPStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricConfigurationEndpoint/?wsdl";
	private final String RANGE_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRangeEndpoint/?wsdl";
	private static WSClient metricSOAPConfigurationClient;
	private static WSClient rangeClient;
	private ItemImpl saveSOAPMetricConfiguration;
	private Item metricSOAPConfigurationResponse;
	private int requestsPerStep;
	private Stack<String> idList;
	private int step = 0;
	private List<Integer> errors;
	private Integer append = 0;

	public DeleteSOAPRange(int requestsPerStep) throws Exception {
		metricSOAPConfigurationClient = new WSClient(METRIC_CONFIGURATION_WSDL);
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		rangeClient = new WSClient(RANGE_WSDL);
	}

	@Override
	public void beforeExperiment() throws Exception {
		saveSOAPMetricConfiguration = new ItemImpl("saveSOAPMetricConfiguration");
		Item metricSOAPConfiguration = saveSOAPMetricConfiguration.addChild("metricSOAPConfiguration");
		metricSOAPConfiguration.addChild("baseToolName").setContent("Analizo");
		metricSOAPConfiguration.addChild("id").setContent("");
		metricSOAPConfiguration.addChild("aggregationForm").setContent("AVERAGE");
		metricSOAPConfiguration.addChild("weight").setContent("10");
		metricSOAPConfiguration.addChild("readingGroupId").setContent("1");
		Item metric = metricSOAPConfiguration.addChild("metric");
		metric.addChild("scope").setContent("CLASS");
		metric.addChild("compound").setContent("FALSE");
		metric.addChild("description").setContent("");
		metric.addChild("name").setContent("Lines of Code");
		metric.addChild("language").setContent("C");
		metric.addChild("script").setContent("");
		metricSOAPConfiguration.addChild("code").setContent("jtp");
		saveSOAPMetricConfiguration.addChild("configurationId").setContent("1");
		metricSOAPConfigurationResponse = metricSOAPConfigurationClient.request("saveSOAPMetricConfiguration",
			saveSOAPMetricConfiguration);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveSOAPRange = new ItemImpl("saveSOAPRange");
			Item range = saveSOAPRange.addChild("range");
			range.addChild("id").setContent("");
			range.addChild("beginning").setContent(append.toString());
			append++;
			range.addChild("readingId").setContent("1");
			range.addChild("comments").setContent("Comment");
			range.addChild("end").setContent(append.toString());
			saveSOAPRange.addChild("metricSOAPConfigurationId").setContent(
				metricSOAPConfigurationResponse.getContent("metricSOAPConfigurationId"));
			idList.push(rangeClient.request("saveSOAPRange", saveSOAPRange).getContent("rangeId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteSOAPRange", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteSOAPRangeResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		metricSOAPConfigurationClient.request("deleteSOAPMetricConfiguration",
			metricSOAPConfigurationResponse.getContent("metricSOAPConfigurationId"));
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}
