package SOAP.degradation;

import java.io.File;

import SOAP.rangeEndpoint.DeleteSOAPRange;
import SOAP.rangeEndpoint.SOAPRangesOf;
import SOAP.rangeEndpoint.SaveSOAPRange;
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
import strategy.SOAPStrategy;

public class SOAPRange extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPRangeEndpoint/?wsdl";
	private SOAPStrategy rangeStrategy;
	private static WSClient kalibroClient;
	private static SOAPRange range;

	public SOAPRange() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPRangeStrategy(SOAPStrategy rangeStrategy) {
		this.rangeStrategy = rangeStrategy;
	}

	@Override
	public void afterExperiment() throws Exception {
		rangeStrategy.afterExperiment();
	}

	@Override
	public void beforeExperiment() throws Exception {
		rangeStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		rangeStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return rangeStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		rangeStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		rangeStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return rangeStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		range = new SOAPRange();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		range.setStrategy(experimentStrategy);

		range.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		range.setNumberOfSteps(10);
		range.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPRange Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/rangeResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "rangesOf", new SOAPRangesOf());

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(350));
		startExperiment(false, "saveSOAPRange", new SaveSOAPRange());
		startExperiment(true, "deleteSOAPRange", new DeleteSOAPRange(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		range.setSOAPRangeStrategy(strategy);
		range.run(label, plotGraph);
	}
}
