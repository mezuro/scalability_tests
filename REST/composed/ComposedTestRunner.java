package composed;

import java.util.Map;

import support.RESTKalibroDeployer;
import support.RESTStrategy;
import support.TestConfiguration;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;

public class ComposedTestRunner {
	private static RESTStrategy experimentSubject;
	private static ComposedAggregationExperiment composedAggregationTestRunner;
	private static TestConfiguration configuration;
	
	public static void main(String[] args)
		throws Exception {
		String requestMethod = args[0];
		configuration = new TestConfiguration(args[1]);
		configureExperiment();
		
		try {
			startExperiment(configuration.plotGraph, configuration.subjectName, experimentSubject);
		}
		catch (Exception error) {
			error.printStackTrace();
		}
	}
	
	private static void configureExperiment() throws Exception {
		composedAggregationTestRunner = new ComposedAggregationExperiment(experimentSubject, configuration.kalibroProcessorConfigurationParameters);
		composedAggregationTestRunner.setDeployer(new RESTKalibroDeployer());
		
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(configuration.initialCapacityValue);
		capacityStrategy.setFunction(configuration.increaseCapacityFunctionObject);
		
		ExperimentStrategy workloadStrategy = new WorkloadScaling();
		workloadStrategy.setParameterInitialValue(configuration.initialWorkloadValue);
		workloadStrategy.setFunction(configuration.increaseWorkloadFunctionObject);

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		
		composedAggregationTestRunner.setStrategy(composedStrategy);
	
		composedAggregationTestRunner.setNumberOfRequestsPerStep(configuration.requestsPerStep);
		composedAggregationTestRunner.setNumberOfSteps(configuration.numberOfSteps);
		composedAggregationTestRunner.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Composed Aggregation Performance",
			new MeanChartCreator())));
	}
	
	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(composedAggregationTestRunner.getKalibroClient());
		composedAggregationTestRunner.run(label, plotGraph);
	}
}
