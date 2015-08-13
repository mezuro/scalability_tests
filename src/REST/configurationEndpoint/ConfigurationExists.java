package REST.configurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class ConfigurationExists extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("configurationExists", "1");
	}

}
