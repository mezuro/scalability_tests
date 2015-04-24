package metricConfigurationEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetMetricConfiguration extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("getMetricConfiguration", "1");
	}

}
