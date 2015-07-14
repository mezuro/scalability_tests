package runner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import support.RESTStrategy;
import support.TestConfiguration;

public class TestRunner {
	private static final String SERVICES_CONFIG_YML = "services_configuration.yml";
	private static TestConfiguration testConfiguration;
	private static Map<Object, Object> serviceConfiguration;
	private static KalibroExperiment experiment;
	private static RESTStrategy subject;

	public static void main(String[] args) throws Exception {
		serviceConfiguration = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(SERVICES_CONFIG_YML)));
		testConfiguration = new TestConfiguration(args[1]);
		
		initSubject(testConfiguration.getSubjectName());
		subject.configure(serviceConfiguration);
		
		Class<?> experimentClass = Class.forName(testConfiguration.getMetric()+"Experiment");
		experiment = (KalibroExperiment) experimentClass.newInstance();
		experiment.setAttributes(testConfiguration, subject);
		experiment.start();
	}

	private static void initSubject(String subjectName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String[] splittedSubjectName = StringUtils.splitByCharacterTypeCamelCase(subjectName);
		String endpointName = splittedSubjectName[splittedSubjectName.length-1].toLowerCase();
		subject = (RESTStrategy) Class.forName(endpointName+"Endpoint."+StringUtils.capitalize(subjectName)).newInstance();	
	}	
}
