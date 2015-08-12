package SOAP.degradation;

import java.io.File;

import SOAP.readingGroupEndpoint.AllSOAPReadingGroups;
import SOAP.readingGroupEndpoint.DeleteSOAPReadingGroup;
import SOAP.readingGroupEndpoint.GetSOAPReadingGroup;
import SOAP.readingGroupEndpoint.SOAPReadingGroupExists;
import SOAP.readingGroupEndpoint.SaveSOAPReadingGroup;
import SOAP.support.SOAPStrategy;
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

public class SOAPReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPReadingGroupEndpoint/?wsdl";
	private SOAPStrategy readingGroupStrategy;
	private static WSClient kalibroClient;
	private static SOAPReadingGroup readingGroup;

	public SOAPReadingGroup() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPReadingGroupStrategy(SOAPStrategy readingGroupStrategy) {
		this.readingGroupStrategy = readingGroupStrategy;
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		readingGroupStrategy.afterRequest(requestResponse);
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
	public void beforeIteration() throws Exception {
		readingGroupStrategy.beforeStep();
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
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		readingGroup.setStrategy(experimentStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(10);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPReading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/readingGroupResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allSOAPReadingGroups", new AllSOAPReadingGroups());
		startExperiment(false, "readingGroupExists", new SOAPReadingGroupExists());
		startExperiment(false, "getSOAPReadingGroup", new GetSOAPReadingGroup());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveSOAPReadingGroup", new SaveSOAPReadingGroup());
		startExperiment(true, "deleteSOAPReadingGroup", new DeleteSOAPReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		readingGroup.setSOAPReadingGroupStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}
}
