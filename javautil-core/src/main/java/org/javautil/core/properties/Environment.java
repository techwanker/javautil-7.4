package org.javautil.core.properties;

import java.util.Map;

public class Environment {

	public static void setProperties() {
		Map<String, String> environment = System.getenv();
		for (String key : environment.keySet()) {
			System.setProperty(key, environment.get(key));
		}
	}

}
