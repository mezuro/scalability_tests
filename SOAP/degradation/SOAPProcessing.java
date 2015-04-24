package degradation;

import java.io.File;

import processingEndpoint.FirstSOAPProcessing;
import processingEndpoint.FirstSOAPProcessingAfter;
import processingEndpoint.HasSOAPProcessing;
import processingEndpoint.HasSOAPProcessingAfter;
import processingEndpoint.HasSOAPProcessingBefore;
import processingEndpoint.HasReadySOAPProcessing;
import processingEndpoint.LastSOAPProcessing;
import processingEndpoint.LastSOAPProcessingBefore;
import processingEndpoint.LastSOAPProcessingState;
import processingEndpoint.LastReadySOAPProcessing;
import support.SOAPStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPProcessing extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProcessingEndpoint/?wsdl";
	private SOAPStrategy processingStrategy;
	private static WSClient kalibroClient;
	private static SOAPProcessing processing;

	public SOAPProcessing() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPProcessingStrategy(SOAPStrategy processingStrategy) {
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
		processing = new SOAPProcessing();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		processing.setStrategy(experimentStrategy);

		processing.setNumberOfRequestsPerStep(10);
		processing.setNumberOfSteps(10);
		processing.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPProcessing Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/processingResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(1000));
		startExperiment(false, "firstSOAPProcessing", new FirstSOAPProcessing());
		startExperiment(false, "firstSOAPProcessingAfter", new FirstSOAPProcessingAfter());
		startExperiment(false, "hasSOAPProcessing", new HasSOAPProcessing());
		startExperiment(false, "hasSOAPProcessingAfter", new HasSOAPProcessingAfter());
		startExperiment(false, "hasSOAPProcessingBefore", new HasSOAPProcessingBefore());
		startExperiment(false, "hasReadySOAPProcessing", new HasReadySOAPProcessing());
		startExperiment(false, "lastSOAPProcessing", new LastSOAPProcessing());
		startExperiment(false, "lastSOAPProcessingBefore", new LastSOAPProcessingBefore());
		startExperiment(false, "lastSOAPProcessingState", new LastSOAPProcessingState());
		startExperiment(true, "lastReadySOAPProcessing", new LastReadySOAPProcessing());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		processing.setSOAPProcessingStrategy(strategy);
		processing.run(label, plotGraph);
	}
}
