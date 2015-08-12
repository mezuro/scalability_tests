package SOAP.degradation;

import java.io.File;

import SOAP.metricConfigurationEndpoint.DeleteSOAPMetricConfiguration;
import SOAP.metricConfigurationEndpoint.GetSOAPMetricConfiguration;
import SOAP.metricConfigurationEndpoint.SOAPMetricConfigurationsOf;
import SOAP.metricConfigurationEndpoint.SaveSOAPMetricConfiguration;
import SOAP.support.SOAPStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPMetricConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricConfigurationEndpoint/?wsdl";
	private SOAPStrategy metricSOAPConfigurationStrategy;
	private static WSClient kalibroClient;
	private static SOAPMetricConfiguration metricSOAPConfiguration;

	public SOAPMetricConfiguration() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPMetricConfigurationStrategy(SOAPStrategy metricSOAPConfigurationStrategy) {
		this.metricSOAPConfigurationStrategy = metricSOAPConfigurationStrategy;
	}

	@Override
	public void afterExperiment() throws Exception {
		metricSOAPConfigurationStrategy.afterExperiment();
	}

	@Override
	public void beforeExperiment() throws Exception {
		metricSOAPConfigurationStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		metricSOAPConfigurationStrategy.afterRequest(requestResponse);
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
	public void beforeIteration() throws Exception {
		metricSOAPConfigurationStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricSOAPConfigurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricSOAPConfiguration = new SOAPMetricConfiguration();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		metricSOAPConfiguration.setStrategy(experimentStrategy);

		metricSOAPConfiguration.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric SOAPConfiguration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/metricSOAPConfigurationResults.xml"))));
		metricSOAPConfiguration.setNumberOfRequestsPerStep(15);
		metricSOAPConfiguration.setNumberOfSteps(6);

		experimentStrategy.setParameterInitialValue(8);
		experimentStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(true, "metricSOAPConfigurationsOf", new SOAPMetricConfigurationsOf());

		metricSOAPConfiguration.setNumberOfSteps(10);
		metricSOAPConfiguration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		experimentStrategy.setParameterInitialValue(300);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "getSOAPMetricConfiguration", new GetSOAPMetricConfiguration());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(300));
		startExperiment(false, "saveSOAPMetricConfiguration", new SaveSOAPMetricConfiguration());

		experimentStrategy.setParameterInitialValue(350);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(true, "deleteSOAPMetricConfiguration", new DeleteSOAPMetricConfiguration(REQUESTS_PER_STEP));

	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		metricSOAPConfiguration.setSOAPMetricConfigurationStrategy(strategy);
		metricSOAPConfiguration.run(label, plotGraph);
	}
}
