package speedup;

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
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class SOAPBaseTool extends Experiment<Item, Item> {

	private SOAPStrategy baseToolSOAPStrategy;
	private static SOAPBaseTool baseTool;

	public void setBaseToolSOAPStrategy(SOAPStrategy baseToolSOAPStrategy) {
		this.baseToolSOAPStrategy = baseToolSOAPStrategy;
	}

	@Override
	public void beforeIteration() throws Exception {
		baseToolSOAPStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("BaseTool").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return baseToolSOAPStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		baseTool = new SOAPBaseTool();

		baseTool.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		baseTool.setStrategy(experimentStrategy);

		baseTool.setNumberOfRequestsPerStep(30);
		baseTool.setNumberOfSteps(4);
		baseTool.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Base Tool Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/baseToolResults.xml"))));

		baseTool.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allBaseToolNames", new SOAPAllBaseToolNames());
		startExperiment(true, "getBaseTool", new SOAPGetBaseTool());
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy SOAPStrategy)
		throws Exception {
		baseTool.setBaseToolSOAPStrategy(SOAPStrategy);
		baseTool.run(label, plotGraph);
	}

}
