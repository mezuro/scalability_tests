package runner;

import java.util.ArrayList;
import java.util.Map;

import support.RESTStrategy;
import support.TestConfiguration;
import eu.choreos.vv.experiments.Experiment;

public abstract class KalibroExperiment extends Experiment<String, String> {
	protected RESTStrategy subject;
	protected ArrayList<String> baseUris;
	protected String basePath;
	protected int port;
	protected TestConfiguration configuration;
	
	protected void setAttributes(TestConfiguration configuration, RESTStrategy subject) throws Exception {
		this.configuration = configuration;
		this.subject = subject;
		//this.baseUris = (ArrayList<String>) configParameters.get("base_uris");
		//this.basePath = (String) configParameters.get("base_path");
		//this.port = (Integer) configParameters.get("port");
	}
	
	public void start() throws Exception {
		this.run(this.configuration.subjectName, this.configuration.plotGraph);
	}

	@Override
	public void beforeExperiment() throws Exception {
		subject.beforeExperiment();
	}

	@Override
	public void afterExperiment() throws Exception {
		subject.afterExperiment();
	}

	// FIXME: methods with the same name 
	@Override
	public void beforeIteration() throws Exception {
		subject.beforeStep();
	}
	
	@Override
	public void afterIteration() throws Exception {
		subject.afterStep();
	}
	
	@Override
	public String beforeRequest() throws Exception {
		return subject.beforeRequest();
	}

	@Override
	public String request(String string) throws Exception {
		return subject.request(string);
	}
	
	@Override
	public void afterRequest(String requestResponse) throws Exception {
		subject.afterRequest(requestResponse);
	}
}
