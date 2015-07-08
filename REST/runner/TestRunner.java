package runner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import eu.choreos.vv.experiments.Experiment;
import support.RESTStrategy;
import support.TestConfiguration;

public class TestRunner {
	private static final String SERVICES_CONFIG_YML = "services_configuration.yml";
	public static Map<Object, Object> kalibroProcessorConfigurationParameters;
	public static Map<Object, Object> kalibroConfigurationConfigurationParameters;
	private static TestConfiguration testConfiguration;
	private static KalibroExperiment experiment;
	private static RESTStrategy subject;

	public static void main(String[] args) throws Exception {
		configureServices();
		testConfiguration = new TestConfiguration(args[1]);
		setSubject(testConfiguration.subjectName);
		
		Class<?> experimentClass = Class.forName(StringUtils.capitalize(testConfiguration.metric)+"Experiment");
		experiment = (KalibroExperiment) experimentClass.newInstance();
		experiment.setAttributes(testConfiguration, subject);
		experiment.start();
	}

	private static void setSubject(String subjectName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String[] splittedSubjectName = StringUtils.splitByCharacterTypeCamelCase(subjectName);
		String endpointName = splittedSubjectName[splittedSubjectName.length-1].toLowerCase();
		subject = (RESTStrategy) Class.forName(endpointName+"Endpoint."+StringUtils.capitalize(subjectName)).newInstance();
	}
	
	private static void configureServices() throws Exception {
		Map<Object, Object> yaml = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(SERVICES_CONFIG_YML)));
		kalibroProcessorConfigurationParameters = (Map<Object, Object>) yaml.get("kalibro_processor");
		kalibroConfigurationConfigurationParameters = (Map<Object, Object>) yaml.get("kalibro_processor");
	}

}
