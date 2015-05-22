package degradation;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.RSClient;
import eu.choreos.vv.experiments.Experiment;

public class DegradationTestRunner extends Experiment<String, String> {
	private RESTStrategy experimentSubject;
	
//	Use YML files to receive these constants.
	private final String BASE_URI = "http://aguia1.ime.usp.br";
	private final int PORT = 8082;
	private final String BASE_PATH = "/";
	static RSClient kalibroClient;
	
	public DegradationTestRunner(RESTStrategy experimentSubject) throws Exception {
		this.experimentSubject = experimentSubject;
		kalibroClient = new RSClient(BASE_URI, BASE_PATH, PORT);
		
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
