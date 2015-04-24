package composed;

import java.io.File;

import repositoryEndpoint.ProcessSOAPRepository;
import support.SOAPKalibroDeployer;
import support.SOAPStrategy;
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
	public void afterRequest(Item resquestResponse) throws Exception {
		repositoryStrategy.afterRequest(resquestResponse);
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
	public void afterExperiment() throws Exception {
		repositoryStrategy.afterExperiment();
	}

	@Override
	public void beforeIteration() throws Exception {
		repositoryStrategy.beforeStep();
		repositoryStrategy.setWsClient(new WSClient(getDeployer().getServiceUris("SOAPRepository").get(0)));
	}

	@Override
	public Item request(Item item) throws Exception {
		return repositoryStrategy.request(item);
	}

	public static void main(String[] args) throws Exception {
		repository = new SOAPRepository();

		repository.setDeployer(new SOAPKalibroDeployer());
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(1);
		capacityStrategy.setFunction(new LinearIncrease(1));

		ExperimentStrategy workloadStrategy = new WorkloadScaling();

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		repository.setStrategy(composedStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(4);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPRepository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/composed/new1ProcessSOAPRepositoryResults.xml"))));

//		old
//		workloadStrategy.setParameterInitialValue(300);
//		workloadStrategy.setFunction(new LinearIncrease(300));
//		new
		workloadStrategy.setParameterInitialValue(2);
		workloadStrategy.setFunction(new ExponentialIncrease(2));
		startExperiment(true, "processSOAPRepository", new ProcessSOAPRepository());

//		repository.setNumberOfRequestsPerMinute(1000);
//		workloadStrategy.setParameterInitialValue(300);
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedSOAPRepositoryTypes", new SupportedSOAPRepositoryTypes());
//		startExperiment(false, "cancelSOAPProcessingOfSOAPRepository", new CancelSOAPProcessingOfSOAPRepository());
//
//		repository.setNumberOfRequestsPerMinute(600);
//		workloadStrategy.setParameterInitialValue(150);
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
