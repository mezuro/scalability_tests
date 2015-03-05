package composed;

import java.io.File;

import processingEndpoint.FirstProcessing;
import processingEndpoint.FirstProcessingAfter;
import processingEndpoint.HasProcessing;
import processingEndpoint.HasProcessingAfter;
import processingEndpoint.HasProcessingBefore;
import processingEndpoint.HasReadyProcessing;
import processingEndpoint.LastProcessing;
import processingEndpoint.LastProcessingBefore;
import processingEndpoint.LastProcessingState;
import processingEndpoint.LastReadyProcessing;
import support.KalibroDeployer;
import support.Strategy;
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

public class Processing extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private Strategy processingStrategy;
	private static Processing processing;

	public void setProcessingStrategy(Strategy processingStrategy) {
		this.processingStrategy = processingStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		processingStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		processingStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		processingStrategy
			.setWsClient(new WSClient(getDeployer().getServiceUris("Processing").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return processingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		processing = new Processing();

		processing.setDeployer(new KalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		processing.setStrategy(composedStrategy);

		processing.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		processing.setNumberOfSteps(4);
		processing.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Processing Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/processingResults.xml"))));

		processing.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "firstProcessing", new FirstProcessing());
		startExperiment(false, "firstProcessingAfter", new FirstProcessingAfter());
		startExperiment(false, "hasProcessing", new HasProcessing());
		startExperiment(false, "hasProcessingAfter", new HasProcessingAfter());
		startExperiment(false, "hasProcessingBefore", new HasProcessingBefore());
		startExperiment(false, "hasReadyProcessing", new HasReadyProcessing());
		startExperiment(false, "lastProcessing", new LastProcessing());
		startExperiment(false, "lastProcessingBefore", new LastProcessingBefore());
		startExperiment(false, "lastProcessingState", new LastProcessingState());
		startExperiment(true, "lastReadyProcessing", new LastReadyProcessing());
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		processing.setProcessingStrategy(strategy);
		processing.run(label, plotGraph);
	}

}