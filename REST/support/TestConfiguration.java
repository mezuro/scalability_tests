package support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import eu.choreos.vv.increasefunctions.QuadraticIncrease;
import eu.choreos.vv.increasefunctions.ScalabilityFunction;

public class TestConfiguration {
	public static int requestsPerStep, numberOfSteps, initialCapacityValue, increaseCapacityFunctionParameter, initialWorkloadValue, increaseWorkloadFunctionParameter;
	public static ScalabilityFunction increaseCapacityFunctionObject, increaseWorkloadFunctionObject;
	public static String subjectName, increaseWorkloadFunction, increaseCapacityFunction;
	public static String metric;
	public static boolean plotGraph;
	
	public TestConfiguration(String filename) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		readParameters(filename);
	}

	private static void readParameters(String filename)
			throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<Object, Object> parameters = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(filename)));
		metric = (String) parameters.get("type");
		requestsPerStep = (Integer) parameters.get("requestsPerStep");
		numberOfSteps = (Integer) parameters.get("numberOfSteps");

		initialCapacityValue = (Integer) parameters.get("initialCapacityValue");
		increaseCapacityFunction = ((String) parameters.get("increaseCapacityFunction")).toLowerCase();
		increaseCapacityFunctionParameter = (Integer) parameters.get("increaseCapacityFunctionParameter");

		initialWorkloadValue = (Integer) parameters.get("initialWorkloadValue");
		increaseWorkloadFunction = ((String) parameters.get("increaseWorkloadFunction")).toLowerCase();
		increaseWorkloadFunctionParameter = (Integer) parameters.get("increaseWorkloadFunctionParameter");
				
		increaseCapacityFunctionObject = getIncreaseFunction(increaseCapacityFunction, increaseCapacityFunctionParameter);
		increaseWorkloadFunctionObject = getIncreaseFunction(increaseWorkloadFunction, increaseWorkloadFunctionParameter);
		
		plotGraph = (Boolean) parameters.get("plotGraph");
		subjectName = (String) parameters.get("experientName");
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
}
