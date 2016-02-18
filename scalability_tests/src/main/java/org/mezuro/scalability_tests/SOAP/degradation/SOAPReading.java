package org.mezuro.scalability_tests.SOAP.degradation;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.readingEndpoint.DeleteSOAPReading;
import org.mezuro.scalability_tests.SOAP.readingEndpoint.GetSOAPReading;
import org.mezuro.scalability_tests.SOAP.readingEndpoint.SOAPReadingsOf;
import org.mezuro.scalability_tests.SOAP.readingEndpoint.SaveSOAPReading;
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
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPReading extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPReadingEndpoint/?wsdl";
	private SOAPStrategy readingStrategy;
	private static WSClient kalibroClient;
	private static SOAPReading reading;

	public SOAPReading() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPReadingStrategy(SOAPStrategy readingStrategy) {
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
		readingStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		readingStrategy.beforeStep();
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
		reading = new SOAPReading();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		reading.setStrategy(experimentStrategy);

		reading.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		reading.setNumberOfSteps(10);
		reading.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPReading Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/SOAPReadingResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "readingsOf", new SOAPReadingsOf());
		startExperiment(false, "getSOAPReading", new GetSOAPReading());

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(350));
		startExperiment(false, "saveSOAPReading", new SaveSOAPReading());
		startExperiment(true, "deleteSOAPReading", new DeleteSOAPReading(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		reading.setSOAPReadingStrategy(strategy);
		reading.run(label, plotGraph);
	}
}
