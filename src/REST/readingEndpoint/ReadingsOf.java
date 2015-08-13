package REST.readingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class ReadingsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("readingsOf", "1");
	}

}
