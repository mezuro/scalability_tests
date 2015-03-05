package readingGroupEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllReadingGroups extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allReadingGroups");
	}

}
