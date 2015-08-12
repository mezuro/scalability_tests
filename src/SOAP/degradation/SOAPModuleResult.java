package SOAP.degradation;

import java.io.File;

import SOAP.moduleResultEndpoint.GetSOAPModuleResult;
import SOAP.moduleResultEndpoint.SOAPChildrenOf;
import SOAP.moduleResultEndpoint.SOAPHistoryOfModule;
import SOAP.support.SOAPStrategy;
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

public class SOAPModuleResult extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPModuleResultEndpoint/?wsdl";
	private SOAPStrategy moduleResultStrategy;
	private static WSClient kalibroClient;
	private static SOAPModuleResult moduleResult;

	public SOAPModuleResult() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

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
	public void afterRequest(Item requestResponse) throws Exception {
		moduleResultStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return moduleResultStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		moduleResultStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		moduleResultStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return moduleResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		moduleResult = new SOAPModuleResult();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		moduleResult.setStrategy(experimentStrategy);

		moduleResult.setNumberOfRequestsPerStep(10);
		moduleResult.setNumberOfSteps(10);
		moduleResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Module Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/moduleResultResults.xml"))));

		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(700));
		startExperiment(false, "childrensOf", new SOAPChildrenOf());
		startExperiment(false, "getSOAPModuleResult", new GetSOAPModuleResult());
		startExperiment(true, "historyOfModule", new SOAPHistoryOfModule());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		moduleResult.setSOAPModuleResultStrategy(strategy);
		moduleResult.run(label, plotGraph);
	}
}
