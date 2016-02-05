package REST.speedup;

import java.io.File;

import REST.readingGroupEndpoint.Index;
import REST.readingGroupEndpoint.Delete;
import REST.readingGroupEndpoint.Show;
import REST.readingGroupEndpoint.Exists;
import REST.readingGroupEndpoint.Save;
import REST.support.RESTKalibroDeployer;
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
import strategy.RESTStrategy;

public class ReadingGroup extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy readingGroupStrategy;
	private static ReadingGroup readingGroup;

	public void setReadingGroupStrategy(RESTStrategy readingGroupStrategy) {
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
		readingGroupStrategy.beforeIteration();
		readingGroupStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("ReadingGroup").get(0)));
	}

	@Override
	public void afterIteration() throws Exception {
		readingGroupStrategy.afterIteration();
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

		readingGroup.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		readingGroup.setStrategy(experimentStrategy);

		readingGroup.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		readingGroup.setNumberOfSteps(4);
		readingGroup.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Group Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/deleteReadingGroupResults.xml"))));

		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allReadingGroups", new Index());
		startExperiment(false, "getReadingGroup", new Show());
		startExperiment(false, "readingGroupExists", new Exists());
		readingGroup.setNumberOfRequestsPerMinute(500);
		startExperiment(false, "saveReadingGroup", new Save());
		readingGroup.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteReadingGroup", new Delete(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		readingGroup.setReadingGroupStrategy(strategy);
		readingGroup.run(label, plotGraph);
	}

}
