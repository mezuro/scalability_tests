package REST.degradation;

import java.io.File;

import REST.readingEndpoint.DeleteReading;
import REST.readingEndpoint.GetReading;
import REST.readingEndpoint.ReadingsOf;
import REST.readingEndpoint.SaveReading;
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
import strategy.RESTStrategy;

public class Reading extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/ReadingEndpoint/?wsdl";
	private RESTStrategy readingStrategy;
	private static RSClient kalibroClient;
	private static Reading reading;

	public Reading() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setReadingStrategy(RESTStrategy readingStrategy) {
		this.readingStrategy = readingStrategy;
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		readingStrategy.afterRequest(requestResponse);
	}

	@Override
	public void beforeExperiment() throws Exception {
		readingStrategy.beforeExperiment();
	}

	@Override
	public Item beforeRequest() throws Exception {
		return readingStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		readingStrategy.afterIteration();
	}

	@Override
	public void beforeIteration() throws Exception {
		readingStrategy.beforeIteration();
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
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		reading.setStrategy(experimentStrategy);

		reading.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		reading.setNumberOfSteps(10);
		reading.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Reading Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/ReadingResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "readingsOf", new ReadingsOf());
		startExperiment(false, "getReading", new GetReading());

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(350));
		startExperiment(false, "saveReading", new SaveReading());
		startExperiment(true, "deleteReading", new DeleteReading(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		reading.setReadingStrategy(strategy);
		reading.run(label, plotGraph);
	}
}
