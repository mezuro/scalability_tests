package baseToolEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPGetBaseTool extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getBaseTool", "Analizo");
	}

}
