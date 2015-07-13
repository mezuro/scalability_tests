package composed;

import java.io.File;

import readingEndpoint.DeleteReading;
import readingEndpoint.GetReading;
import readingEndpoint.ReadingsOf;
import readingEndpoint.SaveReading;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class Reading extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy readingStrategy;
	private static Reading reading;

	public void setReadingStrategy(RESTStrategy readingStrategy) {
		this.readingStrategy = readingStrategy;
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		readingStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return readingStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		readingStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		readingStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		readingStrategy.beforeStep();
		readingStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Reading").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return readingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		reading = new Reading();

		reading.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		reading.setStrategy(composedStrategy);

		reading.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		reading.setNumberOfSteps(4);
		reading.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/readingResults.xml"))));

		reading.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "readingsOf", new ReadingsOf());
		reading.setNumberOfRequestsPerMinute(800);
		startExperiment(false, "getReading", new GetReading());
		reading.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(250);
		startExperiment(false, "saveReading", new SaveReading());
		reading.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteReading", new DeleteReading(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		reading.setReadingStrategy(strategy);
		reading.run(label, plotGraph);
	}

}