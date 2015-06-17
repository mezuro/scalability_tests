package composed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import support.RESTKalibroDeployer;
import support.RESTStrategy;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.experiments.strategy.WorkloadScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import eu.choreos.vv.increasefunctions.QuadraticIncrease;
import eu.choreos.vv.increasefunctions.ScalabilityFunction;

public class TestRunner {
	private static final String CONFIG_YML = "speedup_config.yml";
	private static int requestsPerStep, numberOfSteps, initialCapacityValue, initialWorkloadValue, increaseWorkloadFunctionParameter;
	private static ScalabilityFunction increaseCapacityFunctionObject, increaseWorkloadFunctionObject;
	private static String experimentName, increaseWorkloadFunction;
	private static boolean plotGraph;
	private static RESTStrategy experimentSubject;
	private static ComposedAggregationTestRunner composedAggregationTestRunner;
	
	public static void main(String[] args)
		throws Exception {
		String requestMethod = args[0];
		readParameters(args[1]);
		Map<Object, Object> configParameters = extractConfigParameters();
		configureExperiment(configParameters);
		
		try {
			startExperiment(plotGraph, experimentName, experimentSubject);
		}
		catch (Exception error) {
			error.printStackTrace();
		}
	}

	private static Map<Object, Object> extractConfigParameters() throws FileNotFoundException {
		Map<Object, Object> yaml = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(CONFIG_YML)));
		Map<Object, Object> configParameters = (Map<Object, Object>) yaml.get("kalibro_processor");
		return configParameters;
	}
	
	private static void readParameters(String filename)
			throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		requestsPerStep = readInteger(bufferedReader);
		numberOfSteps = readInteger(bufferedReader);
		initialCapacityValue = readInteger(bufferedReader);
		initialWorkloadValue = readInteger(bufferedReader);
		increaseWorkloadFunction = bufferedReader.readLine().toLowerCase();
		increaseWorkloadFunctionParameter = readInteger(bufferedReader);
				
		increaseCapacityFunctionObject = new LinearIncrease(1);
		increaseWorkloadFunctionObject = getIncreaseFunction(increaseWorkloadFunction, increaseWorkloadFunctionParameter);
		
		plotGraph = Boolean.parseBoolean(bufferedReader.readLine());
		experimentName = bufferedReader.readLine();
		String[] splittedExperimentName = StringUtils.splitByCharacterTypeCamelCase(experimentName);
		String endpointName = splittedExperimentName[splittedExperimentName.length-1].toLowerCase();
		experimentSubject = (RESTStrategy) Class.forName(endpointName+"Endpoint."+StringUtils.capitalize(experimentName)).newInstance();
		bufferedReader.close();
	}

	private static ScalabilityFunction getIncreaseFunction(String increaseFunction, int increaseFunctionParameter) {
		ScalabilityFunction increaseFunctionObject = null;
		if (increaseFunction.startsWith("linear")) {
			increaseFunctionObject = new LinearIncrease(increaseFunctionParameter);
		} else if (increaseFunction.startsWith("exponential")) {
			increaseFunctionObject = new ExponentialIncrease(increaseFunctionParameter);
		} else if (increaseFunction.startsWith("quadratic")) {
			increaseFunctionObject = new QuadraticIncrease(increaseFunctionParameter);
		} else {
			System.out.println("Wrong argument for increase function: " + increaseFunction + "\nExpected: linear, exponential or quadratic");
		}
		return increaseFunctionObject;
	}
	
	private static int readInteger(BufferedReader bufferedReader) throws IOException {
		return Integer.parseInt(bufferedReader.readLine());
	}
	
	private static void configureExperiment(Map<Object, Object> configParameters) throws Exception {
		composedAggregationTestRunner = new ComposedAggregationTestRunner(experimentSubject, configParameters);
		composedAggregationTestRunner.setDeployer(new RESTKalibroDeployer());
		
		ExperimentStrategy capacityStrategy = new ParameterScaling("");
		capacityStrategy.setParameterInitialValue(initialCapacityValue);
		capacityStrategy.setFunction(increaseCapacityFunctionObject);
		
		ExperimentStrategy workloadStrategy = new WorkloadScaling();
		workloadStrategy.setParameterInitialValue(initialWorkloadValue);
		workloadStrategy.setFunction(increaseWorkloadFunctionObject);

		ExperimentStrategy composedStrategy = new ComposedStrategy(capacityStrategy, workloadStrategy);
		
		composedAggregationTestRunner.setStrategy(composedStrategy);
	
		composedAggregationTestRunner.setNumberOfRequestsPerStep(requestsPerStep);
		composedAggregationTestRunner.setNumberOfSteps(numberOfSteps);
		composedAggregationTestRunner.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Composed Aggregation Performance",
			new MeanChartCreator())));
	}
	
	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(composedAggregationTestRunner.getKalibroClient());
		composedAggregationTestRunner.run(label, plotGraph);
	}
}
