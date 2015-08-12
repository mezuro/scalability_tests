package REST.composed;

import java.io.File;

import REST.metricConfigurationEndpoint.DeleteMetricConfiguration;
import REST.metricConfigurationEndpoint.GetMetricConfiguration;
import REST.metricConfigurationEndpoint.MetricConfigurationsOf;
import REST.metricConfigurationEndpoint.SaveMetricConfiguration;
import REST.support.RESTKalibroDeployer;
import REST.support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class MetricConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy metricConfigurationStrategy;
	private static MetricConfiguration metricConfiguration;

	public void setMetricConfigurationStrategy(RESTStrategy metricConfigurationStrategy) {
		this.metricConfigurationStrategy = metricConfigurationStrategy;
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		metricConfigurationStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return metricConfigurationStrategy.beforeRequest();
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
	public void beforeIteration() throws Exception {
		metricConfigurationStrategy.beforeStep();
		metricConfigurationStrategy
			.setRsClient(new RSClient(getDeployer().getServiceUris("MetricConfiguration").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricConfigurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricConfiguration = new MetricConfiguration();

		metricConfiguration.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		metricConfiguration.setStrategy(composedStrategy);

		metricConfiguration.setNumberOfRequestsPerStep(15);
		metricConfiguration.setNumberOfSteps(4);
		metricConfiguration.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/metricConfigurationResults.xml"))));

		metricConfiguration.setNumberOfRequestsPerMinute(75);
		workloadStrategy.setParameterInitialValue(8);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "metricConfigurationsOf", new MetricConfigurationsOf());

		metricConfiguration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		metricConfiguration.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(400);
		startExperiment(false, "getMetricConfiguration", new GetMetricConfiguration());

		workloadStrategy.setParameterInitialValue(50);
		metricConfiguration.setNumberOfRequestsPerMinute(250);
		startExperiment(false, "saveMetricConfiguration", new SaveMetricConfiguration());

		workloadStrategy.setParameterInitialValue(350);
		metricConfiguration.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteMetricConfiguration", new DeleteMetricConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		metricConfiguration.setMetricConfigurationStrategy(strategy);
		metricConfiguration.run(label, plotGraph);
	}
}
