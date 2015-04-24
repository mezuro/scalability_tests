package configurationEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class ConfigurationExists extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("configurationExists", "1");
	}

}
