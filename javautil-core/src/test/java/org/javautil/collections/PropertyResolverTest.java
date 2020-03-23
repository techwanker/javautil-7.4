package org.javautil.collections;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.javautil.core.collections.MapOfProperties;
import org.javautil.core.collections.PropertiesResolver;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyResolverTest {
	
//	    driver_class: "org.postgresql.Driver"
//	    url: "jdbc:postgresql://localhost/sales_reporting"
//	    username: "${USER}"
//	    password: ""
	private static final String integrationPostgres = "integration_postgres";
	private static final String userName = "username";
	private static final String iAm = System.getenv("USER");
	private final Logger logger  = LoggerFactory.getLogger(getClass());
	
	  LinkedHashMap<String, Object> getProperties() {
		 LinkedHashMap<String,Object> properties = new LinkedHashMap<>();
		 properties.put("driver_class","org.postgresql.Driver");
		 properties.put("url", "jdbc:postgresql://localhost/sales_reporting");
		 properties.put(userName, "${USER}");
		 return properties;

	 }
	
	 LinkedHashMap<String, Object> getPropertiesNoEnv() {
		 LinkedHashMap<String,Object> properties = new LinkedHashMap<>();
		 properties.put("driver_class","org.postgresql.Driver");
		 properties.put("url", "jdbc:postgresql://localhost/sales_reporting");
		 properties.put(userName, "${NONE}");
		 return properties;

	 }
	@Test
	public void testUser() {
		 Map<String, Object> map  = getProperties();
		 logger.info("map.size {}", map.size());
			LinkedHashMap<String, Object> resolved = PropertiesResolver.resolveEnvironment(map);
			assertEquals(map.size(),resolved.size());
			String resolvedUser = (String) resolved.get(userName);
			assertEquals(iAm,resolvedUser);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testUserNone() {
		 Map<String, Object> map  = getPropertiesNoEnv();
			LinkedHashMap<String, Object> resolved = PropertiesResolver.resolveEnvironment(map);
			String resolvedUser = (String) resolved.get(userName);
			assertEquals(iAm,resolvedUser);
	}
}
