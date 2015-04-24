package readingEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetReading extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getReading", "1");
	}

}
