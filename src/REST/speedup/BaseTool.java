package REST.speedup;

import java.io.File;

import REST.support.RESTKalibroDeployer;
import REST.baseToolEndpoint.AllBaseToolNames;
import REST.baseToolEndpoint.GetBaseTool;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import strategy.RESTStrategy;

public class BaseTool extends Experiment<Item, Item> {

	private RESTStrategy baseToolStrategy;
	private static BaseTool baseTool;

	public void setBaseToolStrategy(RESTStrategy baseToolStrategy) {
		this.baseToolStrategy = baseToolStrategy;
	}

	@Override
	public void beforeIteration() throws Exception {
		baseToolStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("BaseTool").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return baseToolStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		baseTool = new BaseTool();

		baseTool.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		baseTool.setStrategy(experimentStrategy);

		baseTool.setNumberOfRequestsPerStep(30);
		baseTool.setNumberOfSteps(4);
		baseTool.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Base Tool Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/baseToolResults.xml"))));

		baseTool.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allBaseToolNames", new AllBaseToolNames());
		startExperiment(true, "getBaseTool", new GetBaseTool());
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		baseTool.setBaseToolStrategy(strategy);
		baseTool.run(label, plotGraph);
	}

}
