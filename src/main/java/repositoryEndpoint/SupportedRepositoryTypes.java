package repositoryEndpoint;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;

public class SupportedRepositoryTypes extends Strategy {

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("supportedRepositoryTypes");
	}

}
