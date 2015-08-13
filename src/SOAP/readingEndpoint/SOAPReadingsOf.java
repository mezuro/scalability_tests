package SOAP.readingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class SOAPReadingsOf extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingsOf", "1");
	}

}
