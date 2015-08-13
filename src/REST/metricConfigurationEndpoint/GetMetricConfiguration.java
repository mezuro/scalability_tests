package REST.metricConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class GetMetricConfiguration extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getMetricConfiguration", "1");
	}

}
