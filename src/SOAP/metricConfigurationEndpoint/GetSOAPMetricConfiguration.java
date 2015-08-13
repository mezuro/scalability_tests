package SOAP.metricConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.SOAPStrategy;

public class GetSOAPMetricConfiguration extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getSOAPMetricConfiguration", "1");
	}

}
