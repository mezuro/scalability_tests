package baseToolEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllBaseToolNames extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allBaseToolNames");
	}

}
