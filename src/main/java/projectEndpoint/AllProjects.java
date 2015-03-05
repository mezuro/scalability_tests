package projectEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class AllProjects extends Strategy {

	@Override
	public Item request(Item item) throws InvalidOperationNameException, FrameworkException {
		return wsClient.request("allProjects");
	}

}
