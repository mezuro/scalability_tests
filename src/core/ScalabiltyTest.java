package core;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import REST.support.RESTStrategy;
import SOAP.support.SOAPStrategy;

public class ScalabiltyTest {
	private static final String SERVICES_CONFIG_YML = "services_configuration.yml";
	private static TestConfiguration testConfiguration;
	private static Map<Object, Object> serviceConfiguration;
	private static KalibroExperiment experiment;
	private static String architecture;
	private static Object subject;

	public static void main(String[] args) throws Exception {
		serviceConfiguration = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(SERVICES_CONFIG_YML)));
		architecture = args[0].toUpperCase();
		testConfiguration = new TestConfiguration(args[1]);
		
		subject = initSubject(testConfiguration.getSubjectName()).newInstance();
		if (architecture == "REST")			
			((RESTStrategy) subject).configure(serviceConfiguration);
		else
			((SOAPStrategy) subject).configure(serviceConfiguration);
		
		Class<?> experimentClass = Class.forName(architecture+"."+testConfiguration.getMetric()+"Experiment");
		experiment = (KalibroExperiment) experimentClass.newInstance();
		experiment.setAttributes(testConfiguration, subject);
		experiment.start();
	}

	private static Class<?> initSubject(String subjectName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String[] splittedSubjectName = StringUtils.splitByCharacterTypeCamelCase(subjectName);
		String endpointName = splittedSubjectName[splittedSubjectName.length-1].toLowerCase();
		return Class.forName(architecture+"."+endpointName+"Endpoint."+StringUtils.capitalize(subjectName));
	}
}
