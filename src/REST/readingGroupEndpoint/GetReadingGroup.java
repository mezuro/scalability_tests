package REST.readingGroupEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetReadingGroup extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getReadingGroup", "1");
	}

}
