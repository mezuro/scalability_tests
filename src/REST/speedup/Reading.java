package REST.speedup;

import java.io.File;

import REST.readingEndpoint.Delete;
import REST.readingEndpoint.GetReading;
import REST.readingEndpoint.ReadingsOf;
import REST.readingEndpoint.SaveReading;
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

public class Reading extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy readingStrategy;
	private static Reading reading;

	public void setReadingStrategy(RESTStrategy readingStrategy) {
		this.readingStrategy = readingStrategy;
	}

	@Override
	public Item beforeRequest() throws Exception {
		return readingStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		readingStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		readingStrategy.beforeIteration();
		readingStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Reading").get(0)));
	}

	@Override
	public void afterIteration() throws Exception {
		readingStrategy.afterIteration();
	}

	@Override
	public void afterExperiment() throws Exception {
		readingStrategy.afterExperiment();
	}

	@Override
	public Item request(Item item) throws Exception {
		return readingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		reading = new Reading();

		reading.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		reading.setStrategy(experimentStrategy);

		reading.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		reading.setNumberOfSteps(4);
		reading.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/readingResults.xml"))));

		reading.setNumberOfRequestsPerMinute(600);
		startExperiment(false, "getReading", new GetReading());
		reading.setNumberOfRequestsPerMinute(800);
		startExperiment(false, "readingsOf", new ReadingsOf());
		reading.setNumberOfRequestsPerMinute(600);
		startExperiment(false, "saveReading", new SaveReading());
		reading.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteReading", new Delete(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		reading.setReadingStrategy(strategy);
		reading.run(label, plotGraph);
	}

}
