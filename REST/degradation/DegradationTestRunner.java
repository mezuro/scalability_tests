package degradation;

import java.util.Map;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;

public class DegradationTestRunner extends Experiment<String, String> {
	private RESTStrategy experimentSubject;
	
	static RSClient kalibroClient;
	
	public DegradationTestRunner(RESTStrategy experimentSubject, Map<Object, Object> configParameters) throws Exception {
		this.experimentSubject = experimentSubject;
		kalibroClient = new RSClient((String) configParameters.get("base_uri"), (String) configParameters.get("base_path"), (Integer) configParameters.get("port"));
	}
	
	public RSClient getKalibroClient() {
		return kalibroClient;
	}

	@Override
	public void afterRequest(String requestResponse) throws Exception {
		experimentSubject.afterRequest(requestResponse);
	}

	@Override
	public void beforeExperiment() throws Exception {
		experimentSubject.beforeExperiment();
	}

	@Override
	public String beforeRequest() throws Exception {
		return experimentSubject.beforeRequest();
	}

	@Override
	public void afterIteration() throws Exception {
		experimentSubject.afterStep();
	}

	@Override
	public void beforeIteration() throws Exception {
		experimentSubject.beforeStep();
	}

	@Override
	public void afterExperiment() throws Exception {
		experimentSubject.afterExperiment();
	}

	@Override
	public String request(String string) throws Exception {
		return experimentSubject.request(string);
	}

}
