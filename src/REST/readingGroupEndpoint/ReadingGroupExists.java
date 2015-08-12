package REST.readingGroupEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class ReadingGroupExists extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("readingGroupExists", "1");
	}

}
