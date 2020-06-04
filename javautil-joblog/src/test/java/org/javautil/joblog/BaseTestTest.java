/**
 *
 */
package org.javautil.joblog;

import static org.junit.Assert.assertTrue;

import org.javautil.core.sql.SqlSplitterException;
import org.javautil.joblog.installer.JoblogOracleInstall;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTestTest
extends BaseTest 
{

	private final static Logger logger = LoggerFactory.getLogger(BaseTest.class);
//	final String processName = "Logging Example";
//	private static DataSource applicationDataSource;
//	private static DataSource loggerDataSource;
//	private boolean showInstallSql = false;
//	private static DataSourceFactory dsf = new DataSourceFactory();

	@Test
	public void testBase() {
		assertTrue(true);
	}

	public void dbLoggerForOracleInstallTest() throws SqlSplitterException, Exception {
		new JoblogOracleInstall(applicationConnection, true, false).process();
	}
}
