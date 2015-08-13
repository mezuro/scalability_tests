package SOAP.readingGroupEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class AllSOAPReadingGroups extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allSOAPReadingGroups");
	}

}
