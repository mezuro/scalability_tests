package composed;

import java.io.File;

import metricResultEndpoint.SOAPDescendantResultsOf;
import metricResultEndpoint.SOAPHistoryOfMetric;
import metricResultEndpoint.SOAPMetricResultsOf;
import support.SOAPKalibroDeployer;
import support.SOAPStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPMetricResult extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy metricResultStrategy;
	private static SOAPMetricResult metricResult;

	public void setSOAPMetricResultStrategy(SOAPStrategy metricResultStrategy) {
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
		metricResultStrategy
			.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPMetricResult").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricResult = new SOAPMetricResult();

		metricResult.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		metricResult.setStrategy(composedStrategy);

		metricResult.setNumberOfRequestsPerStep(8);
		metricResult.setNumberOfSteps(4);
		metricResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Metric Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/metricResultResults.xml"))));

		metricResult.setNumberOfRequestsPerMinute(75);
		workloadStrategy.setParameterInitialValue(1);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "metricResultsOf", new SOAPMetricResultsOf());

		metricResult.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		metricResult.setNumberOfRequestsPerMinute(350);
		workloadStrategy.setParameterInitialValue(500);
		startExperiment(false, "descendantResultsOf", new SOAPDescendantResultsOf());
		startExperiment(true, "historyOfMetric", new SOAPHistoryOfMetric());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		metricResult.setSOAPMetricResultStrategy(strategy);
		metricResult.run(label, plotGraph);
	}

}
