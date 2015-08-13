package SOAP.repositoryEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class SupportedSOAPRepositoryTypes extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("supportedSOAPRepositoryTypes");
	}

}
