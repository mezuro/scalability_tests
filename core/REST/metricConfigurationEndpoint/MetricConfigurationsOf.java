package REST.metricConfigurationEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class MetricConfigurationsOf extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("metricConfigurationsOf", "1");
	}

}
