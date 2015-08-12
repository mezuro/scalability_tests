package SOAP.repositoryEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SupportedSOAPRepositoryTypes extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("supportedSOAPRepositoryTypes");
	}

}
