package org.mezuro.scalability_tests.SOAP.speedup;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.processingEndpoint.FirstSOAPProcessing;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.FirstSOAPProcessingAfter;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.HasSOAPProcessing;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.HasSOAPProcessingAfter;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.HasSOAPProcessingBefore;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.HasReadySOAPProcessing;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.LastSOAPProcessing;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.LastSOAPProcessingBefore;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.LastSOAPProcessingState;
import org.mezuro.scalability_tests.SOAP.processingEndpoint.LastReadySOAPProcessing;
import org.mezuro.scalability_tests.SOAP.support.SOAPKalibroDeployer;
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
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPProcessing extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy processingStrategy;
	private static SOAPProcessing processing;

	public void setSOAPRepositoryStrategy(SOAPStrategy processingStrategy) {
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
		processingStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPProcessing").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return processingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		processing = new SOAPProcessing();

		processing.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		processing.setStrategy(experimentStrategy);

		processing.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		processing.setNumberOfSteps(4);
		processing.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPProcessing Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/processingResults.xml"))));

		processing.setNumberOfRequestsPerMinute(1000);
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
		processing.setSOAPRepositoryStrategy(strategy);
		processing.run(label, plotGraph);
	}

}
