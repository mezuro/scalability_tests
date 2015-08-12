package SOAP.metricConfigurationEndpoint;

import SOAP.support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetSOAPMetricConfiguration extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPMetricConfiguration", "1");
	}

}
