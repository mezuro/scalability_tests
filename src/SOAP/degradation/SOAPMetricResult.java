package SOAP.degradation;

import java.io.File;

import SOAP.metricResultEndpoint.SOAPDescendantResultsOf;
import SOAP.metricResultEndpoint.SOAPHistoryOfMetric;
import SOAP.metricResultEndpoint.SOAPMetricResultsOf;
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
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPMetricResult extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPMetricResultEndpoint/?wsdl";
	private SOAPStrategy metricResultStrategy;
	private static WSClient kalibroClient;
	private static SOAPMetricResult metricResult;

	public SOAPMetricResult() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPMetricResultStrategy(SOAPStrategy metricResultStrategy) {
		this.metricResultStrategy = metricResultStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		metricResultStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		metricResultStrategy.afterExperiment();
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		metricResultStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return metricResultStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		metricResultStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		metricResultStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return metricResultStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		metricResult = new SOAPMetricResult();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		metricResult.setStrategy(experimentStrategy);

		metricResult.setNumberOfRequestsPerStep(8);
		metricResult.setNumberOfSteps(10);
		metricResult.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Metric Result Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/metricResults.xml"))));

		experimentStrategy.setFunction(new ExponentialIncrease(2));
		experimentStrategy.setParameterInitialValue(1);
		startExperiment(true, "metricResultsOf", new SOAPMetricResultsOf());

		metricResult.setNumberOfRequestsPerStep(10);
		metricResult.setNumberOfSteps(10);
		experimentStrategy.setParameterInitialValue(500);
		experimentStrategy.setFunction(new LinearIncrease(700));
		startExperiment(false, "descendantResultsOf", new SOAPDescendantResultsOf());
		startExperiment(true, "historyOfMetric", new SOAPHistoryOfMetric());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		metricResult.setSOAPMetricResultStrategy(strategy);
		metricResult.run(label, plotGraph);
	}
}
