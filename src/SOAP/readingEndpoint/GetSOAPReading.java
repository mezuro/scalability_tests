package SOAP.readingEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetSOAPReading extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPReading", "1");
	}

}
