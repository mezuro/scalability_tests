package readingGroupEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllReadingGroups extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allReadingGroups");
	}

}
