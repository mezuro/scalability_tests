package org.mezuro.scalability_tests.REST.composed;

import java.io.File;

import org.mezuro.scalability_tests.REST.repositoryEndpoint.ProcessRepository;
import org.mezuro.scalability_tests.REST.support.RESTKalibroDeployer;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import org.mezuro.scalability_tests.strategy.RESTStrategy;

public class Repository extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private RESTStrategy repositoryStrategy;
	private static Repository repository;

	public void setRepositoryStrategy(RESTStrategy repositoryStrategy) {
		this.repositoryStrategy = repositoryStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		repositoryStrategy.beforeExperiment();
	}

	@Override
	public void afterRequest(Item resquestResponse) throws Exception {
		repositoryStrategy.afterRequest(resquestResponse);
	}

	@Override
	public Item beforeRequest() throws Exception {
		return repositoryStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		repositoryStrategy.afterIteration();
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		repositoryStrategy.beforeIteration();
		repositoryStrategy.setRsClient(new RSClient(getDeployer().getServiceUris("Repository").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return repositoryStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		repository = new Repository();

		repository.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		repository.setStrategy(composedStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(4);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Repository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/new1ProcessRepositoryResults.xml"))));

//		old
//		workloadStrategy.setParameterInitialValue(300);
//		workloadStrategy.setFunction(new LinearIncrease(300));
//		new
		workloadStrategy.setParameterInitialValue(2);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(true, "processRepository", new ProcessRepository());

//		repository.setNumberOfRequestsPerMinute(1000);
//		workloadStrategy.setParameterInitialValue(300);
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedRepositoryTypes", new SupportedRepositoryTypes());
//		startExperiment(false, "cancelProcessingOfRepository", new CancelProcessingOfRepository());
//
//		repository.setNumberOfRequestsPerMinute(600);
//		workloadStrategy.setParameterInitialValue(150);
//		startExperiment(false, "saveRepository", new SaveRepository());
//		repository.setNumberOfRequestsPerMinute(1000);
//		startExperiment(true, "deleteRepository", new DeleteRepository(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		repository.setRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}

}
