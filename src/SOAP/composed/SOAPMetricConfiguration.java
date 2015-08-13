package SOAP.composed;

import java.io.File;

import SOAP.metricConfigurationEndpoint.DeleteSOAPMetricConfiguration;
import SOAP.metricConfigurationEndpoint.GetSOAPMetricConfiguration;
import SOAP.metricConfigurationEndpoint.SOAPMetricConfigurationsOf;
import SOAP.metricConfigurationEndpoint.SaveSOAPMetricConfiguration;
import SOAP.support.SOAPKalibroDeployer;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.SOAPStrategy;

public class SOAPMetricConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy metricSOAPConfigurationStrategy;
	private static SOAPMetricConfiguration metricSOAPConfiguration;

	public void setSOAPMetricConfigurationStrategy(SOAPStrategy metricSOAPConfigurationStrategy) {
		this.metricSOAPConfigurationStrategy = metricSOAPConfigurationStrategy;
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		metricSOAPConfigurationStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return metricSOAPConfigurationStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		metricSOAPConfigurationStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		metricSOAPConfigurationStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		metricSOAPConfigurationStrategy.beforeStep();
		metricSOAPConfigurationStrategy
			.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPMetricConfiguration").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricSOAPConfigurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricSOAPConfiguration = new SOAPMetricConfiguration();

		metricSOAPConfiguration.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		metricSOAPConfiguration.setStrategy(composedStrategy);

		metricSOAPConfiguration.setNumberOfRequestsPerStep(15);
		metricSOAPConfiguration.setNumberOfSteps(4);
		metricSOAPConfiguration.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric SOAPConfiguration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/metricSOAPConfigurationResults.xml"))));

		metricSOAPConfiguration.setNumberOfRequestsPerMinute(75);
		workloadStrategy.setParameterInitialValue(8);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "metricSOAPConfigurationsOf", new SOAPMetricConfigurationsOf());

		metricSOAPConfiguration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		metricSOAPConfiguration.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(400);
		startExperiment(false, "getSOAPMetricConfiguration", new GetSOAPMetricConfiguration());

		workloadStrategy.setParameterInitialValue(50);
		metricSOAPConfiguration.setNumberOfRequestsPerMinute(250);
		startExperiment(false, "saveSOAPMetricConfiguration", new SaveSOAPMetricConfiguration());

		workloadStrategy.setParameterInitialValue(350);
		metricSOAPConfiguration.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPMetricConfiguration", new DeleteSOAPMetricConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		metricSOAPConfiguration.setSOAPMetricConfigurationStrategy(strategy);
		metricSOAPConfiguration.run(label, plotGraph);
	}
}
