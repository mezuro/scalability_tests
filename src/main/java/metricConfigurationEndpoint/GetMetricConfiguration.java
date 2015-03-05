package metricConfigurationEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetMetricConfiguration extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getMetricConfiguration", "1");
	}

}
