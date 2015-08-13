package SOAP.degradation;

import java.io.File;

import SOAP.projectEndpoint.AllSOAPProjects;
import SOAP.projectEndpoint.DeleteSOAPProject;
import SOAP.projectEndpoint.GetSOAPProject;
import SOAP.projectEndpoint.SOAPProjectExists;
import SOAP.projectEndpoint.SaveSOAPProject;
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

public class SOAPProject extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private final String WSDL = "http://10.0.0.12:8080/KalibroService/SOAPProjectEndpoint/?wsdl";
	private SOAPStrategy projectStrategy;
	private static WSClient kalibroClient;
	private static SOAPProject project;

	public SOAPProject() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPProjectStrategy(SOAPStrategy projectStrategy) {
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
		projectStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		projectStrategy.beforeStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return projectStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		project = new SOAPProject();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		project.setStrategy(experimentStrategy);

		project.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		project.setNumberOfSteps(10);
		project.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPProject Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/projectResults.xml"))));

		experimentStrategy.setParameterInitialValue(1000);
		experimentStrategy.setFunction(new LinearIncrease(500));
		startExperiment(false, "allSOAPProjects", new AllSOAPProjects());
		startExperiment(false, "projectExists", new SOAPProjectExists());
		startExperiment(false, "getSOAPProject", new GetSOAPProject());

		experimentStrategy.setParameterInitialValue(50);
		experimentStrategy.setFunction(new LinearIncrease(600));
		startExperiment(false, "saveSOAPProject", new SaveSOAPProject());
		startExperiment(true, "deleteSOAPProject", new DeleteSOAPProject(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		project.setSOAPProjectStrategy(strategy);
		project.run(label, plotGraph);
	}
}
