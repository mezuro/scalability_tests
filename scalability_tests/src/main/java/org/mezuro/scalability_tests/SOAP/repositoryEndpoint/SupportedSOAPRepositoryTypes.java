package org.mezuro.scalability_tests.SOAP.repositoryEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class SupportedSOAPRepositoryTypes extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("supportedSOAPRepositoryTypes");
	}

}
