package org.javautil.core.yaml;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlLoader {

	public YamlLoader() {
		// TODO Auto-generated constructor stub
	}

	// TODO document and test

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Map<String, Object>> loadYamlMapOfMaps(InputStream is) {
		final Yaml yaml = new Yaml();
		return (LinkedHashMap<String, Map<String, Object>>) yaml.load(is);
	}

}
