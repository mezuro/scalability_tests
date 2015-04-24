package composed;

import java.io.File;

import rangeEndpoint.DeleteRange;
import rangeEndpoint.RangesOf;
import rangeEndpoint.SaveRange;
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

public class Range extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy rangeStrategy;
	private static Range range;

	public void setRangeStrategy(RESTStrategy rangeStrategy) {
		this.rangeStrategy = rangeStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		rangeStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		rangeStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return rangeStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		rangeStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		rangeStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		rangeStrategy.beforeStep();
		rangeStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Range").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return rangeStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		range = new Range();

		range.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		range.setStrategy(composedStrategy);

		range.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		range.setNumberOfSteps(4);
		range.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Range Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/rangeResults.xml"))));

		range.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "rangesOf", new RangesOf());

		workloadStrategy.setParameterInitialValue(250);
		startExperiment(false, "saveRange", new SaveRange());
		range.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteRange", new DeleteRange(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		range.setRangeStrategy(strategy);
		range.run(label, plotGraph);
	}
}
