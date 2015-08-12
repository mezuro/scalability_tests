package SOAP.rangeEndpoint;

import java.util.ArrayList;
import java.util.List;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveSOAPRange extends SOAPStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricConfigurationEndpoint/?wsdl";
	private static WSClient metricSOAPConfigurationClient;
	private ItemImpl saveSOAPMetricConfiguration;
	private Item metricSOAPConfigurationResponse;
	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;
	private Integer append = 0;

	public SaveSOAPRange() throws Exception {
		metricSOAPConfigurationClient = new WSClient(METRIC_CONFIGURATION_WSDL);
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
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
	public Item beforeRequest() throws Exception {
		Item saveSOAPRange = new ItemImpl("saveSOAPRange");
		Item range = saveSOAPRange.addChild("range");
		range.addChild("id").setContent("");
		range.addChild("beginning").setContent(append.toString());
		range.addChild("readingId").setContent("1");
		range.addChild("comments").setContent("Comment");
		append++;
		range.addChild("end").setContent(append.toString());
		saveSOAPRange.addChild("metricSOAPConfigurationId").setContent(
			metricSOAPConfigurationResponse.getContent("metricSOAPConfigurationId"));
		return saveSOAPRange;
	}

	@Override
	public Item request(Item saveSOAPRange) throws Exception {
		return wsClient.request("saveSOAPRange", saveSOAPRange);
	}

	@Override
	public void afterRequest(Item rangeResponse) {
		try {
			idList.add(rangeResponse.getContent("rangeId"));
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
			wsClient.request("deleteSOAPRange", id);
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
		metricSOAPConfigurationClient.request("deleteSOAPMetricConfiguration",
			metricSOAPConfigurationResponse.getContent("metricSOAPConfigurationId"));
	}

}
