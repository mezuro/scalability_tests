package configurationEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllSOAPConfigurations extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allSOAPConfigurations");
	}

}
