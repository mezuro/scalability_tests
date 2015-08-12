package REST.degradation;

import java.io.File;

import REST.rangeEndpoint.DeleteRange;
import REST.rangeEndpoint.RangesOf;
import REST.rangeEndpoint.SaveRange;
import REST.support.RESTStrategy;
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

public class Range extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/RangeEndpoint/?wsdl";
	private RESTStrategy rangeStrategy;
	private static RSClient kalibroClient;
	private static Range range;

	public Range() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

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
	public void afterRequest(Item requestResponse) throws Exception {
		rangeStrategy.afterRequest(requestResponse);
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
	public void beforeIteration() throws Exception {
		rangeStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return rangeStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		range = new Range();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		range.setStrategy(experimentStrategy);

		range.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		range.setNumberOfSteps(10);
		range.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Range Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/rangeResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "rangesOf", new RangesOf());

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(350));
		startExperiment(false, "saveRange", new SaveRange());
		startExperiment(true, "deleteRange", new DeleteRange(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		range.setRangeStrategy(strategy);
		range.run(label, plotGraph);
	}
}
