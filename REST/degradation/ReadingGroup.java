package degradation;

import java.io.File;

import readingGroupEndpoint.AllReadingGroups;
import readingGroupEndpoint.DeleteReadingGroup;
import readingGroupEndpoint.GetReadingGroup;
import readingGroupEndpoint.ReadingGroupExists;
import readingGroupEndpoint.SaveReadingGroup;
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

public class ReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/ReadingGroupEndpoint/?wsdl";
	private RESTStrategy readingGroupStrategy;
	private static RSClient kalibroClient;
	private static ReadingGroup readingGroup;

	public ReadingGroup() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setReadingGroupStrategy(RESTStrategy readingGroupStrategy) {
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
		readingGroup = new ReadingGroup();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		readingGroup.setStrategy(experimentStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(10);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/readingGroupResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allReadingGroups", new AllReadingGroups());
		startExperiment(false, "readingGroupExists", new ReadingGroupExists());
		startExperiment(false, "getReadingGroup", new GetReadingGroup());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveReadingGroup", new SaveReadingGroup());
		startExperiment(true, "deleteReadingGroup", new DeleteReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		readingGroup.setReadingGroupStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}
}
