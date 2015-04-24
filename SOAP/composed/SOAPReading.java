package composed;

import java.io.File;

import readingEndpoint.DeleteSOAPReading;
import readingEndpoint.GetSOAPReading;
import readingEndpoint.SOAPReadingsOf;
import readingEndpoint.SaveSOAPReading;
import support.SOAPKalibroDeployer;
import support.SOAPStrategy;
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

public class SOAPReading extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy readingStrategy;
	private static SOAPReading reading;

	public void setSOAPReadingStrategy(SOAPStrategy readingStrategy) {
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
		readingStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPReading").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return readingStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		reading = new SOAPReading();

		reading.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		reading.setStrategy(composedStrategy);

		reading.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		reading.setNumberOfSteps(4);
		reading.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPReading Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/readingResults.xml"))));

		reading.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "readingsOf", new SOAPReadingsOf());
		reading.setNumberOfRequestsPerMinute(800);
		startExperiment(false, "getSOAPReading", new GetSOAPReading());
		reading.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(250);
		startExperiment(false, "saveSOAPReading", new SaveSOAPReading());
		reading.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPReading", new DeleteSOAPReading(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		reading.setSOAPReadingStrategy(strategy);
		reading.run(label, plotGraph);
	}

}
