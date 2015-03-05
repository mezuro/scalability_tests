package configurationEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllConfigurations extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allConfigurations");
	}

}
