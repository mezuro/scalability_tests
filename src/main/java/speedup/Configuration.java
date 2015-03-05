package speedup;

import java.io.File;

import support.KalibroDeployer;
import support.Strategy;
import configurationEndpoint.AllConfigurations;
import configurationEndpoint.ConfigurationExists;
import configurationEndpoint.DeleteConfiguration;
import configurationEndpoint.GetConfiguration;
import configurationEndpoint.SaveConfiguration;
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

public class Configuration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private Strategy configurationStrategy;
	private static Configuration configuration;

	public void setConfigurationStrategy(Strategy baseToolStrategy) {
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
		configurationStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("Configuration").get(0)));
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
		configuration = new Configuration();

		configuration.setDeployer(new KalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		configuration.setStrategy(experimentStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(4);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/configurationResults.xml"))));

		configuration.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allConfigurations", new AllConfigurations());
		startExperiment(false, "getConfiguration", new GetConfiguration());
		startExperiment(false, "configurationExists", new ConfigurationExists());
		configuration.setNumberOfRequestsPerMinute(400);
		startExperiment(false, "saveConfiguration", new SaveConfiguration());
		startExperiment(true, "deleteConfiguration", new DeleteConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		configuration.setConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}