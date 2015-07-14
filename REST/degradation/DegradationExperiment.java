package degradation;

import runner.KalibroExperiment;
import support.RESTStrategy;
import support.TestConfiguration;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;

public class DegradationExperiment extends KalibroExperiment {
	public void setAttributes(TestConfiguration configuration, RESTStrategy subject) throws Exception {
		super.setAttributes(configuration, subject);
		configureExperiment();
	}

	private void configureExperiment() throws Exception {
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		this.setStrategy(experimentStrategy);
	
		this.setNumberOfRequestsPerStep(configuration.requestsPerStep);
		this.setNumberOfSteps(configuration.numberOfSteps);
		this.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Aggregate Performance",
			new MeanChartCreator())));
		
		experimentStrategy.setParameterInitialValue(configuration.initialWorkloadValue);
		experimentStrategy.setFunction(configuration.increaseWorkloadFunctionObject);
	}
}
