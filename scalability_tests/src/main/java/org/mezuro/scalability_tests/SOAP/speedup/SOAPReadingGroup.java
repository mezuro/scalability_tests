package org.mezuro.scalability_tests.SOAP.speedup;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.AllSOAPReadingGroups;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.DeleteSOAPReadingGroup;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.GetSOAPReadingGroup;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.SOAPReadingGroupExists;
import org.mezuro.scalability_tests.SOAP.readingGroupEndpoint.SaveSOAPReadingGroup;
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

public class SOAPReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy readingGroupStrategy;
	private static SOAPReadingGroup readingGroup;

	public void setSOAPReadingGroupStrategy(SOAPStrategy readingGroupStrategy) {
		this.readingGroupStrategy = readingGroupStrategy;
	}

	@Override
	public Item beforeRequest() throws Exception {
		return readingGroupStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		readingGroupStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		readingGroupStrategy.beforeStep();
		readingGroupStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPReadingGroup").get(0)));
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
	public Item request(Item item) throws Exception {
		return readingGroupStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		readingGroup = new SOAPReadingGroup();

		readingGroup.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		readingGroup.setStrategy(experimentStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(4);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPReading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/deleteSOAPReadingGroupResults.xml"))));

		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allSOAPReadingGroups", new AllSOAPReadingGroups());
		startExperiment(false, "getSOAPReadingGroup", new GetSOAPReadingGroup());
		startExperiment(false, "readingGroupExists", new SOAPReadingGroupExists());
		readingGroup.setNumberOfRequestsPerMinute(500);
		startExperiment(false, "saveSOAPReadingGroup", new SaveSOAPReadingGroup());
		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPReadingGroup", new DeleteSOAPReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		readingGroup.setSOAPReadingGroupStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}

}
