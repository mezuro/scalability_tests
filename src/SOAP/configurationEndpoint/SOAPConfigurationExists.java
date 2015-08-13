package SOAP.configurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class SOAPConfigurationExists extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("configurationExists", "1");
	}

}
