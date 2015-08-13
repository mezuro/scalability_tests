package SOAP.readingGroupEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class GetSOAPReadingGroup extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPReadingGroup", "1");
	}

}
