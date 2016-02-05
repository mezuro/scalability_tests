package REST.projectEndpoint;

import java.util.Map;

import strategy.RESTStrategy;

public class Exists extends RESTStrategy {
	
	@Override
	public String request(String string) throws Exception {
		get(buildUrl(PROJECT_PATH + "/1/exists"));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
