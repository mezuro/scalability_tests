package REST.baseToolEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllBaseToolNames extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allBaseToolNames");
	}

}
