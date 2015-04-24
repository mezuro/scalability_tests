package speedup;

import java.io.File;

import metricConfigurationEndpoint.DeleteMetricConfiguration;
import metricConfigurationEndpoint.GetMetricConfiguration;
import metricConfigurationEndpoint.MetricConfigurationsOf;
import metricConfigurationEndpoint.SaveMetricConfiguration;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class MetricConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 20;
	private RESTStrategy metricConfigurationStrategy;
	private static MetricConfiguration metricConfiguration;

	public void setMetricConfigurationStrategy(RESTStrategy metricConfigurationStrategy) {
		this.metricConfigurationStrategy = metricConfigurationStrategy;
	}

	@Override
	public Item beforeRequest() throws Exception {
		return metricConfigurationStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		metricConfigurationStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		metricConfigurationStrategy.beforeStep();
		metricConfigurationStrategy
			.setRsClient(new RSClient(getDeployer().getServiceUris("MetricConfiguration").get(0)));
	}

	@Override
	public void afterIteration() throws Exception {
		metricConfigurationStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		metricConfigurationStrategy.afterExperiment();
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricConfigurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricConfiguration = new MetricConfiguration();

		metricConfiguration.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		metricConfiguration.setStrategy(experimentStrategy);

		metricConfiguration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		metricConfiguration.setNumberOfSteps(4);
		metricConfiguration.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/metricConfigurationResults.xml"))));

		metricConfiguration.setNumberOfRequestsPerMinute(75);
		startExperiment(false, "metricConfigurationsOf", new MetricConfigurationsOf());
		metricConfiguration.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "getMetricConfiguration", new GetMetricConfiguration());
		metricConfiguration.setNumberOfRequestsPerMinute(250);
		startExperiment(false, "saveMetricConfiguration", new SaveMetricConfiguration());
		metricConfiguration.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteMetricConfiguration", new DeleteMetricConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		metricConfiguration.setMetricConfigurationStrategy(strategy);
		metricConfiguration.run(label, plotGraph);
	}

}
