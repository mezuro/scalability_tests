package rangeEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class RangesOf extends RESTStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/MetricConfigurationEndpoint/?wsdl";
	private static RSClient metricConfigurationClient;
	private ItemImpl saveMetricConfiguration;
	private Item requestResponse;

	public RangesOf() throws Exception {
		metricConfigurationClient = new RSClient(METRIC_CONFIGURATION_WSDL);
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
		requestResponse = metricConfigurationClient.request("saveMetricConfiguration", saveMetricConfiguration);
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("rangesOf", requestResponse.getContent("metricConfigurationId"));
	}

	@Override
	public void afterExperiment() throws Exception {
		metricConfigurationClient.request("deleteMetricConfiguration",
			requestResponse.getContent("metricConfigurationId"));
	}
}
