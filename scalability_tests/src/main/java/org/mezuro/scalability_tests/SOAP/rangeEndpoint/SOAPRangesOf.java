package org.mezuro.scalability_tests.SOAP.rangeEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPRangesOf extends SOAPStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricConfigurationEndpoint/?wsdl";
	private static WSClient metricSOAPConfigurationClient;
	private ItemImpl saveSOAPMetricConfiguration;
	private Item requestResponse;

	public SOAPRangesOf() throws Exception {
		metricSOAPConfigurationClient = new WSClient(METRIC_CONFIGURATION_WSDL);
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
		requestResponse = metricSOAPConfigurationClient.request("saveSOAPMetricConfiguration", saveSOAPMetricConfiguration);
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("rangesOf", requestResponse.getContent("metricSOAPConfigurationId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		metricSOAPConfigurationClient.request("deleteSOAPMetricConfiguration",
			requestResponse.getContent("metricSOAPConfigurationId"));
	}
}
