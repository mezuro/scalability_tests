package configurationEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetSOAPConfiguration extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPConfiguration", "1");
	}

}
