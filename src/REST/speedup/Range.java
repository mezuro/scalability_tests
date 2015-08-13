package REST.speedup;

import java.io.File;

import REST.rangeEndpoint.DeleteRange;
import REST.rangeEndpoint.RangesOf;
import REST.rangeEndpoint.SaveRange;
import REST.support.RESTKalibroDeployer;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.RESTStrategy;

public class Range extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy rangeStrategy;
	private static Range range;

	public void setRangeStrategy(RESTStrategy rangeStrategy) {
		this.rangeStrategy = rangeStrategy;
	}

	@Override
	public void afterExperiment() throws Exception {
		rangeStrategy.afterExperiment();
	}

	@Override
	public void beforeExperiment() throws Exception {
		rangeStrategy.beforeExperiment();
	}

	@Override
	public Item beforeRequest() throws Exception {
		return rangeStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		rangeStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		rangeStrategy.beforeIteration();
		rangeStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Range").get(0)));
	}

	@Override
	public void afterIteration() throws Exception {
		rangeStrategy.afterIteration();
	}

	@Override
	public Item request(Item item) throws Exception {
		return rangeStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		range = new Range();

		range.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		range.setStrategy(experimentStrategy);

		range.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		range.setNumberOfSteps(4);
		range.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Range Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/rangeResults.xml"))));

		range.setNumberOfRequestsPerMinute(600);
		startExperiment(false, "rangesOf", new RangesOf());

		range.setNumberOfRequestsPerMinute(600);
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
