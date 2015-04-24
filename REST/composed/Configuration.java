package composed;

import java.io.File;

import support.RESTKalibroDeployer;
import support.RESTStrategy;
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
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class Configuration extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy configurationStrategy;
	private static Configuration configuration;

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
		configurationStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		configurationStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		configurationStrategy.beforeStep();
		configurationStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Configuration").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return configurationStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		configuration = new Configuration();

		configuration.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		configuration.setStrategy(composedStrategy);

		configuration.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		configuration.setNumberOfSteps(4);
		configuration.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Configuration Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/configurationResults.xml"))));

		configuration.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(100);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allConfigurations", new AllConfigurations());
		startExperiment(false, "configurationsExists", new ConfigurationExists());
		startExperiment(false, "getConfiguration", new GetConfiguration());

		configuration.setNumberOfRequestsPerMinute(400);
		workloadStrategy.setParameterInitialValue(400);
		startExperiment(false, "saveConfiguration", new SaveConfiguration());
		startExperiment(true, "deleteConfiguration", new DeleteConfiguration(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		configuration.setConfigurationStrategy(strategy);
		configuration.run(label, plotGraph);
	}
}
