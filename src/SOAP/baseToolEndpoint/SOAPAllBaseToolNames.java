package SOAP.baseToolEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPAllBaseToolNames extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allBaseToolNames");
	}

}
