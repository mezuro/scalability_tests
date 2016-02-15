package org.mezuro.scalability_tests.REST.degradation;

import java.io.File;

import org.mezuro.scalability_tests.REST.projectEndpoint.Index;
import org.mezuro.scalability_tests.REST.projectEndpoint.Delete;
import org.mezuro.scalability_tests.REST.projectEndpoint.Show;
import org.mezuro.scalability_tests.REST.projectEndpoint.Exists;
import org.mezuro.scalability_tests.REST.projectEndpoint.Save;
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

public class Project extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/ProjectEndpoint/?wsdl";
	private RESTStrategy projectStrategy;
	private static RSClient kalibroClient;
	private static Project project;

	public Project() throws Exception {
		kalibroClient = new RSClient(WSDL);
	}

	public void setProjectStrategy(RESTStrategy projectStrategy) {
		this.projectStrategy = projectStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		projectStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		projectStrategy.afterExperiment();
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		projectStrategy.afterRequest(requestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return projectStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		projectStrategy.afterIteration();
	}

	@Override
	public void beforeIteration() throws Exception {
		projectStrategy.beforeIteration();
	}

	@Override
	public Item request(Item item) throws Exception {
		return projectStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		project = new Project();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		project.setStrategy(experimentStrategy);

		project.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		project.setNumberOfSteps(10);
		project.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Project Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/projectResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allProjects", new Index());
		startExperiment(false, "projectExists", new Exists());
		startExperiment(false, "getProject", new Show());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveProject", new Save());
		startExperiment(true, "deleteProject", new Delete(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		project.setProjectStrategy(strategy);
		project.run(label, plotGraph);
	}
}
