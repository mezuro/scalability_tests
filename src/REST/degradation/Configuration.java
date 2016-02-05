package REST.degradation;

import java.io.File;

import REST.kalibroConfigurationEndpoint.AllConfigurations;
import REST.kalibroConfigurationEndpoint.ConfigurationExists;
import REST.kalibroConfigurationEndpoint.DeleteConfiguration;
import REST.kalibroConfigurationEndpoint.GetConfiguration;
import REST.kalibroConfigurationEndpoint.Save;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.RESTStrategy;

public class Configuration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/ConfigurationEndpoint/?wsdl";
	private RESTStrategy configurationStrategy;
	private static RSClient kalibroClient;
	private static Configuration configuration;

	public Configuration() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setConfigurationStrategy(RESTStrategy configurationStrategy) {
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
		configurationStrategy.afterIteration();
	}

	@Override
	public void afterExperiment() throws Exception {
		configurationStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		configurationStrategy.beforeIteration();
	}

	@Override
	public Item request(Item item) throws Exception {
		return configurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		configuration = new Configuration();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		configuration.setStrategy(experimentStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(10);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/configurationResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allConfigurations", new AllConfigurations());
		startExperiment(false, "configurationsExists", new ConfigurationExists());
		startExperiment(false, "getConfiguration", new GetConfiguration());
		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveConfiguration", new Save());
		startExperiment(true, "deleteConfiguration", new DeleteConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		configuration.setConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}
