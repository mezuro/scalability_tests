package REST.readingGroupEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllReadingGroups extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allReadingGroups");
	}

}
