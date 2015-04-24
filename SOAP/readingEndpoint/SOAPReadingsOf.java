package readingEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPReadingsOf extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingsOf", "1");
	}

}
