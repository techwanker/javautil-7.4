package org.javautil.joblog;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.activation.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.joblog.installer.JoblogOracleInstall;
import org.javautil.joblog.persistence.JoblogPersistencePackage;
import org.junit.BeforeClass;
import org.junit.Test;

public class JoblogPersistencePackageTest {

	private static javax.sql.DataSource datasource;
	private static Connection connection ;
	private static JoblogPersistencePackage joblog;
	

	@BeforeClass
	public static void beforeClass() throws PropertyVetoException, SQLException   {
		DataSourceFactory dsf = new DataSourceFactory();

		datasource = dsf.getDatasource("integration_oracle");
		connection = datasource.getConnection();

		joblog = new JoblogPersistencePackage(connection);

	}
	
	@Test
	public  void testAll() {
		
		
		
	}
	
	void testJobLogInsert() throws SQLException {
		String token = joblog.joblogInsert("JoblogPersistenceTest", this.getClass().getName(), 
				"Joblog Module");
	}
}
