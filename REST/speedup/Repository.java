package speedup;

import java.io.File;

import repositoryEndpoint.ProcessRepository;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
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
		repositoryStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("Repository").get(0)));
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
		repository = new Repository();

		repository.setDeployer(new RESTKalibroDeployer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(3);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Repository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/speedup/new2ProcessRepositoryResults.xml"))));

//		old
//		repository.setNumberOfRequestsPerMinute(500);
//		new
		repository.setNumberOfRequestsPerMinute(15);
		startExperiment(true, "processRepository", new ProcessRepository());
//		repository.setNumberOfRequestsPerMinute(1000);
//		startExperiment(false, "cancelProcessingOfRepository", new CancelProcessingOfRepository());
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedRepositoryTypes", new SupportedRepositoryTypes());
//		repository.setNumberOfRequestsPerMinute(600);
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
