package org.mezuro.scalability_tests.SOAP.composed;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.projectEndpoint.AllSOAPProjects;
import org.mezuro.scalability_tests.SOAP.projectEndpoint.DeleteSOAPProject;
import org.mezuro.scalability_tests.SOAP.projectEndpoint.GetSOAPProject;
import org.mezuro.scalability_tests.SOAP.projectEndpoint.SOAPProjectExists;
import org.mezuro.scalability_tests.SOAP.projectEndpoint.SaveSOAPProject;
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

public class SOAPProject extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private SOAPStrategy projectStrategy;
	private static SOAPProject project;

	public void setSOAPProjectStrategy(SOAPStrategy projectStrategy) {
		this.projectStrategy = projectStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		projectStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		projectStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return projectStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		projectStrategy.afterStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		projectStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		projectStrategy.beforeStep();
		projectStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPProject").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return projectStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		project = new SOAPProject();

		project.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		project.setStrategy(composedStrategy);

		project.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		project.setNumberOfSteps(4);
		project.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPProject Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/projectResults.xml"))));

		project.setNumberOfRequestsPerMinute(1000);
		workloadStrategy.setParameterInitialValue(1000);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(false, "allSOAPProjects", new AllSOAPProjects());
		startExperiment(false, "projectExists", new SOAPProjectExists());
		startExperiment(false, "getSOAPProject", new GetSOAPProject());

		project.setNumberOfRequestsPerMinute(400);
		workloadStrategy.setParameterInitialValue(400);
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
