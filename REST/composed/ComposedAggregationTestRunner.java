package composed;

import java.util.ArrayList;
import java.util.Map;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;

public class ComposedAggregationTestRunner extends Experiment<String, String> {
	private RESTStrategy experimentSubject;
	private ArrayList<String> baseUris;
	private String basePath;
	private int port;
	
	static RSClient kalibroClient;
	
	public ComposedAggregationTestRunner(RESTStrategy experimentSubject, Map<Object, Object> configParameters) throws Exception {
		this.experimentSubject = experimentSubject;
		this.baseUris = (ArrayList<String>) configParameters.get("base_uris");
		this.basePath = (String) configParameters.get("base_path");
		this.port = (Integer) configParameters.get("port");
		setKalibroClient();
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
		setKalibroClient();
	}

	private void setKalibroClient() throws IndexOutOfBoundsException {
		kalibroClient = new RSClient(baseUris.remove(0), basePath, port);
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
