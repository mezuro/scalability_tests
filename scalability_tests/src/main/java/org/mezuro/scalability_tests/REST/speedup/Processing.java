package org.mezuro.scalability_tests.REST.speedup;

import java.io.File;

import org.mezuro.scalability_tests.REST.processingEndpoint.FirstProcessingAfter;
import org.mezuro.scalability_tests.REST.processingEndpoint.HasProcessingAfter;
import org.mezuro.scalability_tests.REST.processingEndpoint.LastProcessingBefore;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.FirstProcessing;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.HasProcessing;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.HasProcessingInTime;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.HasReadyProcessing;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.LastProcessing;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.LastProcessingState;
import org.mezuro.scalability_tests.REST.repositoryEndpoint.LastReadyProcessing;
import org.mezuro.scalability_tests.REST.support.RESTKalibroDeployer;
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
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Processing extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy processingStrategy;
	private static Processing processing;

	public void setRepositoryStrategy(RESTStrategy processingStrategy) {
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
		processingStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Processing").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return processingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		processing = new Processing();

		processing.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		processing.setStrategy(experimentStrategy);

		processing.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		processing.setNumberOfSteps(4);
		processing.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Processing Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/processingResults.xml"))));

		processing.setNumberOfRequestsPerMinute(1000);
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
		processing.setRepositoryStrategy(strategy);
		processing.run(label, plotGraph);
	}

}
