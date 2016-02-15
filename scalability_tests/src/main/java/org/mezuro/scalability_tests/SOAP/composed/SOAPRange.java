package org.mezuro.scalability_tests.SOAP.composed;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.rangeEndpoint.DeleteSOAPRange;
import org.mezuro.scalability_tests.SOAP.rangeEndpoint.SOAPRangesOf;
import org.mezuro.scalability_tests.SOAP.rangeEndpoint.SaveSOAPRange;
import org.mezuro.scalability_tests.SOAP.support.SOAPKalibroDeployer;
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
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPRange extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy rangeStrategy;
	private static SOAPRange range;

	public void setSOAPRangeStrategy(SOAPStrategy rangeStrategy) {
		this.rangeStrategy = rangeStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		rangeStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		rangeStrategy.afterRequest(resquestResponse);
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
	public void afterExperiment() throws Exception {
		rangeStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		rangeStrategy.beforeStep();
		rangeStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPRange").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return rangeStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		range = new SOAPRange();

		range.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		range.setStrategy(composedStrategy);

		range.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		range.setNumberOfSteps(4);
		range.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPRange Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/rangeResults.xml"))));

		range.setNumberOfRequestsPerMinute(600);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "rangesOf", new SOAPRangesOf());

		workloadStrategy.setParameterInitialValue(250);
		startExperiment(false, "saveSOAPRange", new SaveSOAPRange());
		range.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPRange", new DeleteSOAPRange(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		range.setSOAPRangeStrategy(strategy);
		range.run(label, plotGraph);
	}
}
