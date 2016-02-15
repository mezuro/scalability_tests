package org.mezuro.scalability_tests.core;

import org.mezuro.scalability_tests.REST.support.RESTKalibroDeployer;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import org.mezuro.scalability_tests.strategy.Strategy;

public class ComposedAggregationExperiment<T> extends KalibroExperiment<T> {
	public void setAttributes(TestConfiguration configuration, Strategy<T> subject) throws Exception {
		super.setAttributes(configuration, subject);
		configureExperiment();
	}
	
	public void afterIteration() throws Exception {
		super.subject.afterIteration();
		subject.changeToNextUrl();
	}
	
	private void configureExperiment() throws Exception {
		this.setDeployer(new RESTKalibroDeployer());
		
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(configuration.getInitialCapacityValue());
		capacityStrategy.setFunction(configuration.getIncreaseCapacityFunctionObject());
		
		ExperimentStrategy workloadStrategy = new WorkloadScaling();
		workloadStrategy.setParameterInitialValue(configuration.getInitialWorkloadValue());
		workloadStrategy.setFunction(configuration.getIncreaseWorkloadFunctionObject());

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		
		this.setStrategy(composedStrategy);
	
		this.setNumberOfRequestsPerStep(configuration.getRequestsPerStep());
		this.setNumberOfSteps(configuration.getNumberOfSteps());
		this.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Composed Aggregation Performance",
			new MeanChartCreator())));
	}
}
