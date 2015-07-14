package speedup;

import runner.KalibroExperiment;
import support.RESTKalibroDeployer;
import support.RESTStrategy;
import support.TestConfiguration;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;

public class SpeedupExperiment extends KalibroExperiment {	
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
