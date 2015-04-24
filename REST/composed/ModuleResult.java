package composed;

import java.io.File;

import moduleResultEndpoint.ChildrenOf;
import moduleResultEndpoint.GetModuleResult;
import moduleResultEndpoint.HistoryOfModule;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
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

public class ModuleResult extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy moduleResultStrategy;
	private static ModuleResult moduleResult;

	public void setModuleResultStrategy(RESTStrategy moduleResultStrategy) {
		this.moduleResultStrategy = moduleResultStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		moduleResultStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		moduleResultStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		moduleResultStrategy
			.setRsClient(new RSClient(getDeployer().getServiceUris("ModuleResult").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return moduleResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		moduleResult = new ModuleResult();

		moduleResult.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		moduleResult.setStrategy(composedStrategy);

		moduleResult.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		moduleResult.setNumberOfSteps(4);
		moduleResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Module Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/moduleResultResults.xml"))));

		moduleResult.setNumberOfRequestsPerMinute(200);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "childrensOf", new ChildrenOf());
		startExperiment(false, "getModuleResult", new GetModuleResult());
		startExperiment(true, "historyOfModule", new HistoryOfModule());
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		moduleResult.setModuleResultStrategy(strategy);
		moduleResult.run(label, plotGraph);
	}

}
