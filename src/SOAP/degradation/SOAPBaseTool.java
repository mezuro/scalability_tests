package SOAP.degradation;

import java.io.File;

import SOAP.baseToolEndpoint.SOAPAllBaseToolNames;
import SOAP.baseToolEndpoint.SOAPGetBaseTool;
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

public class SOAPBaseTool extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/BaseToolEndpoint/?wsdl";
	private SOAPStrategy baseToolStrategy;
	private static WSClient kalibroClient;
	private static SOAPBaseTool baseTool;

	public SOAPBaseTool() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPProjectStrategy(SOAPStrategy baseToolStrategy) {
		this.baseToolStrategy = baseToolStrategy;
	}

	@Override
	public Item request(Item item) throws Exception {
		return baseToolStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		baseTool = new SOAPBaseTool();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		baseTool.setStrategy(experimentStrategy);

		baseTool.setNumberOfRequestsPerStep(10);
		baseTool.setNumberOfSteps(10);
		baseTool.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Base Tool Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/baseToolResults.xml"))));

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(true, "allBaseToolNames", new SOAPAllBaseToolNames());

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(400));
		startExperiment(true, "getBaseTool", new SOAPGetBaseTool());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		baseTool.setSOAPProjectStrategy(strategy);
		baseTool.run(label, plotGraph);
	}
}
