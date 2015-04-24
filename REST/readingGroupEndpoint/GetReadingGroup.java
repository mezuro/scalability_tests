package readingGroupEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetReadingGroup extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getReadingGroup", "1");
	}

}
