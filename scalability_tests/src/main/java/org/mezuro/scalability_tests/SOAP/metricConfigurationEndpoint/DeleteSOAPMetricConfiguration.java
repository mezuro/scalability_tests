package org.mezuro.scalability_tests.SOAP.metricConfigurationEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class DeleteSOAPMetricConfiguration extends SOAPStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricConfigurationEndpoint/?wsdl";
	private static WSClient metricSOAPConfigurationClient;
	private int step = 0;
	private int requestsPerStep;
	private Stack<String> idList;
	private List<Integer> errors;
	private int append = 0;

	public DeleteSOAPMetricConfiguration(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		metricSOAPConfigurationClient = new WSClient(METRIC_CONFIGURATION_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveSOAPMetricConfiguration = new ItemImpl("saveSOAPMetricConfiguration");
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
			metricSOAPConfiguration.addChild("code").setContent("jtp" + append++);
			saveSOAPMetricConfiguration.addChild("configurationId").setContent("1");
			idList.push(metricSOAPConfigurationClient.request("saveSOAPMetricConfiguration", saveSOAPMetricConfiguration)
				.getContent(
					"metricSOAPConfigurationId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteSOAPMetricConfiguration", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteSOAPMetricConfigurationResponse"))
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
