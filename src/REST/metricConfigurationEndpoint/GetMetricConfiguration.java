package REST.metricConfigurationEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class GetMetricConfiguration extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("getMetricConfiguration", "1");
	}

}
