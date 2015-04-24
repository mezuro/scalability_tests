package support;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;

public abstract class RESTStrategy {

	protected WSClient wsClient;

	public void setWsClient(WSClient wsClient) {
		this.wsClient = wsClient;
	}

	public abstract Item request(Item item) throws Exception;

	public Item beforeRequest() throws Exception {
		return null;
	}

	public void afterRequest(Item requestResponse) throws Exception {}

	public void afterStep() throws Exception {}

	public void beforeStep() throws Exception {}

	public void beforeExperiment() throws Exception {}

	public void afterExperiment() throws Exception {}
}
