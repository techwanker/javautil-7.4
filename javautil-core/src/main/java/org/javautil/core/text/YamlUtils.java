package org.javautil.core.text;

import org.yaml.snakeyaml.Yaml;

public class YamlUtils {
	public static String asYaml(Object in) {
		Yaml yaml = new Yaml();
		String retval = yaml.dump(in);
		return retval;
	}
}
