package REST.degradation;

import java.io.File;

import REST.processingEndpoint.FirstProcessingAfter;
import REST.processingEndpoint.HasProcessingAfter;
import REST.processingEndpoint.LastProcessing;
import REST.processingEndpoint.LastProcessingBefore;
import REST.repositoryEndpoint.FirstProcessing;
import REST.repositoryEndpoint.HasProcessing;
import REST.repositoryEndpoint.HasProcessingInTime;
import REST.repositoryEndpoint.HasReadyProcessing;
import REST.repositoryEndpoint.LastProcessingState;
import REST.repositoryEndpoint.LastReadyProcessing;
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
import strategy.RESTStrategy;

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
		processingStrategy.afterIteration();
	}

	@Override
	public void beforeIteration() throws Exception {
		processingStrategy.beforeIteration();
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
		startExperiment(false, "hasProcessingBefore", new HasProcessingInTime());
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
