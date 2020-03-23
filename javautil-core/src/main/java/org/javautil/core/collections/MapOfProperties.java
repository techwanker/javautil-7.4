package org.javautil.core.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapOfProperties {


	private static final Logger logger  = LoggerFactory.getLogger(MapOfProperties.class);
	public  static LinkedHashMap<String, LinkedHashMap<String, Object>> resolveEnvironment(Map<String, Map<String, Object>>  inMap) {
		ArrayList<String> unresolved = new ArrayList<>();
		LinkedHashMap<String, LinkedHashMap<String, Object>> retval = new LinkedHashMap<>();
		for (String setName : inMap.keySet()) {
			logger.debug("looking at properties for {}", setName);;
			LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
			Map<String, Object> nameValues = inMap.get(setName);
			retval.put(setName,PropertiesResolver.resolveEnvironment(nameValues));
			
			logger.debug("properties {}", properties);
		}
		if (unresolved.size() > 0) {
			throw new IllegalArgumentException("unresolved " + unresolved);
		}
		return retval;
	}

//	public  static LinkedHashMap<String, LinkedHashMap<String, Object>> resolveEnvironment(Map<String, Map<String, Object>>  inMap) {
//		ArrayList<String> unresolved = new ArrayList<>();
//		LinkedHashMap<String, LinkedHashMap<String, Object>> retval = new LinkedHashMap<>();
//		for (String setName : inMap.keySet()) {
//			logger.debug("looking at properties for {}", setName);;
//			LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
//			retval.put(setName,properties);
//			Map<String, Object> nameValues = inMap.get(setName);
//			for (String propertyName : nameValues.keySet()) {
//				Object propertyValue = nameValues.get(propertyName);
//				String resolvedValue  = null;
//
//				if (propertyValue instanceof String) {
//					logger.debug("looking at name {} value {}", propertyName, propertyValue);
//					String envName  = (String) propertyValue;
//					final Matcher matcher = environmentPattern.matcher(envName);
//					if (matcher.matches()) {
//						logger.info("matcher matches");
//						final String grp = matcher.group(1);
//						logger.debug("grp is {}",grp);
//						resolvedValue = System.getenv(grp);
//						if (resolvedValue == null) {
//							unresolved.add(grp);
//						}
//						properties.put(propertyName, resolvedValue);
//					} 
//				}
//				else {
//					properties.put(propertyName, propertyValue);
//
//				}
//			}
//			logger.debug("properties {}", properties);
//		}
//		if (unresolved.size() > 0) {
//			throw new IllegalArgumentException("unresolved " + unresolved);
//		}
//		return retval;
//	}
}
