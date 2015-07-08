package support;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import eu.choreos.vv.increasefunctions.QuadraticIncrease;
import eu.choreos.vv.increasefunctions.ScalabilityFunction;

public class TestConfiguration {
	private static final String TEST_CONFIG_YML = "test_configuration.yml";
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
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
		metric = bufferedReader.readLine();
		requestsPerStep = readInteger(bufferedReader);
		numberOfSteps = readInteger(bufferedReader);

		initialCapacityValue = readInteger(bufferedReader);
		increaseCapacityFunction = bufferedReader.readLine().toLowerCase();
		increaseCapacityFunctionParameter = readInteger(bufferedReader);

		initialWorkloadValue = readInteger(bufferedReader);
		increaseWorkloadFunction = bufferedReader.readLine().toLowerCase();
		increaseWorkloadFunctionParameter = readInteger(bufferedReader);
				
		increaseCapacityFunctionObject = getIncreaseFunction(increaseCapacityFunction, increaseCapacityFunctionParameter);
		increaseWorkloadFunctionObject = getIncreaseFunction(increaseWorkloadFunction, increaseWorkloadFunctionParameter);
		
		plotGraph = Boolean.parseBoolean(bufferedReader.readLine());
		subjectName = bufferedReader.readLine();
		
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
}
