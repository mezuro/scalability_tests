package SOAP.degradation;

import java.io.File;

import SOAP.repositoryEndpoint.ProcessSOAPRepository;
import SOAP.support.SOAPStrategy;
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

public class SOAPRepository extends Experiment<Item, Item> {

	private static final int REQUESTS_PER_STEP = 7;
	private final String WSDL = "http://10.0.0.9:8080/KalibroService/SOAPRepositoryEndpoint/?wsdl";
	private SOAPStrategy repositoryStrategy;
	private static WSClient kalibroClient;
	private static SOAPRepository repository;

	public SOAPRepository() throws Exception {
		kalibroClient = new WSClient(WSDL);
	}

	public void setSOAPRepositoryStrategy(SOAPStrategy repositoryStrategy) {
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
		repository = new SOAPRepository();
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		repository.setStrategy(experimentStrategy);

		repository.setNumberOfRequestsPerStep(REQUESTS_PER_STEP);
		repository.setNumberOfSteps(4);
		repository.setAnalyser(new ComposedAnalysis(new AggregatePerformance("SOAPRepository Aggregate Performance",
			new MeanChartCreator()), new SaveToXML(new File("results/degradation/new1ProcessSOAPRepositoryResults.xml"))));

//		old
//		experimentStrategy.setParameterInitialValue(50);
//		experimentStrategy.setFunction(new LinearIncrease(25));
//		new
		experimentStrategy.setParameterInitialValue(2);
		experimentStrategy.setFunction(new LinearIncrease(2));
		startExperiment(true, "processSOAPRepository", new ProcessSOAPRepository());

//		experimentStrategy.setParameterInitialValue(300);
//		experimentStrategy.setFunction(new LinearIncrease(600));
//		startExperiment(false, "repositoriesOf", new RepositoriesOf());
//		startExperiment(false, "supportedSOAPRepositoryTypes", new SupportedSOAPRepositoryTypes());
//		startExperiment(false, "cancelSOAPProcessingOfSOAPRepository", new CancelSOAPProcessingOfSOAPRepository());
//		experimentStrategy.setParameterInitialValue(150);
//		experimentStrategy.setFunction(new LinearIncrease(200));
//		startExperiment(false, "saveSOAPRepository", new SaveSOAPRepository());
//		startExperiment(true, "deleteSOAPRepository", new DeleteSOAPRepository(REQUESTS_PER_STEP));
	}

	private static void startExperiment(boolean plotGraph, String label, SOAPStrategy strategy)
		throws Exception {
		strategy.setWsClient(kalibroClient);
		repository.setSOAPRepositoryStrategy(strategy);
		repository.run(label, plotGraph);
	}
}
