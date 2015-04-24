package readingGroupEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class AllSOAPReadingGroups extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("allSOAPReadingGroups");
	}

}
