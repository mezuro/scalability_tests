package org.mezuro.scalability_tests.core;

import org.mezuro.scalability_tests.REST.support.RESTKalibroDeployer;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import org.mezuro.scalability_tests.strategy.Strategy;

public class SpeedupExperiment<T> extends KalibroExperiment<T> {
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
		
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(configuration.getInitialCapacityValue());
		experimentStrategy.setFunction(configuration.getIncreaseCapacityFunctionObject());
		
		this.setStrategy(experimentStrategy);
	
		this.setNumberOfRequestsPerStep(configuration.getRequestsPerStep());
		this.setNumberOfSteps(configuration.getNumberOfSteps());
		this.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Speedup Performance",
			new MeanChartCreator())));
	}
}
