package REST.projectEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;
import strategy.RESTStrategy;

public class AllProjects extends RESTStrategy {

	@Override
	public Item request(Item item) throws InvalidOperationNameException, FrameworkException {
		return rsClient.request("allProjects");
	}

}
