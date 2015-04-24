package degradation;

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
import support.RESTStrategy;
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

public class Processing extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/ProcessingEndpoint/?wsdl";
	private RESTStrategy processingStrategy;
	private static RSClient kalibroClient;
	private static Processing processing;

	public Processing() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setProcessingStrategy(RESTStrategy processingStrategy) {
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
	public void afterRequest(Item requestResponse) throws Exception {
		processingStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return processingStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		processingStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		processingStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return processingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		processing = new Processing();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		processing.setStrategy(experimentStrategy);

		processing.setNumberOfRequestsPerStep(10);
		processing.setNumberOfSteps(10);
		processing.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Processing Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/processingResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(1000));
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

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		processing.setProcessingStrategy(strategy);
		processing.run(label, plotGraph);
	}
}
