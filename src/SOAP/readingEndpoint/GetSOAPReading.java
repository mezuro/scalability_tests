package SOAP.readingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class GetSOAPReading extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPReading", "1");
	}

}
