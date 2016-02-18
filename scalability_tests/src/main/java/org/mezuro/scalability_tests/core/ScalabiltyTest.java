package org.mezuro.scalability_tests.core;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.RESTStrategy;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class ScalabiltyTest {
	private static final String SERVICES_CONFIG_YML = "services_configuration.yml";
	private static TestConfiguration testConfiguration;
	private static Map<Object, Object> serviceConfiguration;
	private static String architecture;

	public static void main(String[] args) throws Exception {
		serviceConfiguration = (Map<Object, Object>) new Yaml().load(new FileInputStream(new File(SERVICES_CONFIG_YML)));
		architecture = args[0].toUpperCase();
		testConfiguration = new TestConfiguration(args[1]);
		
		Class<?> experimentClass = Class.forName("org.mezuro.scalability_tests.core." + testConfiguration.getMetric() + "Experiment");
		if (architecture.equals("REST"))	{
			RESTStrategy restStrategy = (RESTStrategy) initSubject(testConfiguration.getSubjectName()).newInstance();
			restStrategy.configure(serviceConfiguration);
			KalibroExperiment<String> experiment = (KalibroExperiment<String>) experimentClass.newInstance();
			experiment.setAttributes(testConfiguration, restStrategy);
			experiment.start();
		}
		else {
			SOAPStrategy soapStrategy = (SOAPStrategy) initSubject(testConfiguration.getSubjectName()).newInstance();
			soapStrategy.configure(serviceConfiguration);
			KalibroExperiment<Item> experiment = (KalibroExperiment<Item>) experimentClass.newInstance();
			experiment.setAttributes(testConfiguration, soapStrategy);
			experiment.start();
		}
	}

	private static Class<?> initSubject(String subjectName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return Class.forName("org.mezuro.scalability_tests." + subjectName);
	}
}
