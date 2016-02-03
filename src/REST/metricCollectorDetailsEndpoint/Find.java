package REST.metricCollectorDetailsEndpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import strategy.RESTStrategy;

public class Find extends RESTStrategy {
	
	private JSONObject jsonBody;

	@Override
	public void beforeExperiment() throws Exception {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", "MetricFu");
		jsonBody = new JSONObject(parameters);
	}

	@Override
	public String request(String string) throws Exception {
		post(buildUrl(METRIC_COLLECTOR_DETAILS_PATH + "/find"), jsonBody);
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_processor");
	}

}
