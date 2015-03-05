package balancer;

import java.io.File;

import repositoryEndpoint.ProcessRepository;
import support.BalancerDepolyer;
import support.Strategy;
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

public class SpeedupBalancer extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 10;
	private Strategy repositoryStrategy;
	private static SpeedupBalancer repository;

	public void setRepositoryStrategy(Strategy repositoryStrategy) {
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
		repository = new SpeedupBalancer();

		repository.setDeployer(new BalancerDepolyer());
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(1);
		experimentStrategy.setFunction(new LinearIncrease(1));
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(5);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Repository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/balancer/old1SpeedupProcessRepositoryResults.xml"))));

		repository.setNumberOfRequestsPerMinute(10);
		startExperiment(true, "processRepository", new ProcessRepository());
	}

	private static void startExperiment(boolean plotGraph, String label, Strategy strategy)
		throws Exception {
		WSClient repositoryClient = new WSClient("http://10.0.0.14:8080/KalibroService/RepositoryEndpoint/?wsdl");
		strategy.setWsClient(repositoryClient);
		repository.setRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}

}