package org.javautil.joblog.installer;

import java.io.Closeable;
import java.sql.Connection;

import javax.sql.DataSource;
import org.javautil.joblog.installer.DbloggerOracleInstall;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.junit.Test;

public class DbloggerOracleInstallTest {
	
	// TODO WTF

//	@Test
//	public void testDrop() throws SqlSplitterException, Exception {
//		DataSourceFactory dsf = new DataSourceFactory();
//		DataSource ds = dsf.getDatasource("integration_oracle");
//		Connection loggerConnection = ds.getConnection();
//		DbloggerOracleInstall installer = new DbloggerOracleInstall(loggerConnection, true, true);
//		installer.drop();
//		installer.process();
//		loggerConnection.close();
//		((Closeable) ds).close();
//	}
}