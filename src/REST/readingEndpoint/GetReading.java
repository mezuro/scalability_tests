package REST.readingEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class GetReading extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getReading", "1");
	}

}
