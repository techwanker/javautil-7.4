package org.javautil.core.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesResolver {

	private static final String environmentRegex = "\\$\\{(.*)\\}$";
	private static final Pattern   environmentPattern            = Pattern.compile(environmentRegex);
	private static final Logger logger  = LoggerFactory.getLogger(PropertiesResolver.class);


	public  static  LinkedHashMap<String, Object> resolveEnvironment(Map<String, Object>  nameValues) {
		ArrayList<String> unresolved = new ArrayList<>();
		//	LinkedHashMap<String, LinkedHashMap<String, Object>> retval = new LinkedHashMap<>();
		LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
		for (String propertyName : nameValues.keySet()) {
			Object propertyValue = nameValues.get(propertyName);
			String resolvedValue  = null;

			if (propertyValue instanceof String) {
				logger.debug("looking at name {} value {}", propertyName, propertyValue);
				String envName  = (String) propertyValue;
				final Matcher matcher = environmentPattern.matcher(envName);
				if (matcher.matches()) {
					logger.debug("matcher matches");
					final String grp = matcher.group(1);
					logger.debug("grp is {}",grp);
					resolvedValue = System.getenv(grp);
					if (resolvedValue == null) {
						unresolved.add(grp);
					}
					properties.put(propertyName, resolvedValue);
				} 
				else {
					properties.put(propertyName,propertyValue);
				}
			}
			else {
				properties.put(propertyName, propertyValue);
			}
		}
		logger.debug("properties before resolution {} after resolution  {}", nameValues, properties);
		if (unresolved.size() > 0) {
			throw new IllegalArgumentException("unresolved " + unresolved);
		}
		if (properties.size() != nameValues.size()) {
			String message = String.format("name.size: %d properties.size: %d", nameValues.size(), properties.size());
			logger.error(message);
			throw new IllegalStateException(message);
		}
		return properties;
	}
}