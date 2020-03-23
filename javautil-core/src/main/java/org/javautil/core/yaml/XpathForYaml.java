package org.javautil.core.yaml;

import java.util.Map;

public class XpathForYaml {

	public static String[] getPaths(String path) {
		return path.split("/");
	}

	Object getPath(Map map, String fullpath) {
		Map myMap = map;
		String[] paths = getPaths(fullpath);
		int depth = 0;
		for (String path : paths) {
			myMap = (Map) map.get(path);
			if (myMap == null) {
				throw new IllegalArgumentException("cannot find '" + path + "' at depth " + depth);
			}
		}
		return myMap;
	}

}
