package org.mezuro.scalability_tests.REST.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.choreos.vv.deployment.Deployer;

public class RESTKalibroDeployer implements Deployer {

	private String[] processorsIPs = {"10.0.0.9", "10.0.0.8", "10.0.0.14"};
	private int index;

	@Override
	public void deploy() throws Exception {
		// Don't need this method
	}

	@Override
	public List<String> getServiceUris(String serviceName) {
		List<String> currentUri = new ArrayList<String>();
		currentUri.add("http://" + processorsIPs[index] + ":8080/KalibroService/" + serviceName + "Endpoint/?wsdl");
		return currentUri;
	}

//	FIXME: See how to override this method
//	@Override
//	public void scale(int i) throws Exception {
//		this.index = i - 1;
//	}

	@Override
	public void scale(Map<String, Object> arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
