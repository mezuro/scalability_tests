package REST.degradation;

import java.io.File;

import REST.metricConfigurationEndpoint.DeleteMetricConfiguration;
import REST.metricConfigurationEndpoint.GetMetricConfiguration;
import REST.metricConfigurationEndpoint.MetricConfigurationsOf;
import REST.metricConfigurationEndpoint.SaveMetricConfiguration;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.RESTStrategy;

public class MetricConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/MetricConfigurationEndpoint/?wsdl";
	private RESTStrategy metricConfigurationStrategy;
	private static RSClient kalibroClient;
	private static MetricConfiguration metricConfiguration;

	public MetricConfiguration() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setMetricConfigurationStrategy(RESTStrategy metricConfigurationStrategy) {
		this.metricConfigurationStrategy = metricConfigurationStrategy;
	}

	@Override
	public void afterExperiment() throws Exception {
		metricConfigurationStrategy.afterExperiment();
	}

	@Override
	public void beforeExperiment() throws Exception {
		metricConfigurationStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		metricConfigurationStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return metricConfigurationStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		metricConfigurationStrategy.afterIteration();
	}

	@Override
	public void beforeIteration() throws Exception {
		metricConfigurationStrategy.beforeIteration();
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricConfigurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricConfiguration = new MetricConfiguration();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		metricConfiguration.setStrategy(experimentStrategy);

		metricConfiguration.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/metricConfigurationResults.xml"))));
		metricConfiguration.setNumberOfRequestsPerStep(15);
		metricConfiguration.setNumberOfSteps(6);

		experimentStrategy.setParameterInitialValue(8);
		experimentStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(true, "metricConfigurationsOf", new MetricConfigurationsOf());

		metricConfiguration.setNumberOfSteps(10);
		metricConfiguration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		experimentStrategy.setParameterInitialValue(300);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "getMetricConfiguration", new GetMetricConfiguration());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(300));
		startExperiment(false, "saveMetricConfiguration", new SaveMetricConfiguration());

		experimentStrategy.setParameterInitialValue(350);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(true, "deleteMetricConfiguration", new DeleteMetricConfiguration(REQUESTS_PER_STEP));

	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		metricConfiguration.setMetricConfigurationStrategy(strategy);
		metricConfiguration.run(label, plotGraph);
	}
}
