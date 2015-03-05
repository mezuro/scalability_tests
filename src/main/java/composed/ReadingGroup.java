package composed;

import java.io.File;

import readingGroupEndpoint.AllReadingGroups;
import readingGroupEndpoint.DeleteReadingGroup;
import readingGroupEndpoint.GetReadingGroup;
import readingGroupEndpoint.ReadingGroupExists;
import support.KalibroDeployer;
import support.Strategy;
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

public class ReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private Strategy readingGroupStrategy;
	private static ReadingGroup readingGroup;

	public void setRepositoryStrategy(Strategy readingGroupStrategy) {
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
		readingGroupStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("ReadingGroup").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return readingGroupStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		readingGroup = new ReadingGroup();

		readingGroup.setDeployer(new KalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		readingGroup.setStrategy(composedStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(4);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/readingGroupResults.xml"))));

		readingGroup.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(1000);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allReadingGroups", new AllReadingGroups());
		startExperiment(false, "readingGroupExists", new ReadingGroupExists());
		startExperiment(false, "getReadingGroup", new GetReadingGroup());

		readingGroup.setNumberOfRequestsPerMinute(500);
//		workloadStrategy.setParameterInitialValue(150);
//		startExperiment(false, "saveReadingGroup", new SaveReadingGroup());
		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteReadingGroup", new DeleteReadingGroup(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		readingGroup.setRepositoryStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}

}
