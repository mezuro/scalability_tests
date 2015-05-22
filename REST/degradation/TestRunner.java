package degradation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import eu.choreos.vv.increasefunctions.QuadraticIncrease;
import eu.choreos.vv.increasefunctions.ScalabilityFunction;

public class TestRunner {
	
	private static int requestsPerStep, numberOfSteps, initialValue, increaseFunctionParameter;
	private static ScalabilityFunction increaseFunctionObject;
	private static String increaseFunction, experimentName;
	private static boolean plotGraph;
	private static RESTStrategy experimentSubject;
	private static DegradationTestRunner degradationTestRunner;
	
	public static void main(String[] args)
		throws Exception {
		String requestMethod = args[0];
		readParameters(args[1]);
		configureExperiment();
		startExperiment(plotGraph, experimentName, experimentSubject);
	}
	
	private static void readParameters(String filename)
			throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		requestsPerStep = readInteger(bufferedReader);
		numberOfSteps = readInteger(bufferedReader);
		initialValue = readInteger(bufferedReader);
		increaseFunction = bufferedReader.readLine().toLowerCase();
		increaseFunctionParameter = readInteger(bufferedReader);
				
		if (increaseFunction.startsWith("linear")) {
			increaseFunctionObject = new LinearIncrease(increaseFunctionParameter);
		} else if (increaseFunction.startsWith("exponential")) {
			increaseFunctionObject = new ExponentialIncrease(increaseFunctionParameter);
		} else if (increaseFunction.startsWith("quadratic")) {
			increaseFunctionObject = new QuadraticIncrease(increaseFunctionParameter);
		} else {
			System.out.println("Wrong argument for increase function: " + increaseFunction + "\nExpected: linear, exponential or quadratic");
		}
		plotGraph = Boolean.parseBoolean(bufferedReader.readLine());
		experimentName = bufferedReader.readLine();
		experimentSubject = (RESTStrategy) Class.forName("repositoryEndpoint."+StringUtils.capitalize(experimentName)).newInstance();
		bufferedReader.close();
	}
	
	private static int readInteger(BufferedReader bufferedReader) throws IOException {
		return Integer.parseInt(bufferedReader.readLine());
	}
	
	private static void configureExperiment() throws Exception {
		degradationTestRunner = new DegradationTestRunner(experimentSubject);
		ExperimentStrategy experimentStrategy = new WorkloadScaling();
		degradationTestRunner.setStrategy(experimentStrategy);
	
		degradationTestRunner.setNumberOfRequestsPerStep(requestsPerStep);
		degradationTestRunner.setNumberOfSteps(numberOfSteps);
		degradationTestRunner.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Aggregate Performance",
			new MeanChartCreator())));
		
		experimentStrategy.setParameterInitialValue(initialValue);
		experimentStrategy.setFunction(increaseFunctionObject);
	}
	
	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(degradationTestRunner.getKalibroClient());
		degradationTestRunner.run(label, plotGraph);
	}
}
