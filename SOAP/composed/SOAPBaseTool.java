package composed;

import java.io.File;

import support.SOAPKalibroDeployer;
import support.SOAPStrategy;
import baseToolEndpoint.SOAPAllBaseToolNames;
import baseToolEndpoint.SOAPGetBaseTool;
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

public class SOAPBaseTool extends Experiment<Item, Item> {

	private SOAPStrategy baseToolStrategy;
	private static SOAPBaseTool baseTool;

	public void setBaseToolStrategy(SOAPStrategy baseToolStrategy) {
		this.baseToolStrategy = baseToolStrategy;
	}

	@Override
	public void beforeIteration() throws Exception {
		baseToolStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("BaseTool").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return baseToolStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		baseTool = new SOAPBaseTool();

		baseTool.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		baseTool.setStrategy(composedStrategy);

		baseTool.setNumberOfRequestsPerStep(30);
		baseTool.setNumberOfSteps(4);
		baseTool.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Base Tool Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/baseToolResults.xml"))));

		baseTool.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(100);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allBaseToolNames", new SOAPAllBaseToolNames());

		workloadStrategy.setParameterInitialValue(1000);
		startExperiment(true, "getBaseTool", new SOAPGetBaseTool());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		baseTool.setBaseToolStrategy(strategy);
		baseTool.run(label, plotGraph);
	}
}
