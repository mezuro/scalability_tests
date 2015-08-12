package REST.readingEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetReading extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getReading", "1");
	}

}
