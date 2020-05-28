package org.javautil.dblogging.installer;

import java.io.Closeable;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.junit.Test;

public class H2InstallTest {

	@Test
	public void testDrop() throws SqlSplitterException, Exception {
//		DataSourceFactory dsf = new DataSourceFactory();
//		DataSource ds = dsf.getDatasource("integration_oracle");
//		Connection loggerConnection = ds.getConnection();
//		
		DataSource jobDatasource = DataSourceFactory.getH2Permanent("./target/joblogging", "sa", "tutorial");
		Connection  loggingConnection = jobDatasource.getConnection();
		  
		
		H2Install installer = new H2Install(loggingConnection);
		installer.dropObjects();
		installer.process();
//		DbloggerOracleInstall installer = new DbloggerOracleInstall(loggerConnection, true, false);
//		installer.drop();
//		installer.process();
//		loggerConnection.close();
//		((Closeable) ds).close();
	}
}