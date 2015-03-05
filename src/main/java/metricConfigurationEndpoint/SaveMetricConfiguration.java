package metricConfigurationEndpoint;

import java.util.ArrayList;
import java.util.List;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveMetricConfiguration extends Strategy {

	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;
	private int append = 0;

	public SaveMetricConfiguration() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
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
		return saveMetricConfiguration;
	}

	@Override
	public Item request(Item saveMetricConfiguration) throws Exception {
		return wsClient.request("saveMetricConfiguration", saveMetricConfiguration);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("metricConfigurationId"));
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
			wsClient.request("deleteMetricConfiguration", id);
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
