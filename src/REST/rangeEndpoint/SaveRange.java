package REST.rangeEndpoint;

import java.util.ArrayList;
import java.util.List;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveRange extends RESTStrategy {

	private final String METRIC_CONFIGURATION_WSDL = "http://10.0.0.12:8080/KalibroService/MetricConfigurationEndpoint/?wsdl";
	private static RSClient metricConfigurationClient;
	private ItemImpl saveMetricConfiguration;
	private Item metricConfigurationResponse;
	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;
	private Integer append = 0;

	public SaveRange() throws Exception {
		metricConfigurationClient = new RSClient(METRIC_CONFIGURATION_WSDL);
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
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
	public Item beforeRequest() throws Exception {
		Item saveRange = new ItemImpl("saveRange");
		Item range = saveRange.addChild("range");
		range.addChild("id").setContent("");
		range.addChild("beginning").setContent(append.toString());
		range.addChild("readingId").setContent("1");
		range.addChild("comments").setContent("Comment");
		append++;
		range.addChild("end").setContent(append.toString());
		saveRange.addChild("metricConfigurationId").setContent(
			metricConfigurationResponse.getContent("metricConfigurationId"));
		return saveRange;
	}

	@Override
	public Item request(Item saveRange) throws Exception {
		return rsClient.request("saveRange", saveRange);
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
			rsClient.request("deleteRange", id);
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
		metricConfigurationClient.request("deleteMetricConfiguration",
			metricConfigurationResponse.getContent("metricConfigurationId"));
	}

}
