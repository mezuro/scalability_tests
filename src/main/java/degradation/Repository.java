package degradation;

import java.io.File;

import repositoryEndpoint.ProcessRepository;
import support.Strategy;
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

public class Repository extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 7;
	private final String WSDL = "http://10.0.0.9:8080/KalibroService/RepositoryEndpoint/?wsdl";
	private Strategy repositoryStrategy;
	private static WSClient kalibroClient;
	private static Repository repository;

	public Repository() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setRepositoryStrategy(Strategy repositoryStrategy) {
		this.repositoryStrategy = repositoryStrategy;
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		repositoryStrategy.afterRequest(requestResponse);
	}

	@Override
	public void beforeExperiment() throws Exception {
		repositoryStrategy.beforeExperiment();
	}

	@Override
	public Item beforeRequest() throws Exception {
		return repositoryStrategy.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		repositoryStrategy.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		repositoryStrategy.beforeStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		repositoryStrategy.afterExperiment();
	}

	@Override
	public Item request(Item item) throws Exception {
		return repositoryStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		repository = new Repository();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(4);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Repository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/new1ProcessRepositoryResults.xml"))));

//		old
//		experimentStrategy.setParameterInitialValue(50);
//		experimentStrategy.setFunction(new LinearIncrease(25));
//		new
		experimentStrategy.setParameterInitialValue(2);
		experimentStrategy.setFunction(new LinearIncrease(2));
		startExperiment(true, "processRepository", new ProcessRepository());

//		experimentStrategy.setParameterInitialValue(300);
//		experimentStrategy.setFunction(new LinearIncrease(600));
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedRepositoryTypes", new SupportedRepositoryTypes());
//		startExperiment(false, "cancelProcessingOfRepository", new CancelProcessingOfRepository());
//		experimentStrategy.setParameterInitialValue(150);
//		experimentStrategy.setFunction(new LinearIncrease(200));
//		startExperiment(false, "saveRepository", new SaveRepository());
//		startExperiment(true, "deleteRepository", new DeleteRepository(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		repository.setRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}
}
