package metricConfigurationEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class MetricConfigurationsOf extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("metricConfigurationsOf", "1");
	}

}
