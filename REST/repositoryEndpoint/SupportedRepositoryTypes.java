package repositoryEndpoint;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;

public class SupportedRepositoryTypes extends RESTStrategy {

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("supportedRepositoryTypes");
	}

}
