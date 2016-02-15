package org.mezuro.scalability_tests.SOAP.projectEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.exceptions.FrameworkException;
import eu.choreos.vv.exceptions.InvalidOperationNameException;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class AllSOAPProjects extends SOAPStrategy {

	@Override
	public Item request(Item item) throws InvalidOperationNameException, FrameworkException {
		return wsClient.request("allSOAPProjects");
	}

}
