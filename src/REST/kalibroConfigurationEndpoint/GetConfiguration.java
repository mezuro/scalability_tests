package REST.kalibroConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class GetConfiguration extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getConfiguration", "1");
	}

}
