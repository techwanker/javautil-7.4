package org.javautil.collections;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.javautil.core.collections.MapOfProperties;
import org.junit.Test;

public class MapOfPropertiesTest {
	
//	"integration_postgres":
//	    driver_class: "org.postgresql.Driver"
//	    url: "jdbc:postgresql://localhost/sales_reporting"
//	    username: "${USER}"
//	    password: ""
	private static final String integrationPostgres = "integration_postgres";
	private static final String userName = "username";
	private static final String iAm = System.getenv("USER");
	
	 Map<String, Map<String, Object>> getMapOfProperties() {
		 LinkedHashMap<String, Map<String, Object>> retval = new LinkedHashMap<>();
		 LinkedHashMap<String,Object> properties = new LinkedHashMap<>();
		 properties.put("driver_class","org.postgresql.Driver");
		 properties.put("url", "jdbc:postgresql://localhost/sales_reporting");
		 properties.put(userName, "${USER}");
		 retval.put(integrationPostgres,properties);
		 return retval;

	 }
	
	 Map<String, Map<String, Object>> getMapOfPropertiesNoEnv() {
		 LinkedHashMap<String, Map<String, Object>> retval = new LinkedHashMap<>();
		 LinkedHashMap<String,Object> properties = new LinkedHashMap<>();
		 properties.put("driver_class","org.postgresql.Driver");
		 properties.put("url", "jdbc:postgresql://localhost/sales_reporting");
		 properties.put(userName, "${NONE}");
		 retval.put(integrationPostgres,properties);
		 return retval;

	 }
	@Test
	public void testUser() {
		 Map<String, Map<String, Object>> map  = getMapOfProperties();
			Map<String, LinkedHashMap<String, Object>> resolved = MapOfProperties.resolveEnvironment(map);
			assertEquals(1,resolved.size());
			LinkedHashMap<String, Object> properties = resolved.get(integrationPostgres);
			String resolvedUser = (String) properties.get(userName);
			assertEquals(iAm,resolvedUser);
			
			
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testUserNone() {
		 Map<String, Map<String, Object>> map  = getMapOfPropertiesNoEnv();
			Map<String, LinkedHashMap<String, Object>> resolved = MapOfProperties.resolveEnvironment(map);
			assertEquals(1,resolved.size());
			LinkedHashMap<String, Object> properties = resolved.get(integrationPostgres);
			String resolvedUser = (String) properties.get(userName);
			assertEquals(iAm,resolvedUser);
			
			
	}
}
