package org.mezuro.scalability_tests.SOAP.composed;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.AllSOAPReadingGroups;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.DeleteSOAPReadingGroup;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.GetSOAPReadingGroup;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.SOAPReadingGroupExists;
import org.mezuro.scalability_tests.SOAP.support.SOAPKalibroDeployer;
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
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy readingGroupStrategy;
	private static SOAPReadingGroup readingGroup;

	public void setSOAPRepositoryStrategy(SOAPStrategy readingGroupStrategy) {
		this.readingGroupStrategy = readingGroupStrategy;
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		readingGroupStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return readingGroupStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		readingGroupStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		readingGroupStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		readingGroupStrategy.beforeStep();
		readingGroupStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPReadingGroup").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return readingGroupStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		readingGroup = new SOAPReadingGroup();

		readingGroup.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		readingGroup.setStrategy(composedStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(4);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPReading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/readingGroupResults.xml"))));

		readingGroup.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(1000);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allSOAPReadingGroups", new AllSOAPReadingGroups());
		startExperiment(false, "readingGroupExists", new SOAPReadingGroupExists());
		startExperiment(false, "getSOAPReadingGroup", new GetSOAPReadingGroup());

		readingGroup.setNumberOfRequestsPerMinute(500);
//		workloadStrategy.setParameterInitialValue(150);
//		startExperiment(false, "saveSOAPReadingGroup", new SaveSOAPReadingGroup());
		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPReadingGroup", new DeleteSOAPReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		readingGroup.setSOAPRepositoryStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}

}
