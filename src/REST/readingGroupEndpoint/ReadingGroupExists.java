package REST.readingGroupEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class ReadingGroupExists extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("readingGroupExists", "1");
	}

}
