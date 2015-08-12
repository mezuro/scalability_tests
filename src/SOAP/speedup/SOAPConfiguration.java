package SOAP.speedup;

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
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy configurationStrategy;
	private static SOAPConfiguration configuration;

	public void setSOAPConfigurationStrategy(SOAPStrategy baseToolStrategy) {
		this.configurationStrategy = baseToolStrategy;
	}

	@Override
	public Item beforeRequest() throws Exception {
		return configurationStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		configurationStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		configurationStrategy.beforeStep();
		configurationStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPConfiguration").get(0)));
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
	public Item request(Item item) throws Exception {
		return configurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		configuration = new SOAPConfiguration();

		configuration.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		configuration.setStrategy(experimentStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(4);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPConfiguration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/configurationResults.xml"))));

		configuration.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allSOAPConfigurations", new AllSOAPConfigurations());
		startExperiment(false, "getSOAPConfiguration", new GetSOAPConfiguration());
		startExperiment(false, "configurationExists", new SOAPConfigurationExists());
		configuration.setNumberOfRequestsPerMinute(400);
		startExperiment(false, "saveSOAPConfiguration", new SaveSOAPConfiguration());
		startExperiment(true, "deleteSOAPConfiguration", new DeleteSOAPConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		configuration.setSOAPConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}
