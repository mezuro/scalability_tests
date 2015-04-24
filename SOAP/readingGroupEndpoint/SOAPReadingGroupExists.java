package readingGroupEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPReadingGroupExists extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingGroupExists", "1");
	}

}
