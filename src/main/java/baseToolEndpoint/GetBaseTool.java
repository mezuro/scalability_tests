package baseToolEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetBaseTool extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getBaseTool", "Analizo");
	}

}
