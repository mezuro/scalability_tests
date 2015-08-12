package SOAP.speedup;

import java.io.File;

import SOAP.projectEndpoint.AllSOAPProjects;
import SOAP.projectEndpoint.DeleteSOAPProject;
import SOAP.projectEndpoint.GetSOAPProject;
import SOAP.projectEndpoint.SOAPProjectExists;
import SOAP.projectEndpoint.SaveSOAPProject;
import SOAP.support.SOAPKalibroDeployer;
import SOAP.support.SOAPStrategy;
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

public class SOAPProject extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy projectStrategy;
	private static SOAPProject project;

	public void setSOAPProjectStrategy(SOAPStrategy projectStrategy) {
		this.projectStrategy = projectStrategy;
	}

	@Override
	public Item beforeRequest() throws Exception {
		return projectStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		projectStrategy.afterRequest(param);
	}

	@Override
	public void afterIteration() throws Exception {
		projectStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		projectStrategy.beforeStep();
		projectStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPProject").get(0)));
		projectStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		projectStrategy.afterExperiment();
	}

	@Override
	public Item request(Item item) throws Exception {
		return projectStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		project = new SOAPProject();

		project.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		project.setStrategy(experimentStrategy);

		project.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		project.setNumberOfSteps(4);
		project.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPProject Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/projectResults.xml"))));

		project.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allSOAPProjects", new AllSOAPProjects());
		startExperiment(false, "getSOAPProject", new GetSOAPProject());
		startExperiment(false, "projectExists", new SOAPProjectExists());
		project.setNumberOfRequestsPerMinute(400);
		startExperiment(false, "saveSOAPProject", new SaveSOAPProject());
		project.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteSOAPProject", new DeleteSOAPProject(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		project.setSOAPProjectStrategy(strategy);
		project.run(label, plotGraph);
	}

}
