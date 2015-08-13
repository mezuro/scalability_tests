package REST.metricConfigurationEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class MetricConfigurationsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("metricConfigurationsOf", "1");
	}

}
