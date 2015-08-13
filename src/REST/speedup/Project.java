package REST.speedup;

import java.io.File;

import REST.projectEndpoint.AllProjects;
import REST.projectEndpoint.DeleteProject;
import REST.projectEndpoint.GetProject;
import REST.projectEndpoint.ProjectExists;
import REST.projectEndpoint.SaveProject;
import REST.support.RESTKalibroDeployer;
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

public class Project extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 30;
	private RESTStrategy projectStrategy;
	private static Project project;

	public void setProjectStrategy(RESTStrategy projectStrategy) {
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
		projectStrategy.afterIteration();
	}

	@Override
	public void beforeIteration() throws Exception {
		projectStrategy.beforeIteration();
		projectStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Project").get(0)));
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
		project = new Project();

		project.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		project.setStrategy(experimentStrategy);

		project.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		project.setNumberOfSteps(4);
		project.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Project Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/projectResults.xml"))));

		project.setNumberOfRequestsPerMinute(1000);
		startExperiment(false, "allProjects", new AllProjects());
		startExperiment(false, "getProject", new GetProject());
		startExperiment(false, "projectExists", new ProjectExists());
		project.setNumberOfRequestsPerMinute(400);
		startExperiment(false, "saveProject", new SaveProject());
		project.setNumberOfRequestsPerMinute(1000);
		startExperiment(true, "deleteProject", new DeleteProject(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		project.setProjectStrategy(strategy);
		project.run(label, plotGraph);
	}

}
