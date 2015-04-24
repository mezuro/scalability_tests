package baseToolEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetBaseTool extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getBaseTool", "Analizo");
	}

}
