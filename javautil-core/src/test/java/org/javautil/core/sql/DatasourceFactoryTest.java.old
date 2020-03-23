package org.javautil.sql;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.junit.Test;

public class DatasourceFactoryTest {
	
	@Test
	public void testPostgres() throws PropertyVetoException, SQLException {
		System.out.println("testing postgress");
		HashMap<String,Object> parms  = new HashMap<String,Object>();
		parms.put("driver_class", "org.postgresql.Driver");        
		parms.put( "url" ,"jdbc:postgresql://localhost/sales_reporting_db");
		parms.put("username","jjs");                                  
		parms.put("password","jjs");
		DataSource ds = DataSourceFactory.getDatasource(parms);
		testConnection(ds);
		System.out.println("tested postgress");
		
	}
	
	private void testConnection(DataSource ds) throws SQLException {
		Connection conn = ds.getConnection();
		
		org.junit.Assert.assertNotNull("conn", conn);
		PreparedStatement ps = conn.prepareStatement("select 'x'");
		ResultSet rset = ps.executeQuery();
		while (rset.next()) {
			String x = rset.getString(1);
			assertEquals(x,"x");
		}
	
		conn.close();
		
	}
	
	@Test
	public void testYaml() throws FileNotFoundException, PropertyVetoException, SQLException {
		System.out.println("testYaml");
		String homeDir = System.getProperty("user.home");
		String yamlName = homeDir + "/connections_java.yaml";
		DataSourceFactory dsf = new DataSourceFactory(yamlName);
		System.out.println("getting current");
		DataSource ds = dsf.getDatasource("current");
		System.out.println("got current");
		testConnection(ds);
		System.out.println("testedYaml");
	}
	
	

}
