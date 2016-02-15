package org.mezuro.scalability_tests.strategy;

import java.util.Map;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.WSClient;

public abstract class SOAPStrategy implements Strategy<Item> {

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

	public void configure(Map<Object, Object> serviceConfiguration) {
		// TODO Auto-generated method stub
	}
}
