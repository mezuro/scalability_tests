package speedup;

import java.io.File;

import readingGroupEndpoint.AllReadingGroups;
import readingGroupEndpoint.DeleteReadingGroup;
import readingGroupEndpoint.GetReadingGroup;
import readingGroupEndpoint.ReadingGroupExists;
import readingGroupEndpoint.SaveReadingGroup;
import support.KalibroDeployer;
import support.Strategy;
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

public class ReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private Strategy readingGroupStrategy;
	private static ReadingGroup readingGroup;

	public void setReadingGroupStrategy(Strategy readingGroupStrategy) {
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
		readingGroupStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("ReadingGroup").get(0)));
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
		readingGroup = new ReadingGroup();

		readingGroup.setDeployer(new KalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		readingGroup.setStrategy(experimentStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(4);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/deleteReadingGroupResults.xml"))));

		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allReadingGroups", new AllReadingGroups());
		startExperiment(false, "getReadingGroup", new GetReadingGroup());
		startExperiment(false, "readingGroupExists", new ReadingGroupExists());
		readingGroup.setNumberOfRequestsPerMinute(500);
		startExperiment(false, "saveReadingGroup", new SaveReadingGroup());
		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteReadingGroup", new DeleteReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		readingGroup.setReadingGroupStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}

}
