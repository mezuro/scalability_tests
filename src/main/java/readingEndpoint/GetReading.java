package readingEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetReading extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getReading", "1");
	}

}
