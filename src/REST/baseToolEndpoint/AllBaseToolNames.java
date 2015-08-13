package REST.baseToolEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class AllBaseToolNames extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allBaseToolNames");
	}

}
