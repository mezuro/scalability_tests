package SOAP.degradation;

import java.io.File;

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
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.SOAPStrategy;

public class SOAPConfiguration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPConfigurationEndpoint/?wsdl";
	private SOAPStrategy configurationStrategy;
	private static WSClient kalibroClient;
	private static SOAPConfiguration configuration;

	public SOAPConfiguration() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

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
	}

	@Override
	public Item request(Item item) throws Exception {
		return configurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		configuration = new SOAPConfiguration();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		configuration.setStrategy(experimentStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(10);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPConfiguration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/configurationResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allSOAPConfigurations", new AllSOAPConfigurations());
		startExperiment(false, "configurationsExists", new SOAPConfigurationExists());
		startExperiment(false, "getSOAPConfiguration", new GetSOAPConfiguration());
		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveSOAPConfiguration", new SaveSOAPConfiguration());
		startExperiment(true, "deleteSOAPConfiguration", new DeleteSOAPConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		configuration.setSOAPConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}
