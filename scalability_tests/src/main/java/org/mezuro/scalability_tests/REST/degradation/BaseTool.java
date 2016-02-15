package org.mezuro.scalability_tests.REST.degradation;

import java.io.File;

import org.mezuro.scalability_tests.REST.baseToolEndpoint.AllBaseToolNames;
import org.mezuro.scalability_tests.REST.baseToolEndpoint.GetBaseTool;
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
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class BaseTool extends Experiment<Item, Item> {

	private final String WSDL = "http://10.0.0.12:8080/KalibroService/BaseToolEndpoint/?wsdl";
	private RESTStrategy baseToolStrategy;
	private static RSClient kalibroClient;
	private static BaseTool baseTool;

	public BaseTool() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setProjectStrategy(RESTStrategy baseToolStrategy) {
		this.baseToolStrategy = baseToolStrategy;
	}

	@Override
	public Item request(Item item) throws Exception {
		return baseToolStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		baseTool = new BaseTool();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		baseTool.setStrategy(experimentStrategy);

		baseTool.setNumberOfRequestsPerStep(10);
		baseTool.setNumberOfSteps(10);
		baseTool.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Base Tool Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/baseToolResults.xml"))));

		experimentStrategy.setParameterInitialValue(100);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(true, "allBaseToolNames", new AllBaseToolNames());

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(400));
		startExperiment(true, "getBaseTool", new GetBaseTool());
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		baseTool.setProjectStrategy(strategy);
		baseTool.run(label, plotGraph);
	}
}
