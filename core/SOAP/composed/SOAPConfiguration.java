package SOAP.composed;

import java.io.File;

import SOAP.support.SOAPKalibroDeployer;
import SOAP.support.SOAPStrategy;
import SOAP.configurationEndpoint.AllSOAPConfigurations;
import SOAP.configurationEndpoint.SOAPConfigurationExists;
import SOAP.configurationEndpoint.DeleteSOAPConfiguration;
import SOAP.configurationEndpoint.GetSOAPConfiguration;
import SOAP.configurationEndpoint.SaveSOAPConfiguration;
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

public class SOAPConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy configurationStrategy;
	private static SOAPConfiguration configuration;

	public void setSOAPConfigurationStrategy(SOAPStrategy configurationStrategy) {
		this.configurationStrategy = configurationStrategy;
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		configurationStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return configurationStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		configurationStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		configurationStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		configurationStrategy.beforeStep();
		configurationStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPConfiguration").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return configurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		configuration = new SOAPConfiguration();

		configuration.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		configuration.setStrategy(composedStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(4);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPConfiguration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/configurationResults.xml"))));

		configuration.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(100);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allSOAPConfigurations", new AllSOAPConfigurations());
		startExperiment(false, "configurationsExists", new SOAPConfigurationExists());
		startExperiment(false, "getSOAPConfiguration", new GetSOAPConfiguration());

		configuration.setNumberOfRequestsPerMinute(400);
		workloadStrategy.setParameterInitialValue(400);
		startExperiment(false, "saveSOAPConfiguration", new SaveSOAPConfiguration());
		startExperiment(true, "deleteSOAPConfiguration", new DeleteSOAPConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		configuration.setSOAPConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}
