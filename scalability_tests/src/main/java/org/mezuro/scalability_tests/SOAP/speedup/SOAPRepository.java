package org.mezuro.scalability_tests.SOAP.speedup;

import java.io.File;

import org.mezuro.scalability_tests.SOAP.repositoryEndpoint.ProcessSOAPRepository;
import org.mezuro.scalability_tests.SOAP.support.SOAPKalibroDeployer;
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
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SOAPRepository extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private SOAPStrategy repositoryStrategy;
	private static SOAPRepository repository;

	public void setSOAPRepositoryStrategy(SOAPStrategy repositoryStrategy) {
		this.repositoryStrategy = repositoryStrategy;
	}

	@Override
	public void beforeExperiment() throws Exception {
		repositoryStrategy.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryStrategy.afterExperiment();
	}

	@Override
	public Item beforeRequest() throws Exception {
		return repositoryStrategy.beforeRequest();
	}

	@Override
	public void afterRequest(Item param) throws Exception {
		repositoryStrategy.afterRequest(param);
	}

	@Override
	public void beforeIteration() throws Exception {
		repositoryStrategy.beforeStep();
		repositoryStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPRepository").get(0)));
	}

	@Override
	public void afterIteration() throws Exception {
		repositoryStrategy.afterStep();
	}

	@Override
	public Item request(Item item) throws Exception {
		return repositoryStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		repository = new SOAPRepository();

		repository.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(3);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPRepository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/new2ProcessSOAPRepositoryResults.xml"))));

//		old
//		repository.setNumberOfRequestsPerMinute(500);
//		new
		repository.setNumberOfRequestsPerMinute(15);
		startExperiment(true, "processSOAPRepository", new ProcessSOAPRepository());
//		repository.setNumberOfRequestsPerMinute(1000);
//		startExperiment(false, "cancelSOAPProcessingOfSOAPRepository", new CancelSOAPProcessingOfSOAPRepository());
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedSOAPRepositoryTypes", new SupportedSOAPRepositoryTypes());
//		repository.setNumberOfRequestsPerMinute(600);
//		startExperiment(false, "saveSOAPRepository", new SaveSOAPRepository());
//		repository.setNumberOfRequestsPerMinute(1000);
//		startExperiment(true, "deleteSOAPRepository", new DeleteSOAPRepository(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		repository.setSOAPRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}

}
