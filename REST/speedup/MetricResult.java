package speedup;

import java.io.File;

import metricResultEndpoint.DescendantResultsOf;
import metricResultEndpoint.HistoryOfMetric;
import metricResultEndpoint.MetricResultsOf;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
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

public class MetricResult extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 25;
	private RESTStrategy metricResultStrategy;
	private static MetricResult metricResult;

	public void setMetricResultStrategy(RESTStrategy metricResultStrategy) {
		this.metricResultStrategy = metricResultStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		metricResultStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		metricResultStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		metricResultStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("MetricResult").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricResult = new MetricResult();

		metricResult.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		metricResult.setStrategy(experimentStrategy);

		metricResult.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		metricResult.setNumberOfSteps(4);
		metricResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Metric Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/metricResultResults.xml"))));

		metricResult.setNumberOfRequestsPerMinute(75);
		startExperiment(false, "metricResultsOf", new MetricResultsOf());
		startExperiment(false, "descendantResultsOf", new DescendantResultsOf());
		startExperiment(true, "historyOfMetric", new HistoryOfMetric());
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		metricResult.setMetricResultStrategy(strategy);
		metricResult.run(label, plotGraph);
	}

}
