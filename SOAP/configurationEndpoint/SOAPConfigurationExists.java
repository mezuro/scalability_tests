package configurationEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPConfigurationExists extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("configurationExists", "1");
	}

}
