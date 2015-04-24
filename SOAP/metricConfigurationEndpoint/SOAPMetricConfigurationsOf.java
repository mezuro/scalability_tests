package metricConfigurationEndpoint;

import support.SOAPStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SOAPMetricConfigurationsOf extends SOAPStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("metricSOAPConfigurationsOf", "1");
	}

}
