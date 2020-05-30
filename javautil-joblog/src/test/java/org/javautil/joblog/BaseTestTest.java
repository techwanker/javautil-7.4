/**
 *
 */
package org.javautil.joblog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ConnectionHelper;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.installer.DbloggerOracleInstall;
import org.javautil.joblog.installer.H2LoggerDataSource;
import org.javautil.joblog.logger.Joblog;
import org.javautil.joblog.logger.SplitLoggerForOracle;
import org.javautil.joblog.persistence.DbloggerPersistenceImpl;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.util.NameValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTestTest
extends BaseTest 
{

	private final static Logger logger = LoggerFactory.getLogger(AllLoggersTest.class);
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
		new DbloggerOracleInstall(applicationConnection, true, false).process();
	}
}
