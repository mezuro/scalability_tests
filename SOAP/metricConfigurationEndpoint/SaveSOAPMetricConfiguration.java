package metricConfigurationEndpoint;

import java.util.ArrayList;
import java.util.List;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class SaveSOAPMetricConfiguration extends SOAPStrategy {

	private int step = 0;
	private List<Integer> errors;
	private List<String> idList;
	private int append = 0;

	public SaveSOAPMetricConfiguration() {
		idList = new ArrayList<String>();
		errors = new ArrayList<Integer>();
	}

	@Override
	public Item beforeRequest() {
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
		return saveSOAPMetricConfiguration;
	}

	@Override
	public Item request(Item saveSOAPMetricConfiguration) throws Exception {
		return wsClient.request("saveSOAPMetricConfiguration", saveSOAPMetricConfiguration);
	}

	@Override
	public void afterRequest(Item requestResponse) {
		try {
			idList.add(requestResponse.getContent("metricSOAPConfigurationId"));
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
			wsClient.request("deleteSOAPMetricConfiguration", id);
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
