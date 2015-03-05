package support;

import java.util.List;
import java.util.Map;

import eu.choreos.vv.clientgenerator.WSClient;
import eu.choreos.vv.deployment.Deployer;

public class BalancerDepolyer implements Deployer {

	private final String BALANCER_WSDL = "http://10.0.0.14:8080/KalibroService/LoadBalancerEndpoint/?wsdl";
	private String[] processorsIPs = {"10.0.0.9", "10.0.0.18", "10.0.0.11", "10.0.0.19", "10.0.0.12"};
	private static WSClient balancerClient;

	public BalancerDepolyer() throws Exception {
		balancerClient = new WSClient(BALANCER_WSDL);
	}

	@Override
	public void deploy() throws Exception {
		balancerClient.request("deleteAllProcessors");
	}

//	FIXME: See how to override this method
//	@Override
//	public void scale(int index) throws Exception {
//		balancerClient.request("addProcessor", "http://" + processorsIPs[index - 1] + ":8080/KalibroService/");
//	}

	@Override
	public List<String> getServiceUris(String serviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scale(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
