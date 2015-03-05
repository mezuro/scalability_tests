package readingEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class ReadingsOf extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingsOf", "1");
	}

}
