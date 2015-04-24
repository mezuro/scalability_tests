package degradation;

import repositoryEndpoint.ProcessRepository;
import support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;

public class Repository extends Experiment<String, String> {

	private static final int REQUESTS_PER_STEP = 1;
	private final String BASE_URI = "http://aguia1.ime.usp.br";
	private final int PORT = 8082;
	private final String BASE_PATH = "/";
	private RESTStrategy repositoryStrategy;
	private static RSClient kalibroClient;
	private static Repository repository;

	public Repository() throws Exception {
		kalibroClient = new RSClient(BASE_URI, BASE_PATH, PORT);
	}

	public void setRepositoryStrategy(RESTStrategy repositoryStrategy) {
		this.repositoryStrategy = repositoryStrategy;
	}

	@Override
	public void afterRequest(String requestResponse) throws Exception {
		repositoryStrategy.afterRequest(requestResponse);
	}

	@Override
	public void beforeExperiment() throws Exception {
		repositoryStrategy.beforeExperiment();
	}

	@Override
	public String beforeRequest() throws Exception {
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
	public String request(String string) throws Exception {
		return repositoryStrategy.request(string);
	}

	public static void main(String[] args) throws Exception {
		repository = new Repository();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(1);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Repository Aggregate Performance",
			new MeanChartCreator())));

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

	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(kalibroClient);
		repository.setRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}
}
