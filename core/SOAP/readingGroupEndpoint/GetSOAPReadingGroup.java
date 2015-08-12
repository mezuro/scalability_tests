package SOAP.readingGroupEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetSOAPReadingGroup extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPReadingGroup", "1");
	}

}
