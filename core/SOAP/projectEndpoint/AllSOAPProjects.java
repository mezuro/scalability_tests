package SOAP.projectEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;

public class AllSOAPProjects extends SOAPStrategy {

	@Override
	public Item request(Item item) throws InvalidOperationNameException, FrameworkException {
		return wsClient.request("allSOAPProjects");
	}

}
