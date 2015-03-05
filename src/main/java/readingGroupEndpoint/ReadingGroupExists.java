package readingGroupEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class ReadingGroupExists extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingGroupExists", "1");
	}

}
