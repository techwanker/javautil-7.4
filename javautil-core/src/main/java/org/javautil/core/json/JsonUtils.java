package org.javautil.core.json;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {

	private final Yaml yaml       = new Yaml();

	private final Gson gson       = new Gson();

	private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	public String yamlToJson(String yamlString) {

		Object yamlObj = yaml.load(yamlString);
		String json = gson.toJson(yamlObj);
		return json;
	}

	public String yamlToPrettyJson(String yamlString) {

		Object yamlObj = yaml.load(yamlString);
		String json = prettyGson.toJson(yamlObj);
		return json;
	}

}
