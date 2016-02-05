package REST.readingGroupEndpoint;

import java.util.Map;

import strategy.RESTStrategy;

public class Index extends RESTStrategy {

	@Override
	public String request(String string) throws Exception {
		get(buildUrl(READING_GROUP_PATH));
		return null;
	}

	@Override
	public void configure(Map<Object, Object> options) {
		configure(options, "kalibro_configuration");
	}

}