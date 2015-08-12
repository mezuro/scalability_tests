package REST.repositoryEndpoint;

import REST.support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SupportedRepositoryTypes extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("supportedRepositoryTypes");
	}

}
