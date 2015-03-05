package speedup;

import java.io.File;

import moduleResultEndpoint.ChildrenOf;
import moduleResultEndpoint.GetModuleResult;
import moduleResultEndpoint.HistoryOfModule;
import support.KalibroDeployer;
import support.Strategy;
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

public class ModuleResult extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private Strategy moduleResultStrategy;
	private static ModuleResult moduleResult;

	public void setModuleResultStrategy(Strategy moduleResultStrategy) {
		this.moduleResultStrategy = moduleResultStrategy;
	}

	@Override
	public void afterExperiment() throws Exception {
		moduleResultStrategy.afterExperiment();
	}

	@Override
	public void beforeExperiment() throws Exception {
		moduleResultStrategy.beforeExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		moduleResultStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("ModuleResult").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return moduleResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		moduleResult = new ModuleResult();

		moduleResult.setDeployer(new KalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		moduleResult.setStrategy(experimentStrategy);

		moduleResult.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		moduleResult.setNumberOfSteps(4);
		moduleResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Module Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/moduleResultResults.xml"))));

		moduleResult.setNumberOfRequestsPerMinute(200);
		startExperiment(false, "childrenOf", new ChildrenOf());
		startExperiment(false, "getModuleResult", new GetModuleResult());
		startExperiment(true, "historyOfModule", new HistoryOfModule());
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		moduleResult.setModuleResultStrategy(strategy);
		moduleResult.run(label, plotGraph);
	}

}
