package REST.repositoryEndpoint;

import eu.choreos.vv.clientgenerator.Item;
import strategy.RESTStrategy;

public class SupportedRepositoryTypes extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("supportedRepositoryTypes");
	}

}
