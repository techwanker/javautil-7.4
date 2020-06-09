package org.javautil.joblog.installer;

import java.io.Closeable;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.TestDataSource;
import org.junit.Test;

public class JoblogOracleInstallTest {


	@Test
	public void testInstall() throws Exception  {
		DataSource ds = TestDataSource.getDataSource(Dialect.ORACLE);
		Connection loggerConnection = ds.getConnection();
		JoblogOracleInstall installer = new JoblogOracleInstall(loggerConnection, true, true);
		installer.process();
		loggerConnection.close();
		((Closeable) ds).close();
	}
}