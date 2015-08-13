package SOAP.configurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class AllSOAPConfigurations extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allSOAPConfigurations");
	}

}
