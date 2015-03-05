package configurationEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetConfiguration extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getConfiguration", "1");
	}

}
