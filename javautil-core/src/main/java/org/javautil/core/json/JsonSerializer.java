package org.javautil.core.json;

import java.util.LinkedHashMap;

public interface JsonSerializer {

	// Map<String, Object> toMap(Object bean);

	Object toObjectFromJson(String json);

	Object toObjectFromJson(String json, Class<?> clazz);

	LinkedHashMap<String, Object> toMapFromJson(String json);

	//
	// public String toJson(Map map) {
	// return gson.toJson(map);
	// }
	//
	// public String toJsonPretty(Map map) {
	// return gsonPretty.toJson(map);
	// }
	//
	String toJsonPretty(Object o);

	JsonSerializer withNullValues();

	LinkedHashMap<String, Object> toMapFromBean(Object bean);

	String toJson(Object o);

}