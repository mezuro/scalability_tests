package support;

import eu.choreos.vv.clientgenerator.RSClient;

public abstract class RESTStrategy {

	protected RSClient rsClient;

	public void setRsClient(RSClient rsClient) {
		this.rsClient = rsClient;
	}

	public abstract String request(String string) throws Exception;

	public String beforeRequest() throws Exception {
		return null;
	}

	public void afterRequest(String requestResponse) throws Exception {}

	public void afterStep() throws Exception {}

	public void beforeStep() throws Exception {}

	public void beforeExperiment() throws Exception {}

	public void afterExperiment() throws Exception {}
}
