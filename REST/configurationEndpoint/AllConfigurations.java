package configurationEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllConfigurations extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("allConfigurations");
	}

}
