package speedup;

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
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.LinearIncrease;
import eu.choreos.vv.increasefunctions.ScalabilityFunction;

public class TestRunner {
	private static final String CONFIG_YML = "speedup_config.yml";
	private static int requestsPerStep, numberOfSteps, initialValue;
	private static ScalabilityFunction increaseFunctionObject;
	private static String experimentName;
	private static boolean plotGraph;
	private static RESTStrategy experimentSubject;
	private static SpeedupTestRunner speedupTestRunner;
	
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
		initialValue = readInteger(bufferedReader);
				
		increaseFunctionObject = new LinearIncrease(1);
		
		plotGraph = Boolean.parseBoolean(bufferedReader.readLine());
		experimentName = bufferedReader.readLine();
		String[] splittedExperimentName = StringUtils.splitByCharacterTypeCamelCase(experimentName);
		String endpointName = splittedExperimentName[splittedExperimentName.length-1].toLowerCase();
		experimentSubject = (RESTStrategy) Class.forName(endpointName+"Endpoint."+StringUtils.capitalize(experimentName)).newInstance();
		bufferedReader.close();
	}
	
	private static int readInteger(BufferedReader bufferedReader) throws IOException {
		return Integer.parseInt(bufferedReader.readLine());
	}
	
	private static void configureExperiment(Map<Object, Object> configParameters) throws Exception {
		speedupTestRunner = new SpeedupTestRunner(experimentSubject, configParameters);
		speedupTestRunner.setDeployer(new RESTKalibroDeployer());
		
		ExperimentStrategy experimentStrategy = new ParameterScaling("");
		experimentStrategy.setParameterInitialValue(initialValue);
		experimentStrategy.setFunction(increaseFunctionObject);
		
		speedupTestRunner.setStrategy(experimentStrategy);
	
		speedupTestRunner.setNumberOfRequestsPerStep(requestsPerStep);
		speedupTestRunner.setNumberOfSteps(numberOfSteps);
		speedupTestRunner.setAnalyser(new ComposedAnalysis(new AggregatePerformance("Speedup Performance",
			new MeanChartCreator())));
	}
	
	private static void startExperiment(boolean plotGraph, String label, RESTStrategy strategy)
		throws Exception {
		strategy.setRsClient(speedupTestRunner.getKalibroClient());
		speedupTestRunner.run(label, plotGraph);
	}
}
