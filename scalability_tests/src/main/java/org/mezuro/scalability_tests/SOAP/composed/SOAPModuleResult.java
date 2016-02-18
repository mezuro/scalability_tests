package org.mezuro.scalability_tests.SOAP.composed;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.moduleResultEndpoint.GetSOAPModuleResult;
import org.mezuro.scalability_tests.SOAP.moduleResultEndpoint.SOAPChildrenOf;
import org.mezuro.scalability_tests.SOAP.moduleResultEndpoint.SOAPHistoryOfModule;
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

public class SOAPModuleResult extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy moduleResultStrategy;
	private static SOAPModuleResult moduleResult;

	public void setSOAPModuleResultStrategy(SOAPStrategy moduleResultStrategy) {
		this.moduleResultStrategy = moduleResultStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		moduleResultStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		moduleResultStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		moduleResultStrategy
			.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPModuleResult").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return moduleResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		moduleResult = new SOAPModuleResult();

		moduleResult.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		moduleResult.setStrategy(composedStrategy);

		moduleResult.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		moduleResult.setNumberOfSteps(4);
		moduleResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance(
			"Module Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/moduleResultResults.xml"))));

		moduleResult.setNumberOfRequestsPerMinute(200);
		workloadStrategy.setParameterInitialValue(500);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "childrensOf", new SOAPChildrenOf());
		startExperiment(false, "getSOAPModuleResult", new GetSOAPModuleResult());
		startExperiment(true, "historyOfModule", new SOAPHistoryOfModule());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		moduleResult.setSOAPModuleResultStrategy(strategy);
		moduleResult.run(label, plotGraph);
	}

}
