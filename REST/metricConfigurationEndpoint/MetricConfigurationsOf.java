package metricConfigurationEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class MetricConfigurationsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("metricConfigurationsOf", "1");
	}

}
