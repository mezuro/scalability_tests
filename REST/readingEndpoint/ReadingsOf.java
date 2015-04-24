package readingEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class ReadingsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("readingsOf", "1");
	}

}
