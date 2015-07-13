package composed;

import runner.KalibroExperiment;
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

public class ComposedAggregationExperiment extends KalibroExperiment {
	
	public void setAttributes(TestConfiguration configuration, RESTStrategy subject) throws Exception {
		super.setAttributes(configuration, subject);
		configureExperiment();
	}
	
	public void afterIteration() throws Exception {
		super.subject.afterStep();
		subject.changeToNextUrl();
	}
	
	private void configureExperiment() throws Exception {
		this.setDeployer(new RESTKalibroDeployer());
		
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(configuration.initialCapacityValue);
		capacityStrategy.setFunction(configuration.increaseCapacityFunctionObject);
		
		ExperimentStrategy workloadStrategy = new WorkloadScaling();
		workloadStrategy.setParameterInitialValue(configuration.initialWorkloadValue);
		workloadStrategy.setFunction(configuration.increaseWorkloadFunctionObject);

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		
		this.setStrategy(composedStrategy);
	
		this.setNumberOfRequestsPerStep(configuration.requestsPerStep);
		this.setNumberOfSteps(configuration.numberOfSteps);
		this.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Composed Aggregation Performance",
			new MeanChartCreator())));
	}
}