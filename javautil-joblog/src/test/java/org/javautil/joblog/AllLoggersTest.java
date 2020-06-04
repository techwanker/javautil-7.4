///**
// *
// */
//package org.javautil.joblog;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.io.Closeable;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//import javax.sql.DataSource;
//
//import org.javautil.core.sql.Binds;
//import org.javautil.core.sql.ConnectionHelper;
//import org.javautil.core.sql.DataSourceFactory;
//import org.javautil.core.sql.SqlSplitterException;
//import org.javautil.core.sql.SqlStatement;
//import org.javautil.joblog.installer.DbloggerOracleInstall;
//import org.javautil.joblog.installer.DbloggerOracleInstallTest;
//import org.javautil.joblog.installer.H2LoggerDataSource;
//import org.javautil.joblog.logger.Joblog;
//import org.javautil.joblog.logger.SplitLoggerForOracle;
//import org.javautil.joblog.persistence.JoblogPersistenceSql;
//import org.javautil.joblog.persistence.JoblogPersistence;
//import org.javautil.util.NameValue;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class AllLoggersTest
////extends BaseTest 
//{
//
//	private final static Logger logger = LoggerFactory.getLogger(AllLoggersTest.class);
//	final String processName = "Logging Example";
//	private static DataSource applicationDataSource;
//	private static DataSource loggerDataSource;
//	private boolean showInstallSql = true;
//	private static DataSourceFactory dsf = new DataSourceFactory();
//
//	@BeforeClass
//	public static void beforeClass() throws SqlSplitterException, Exception {
//		applicationDataSource = dsf.getDatasource("integration");
//		loggerDataSource = dsf.getDatasource("integration");
//	}
//
//	@AfterClass
//	public static void afterClass() throws IOException {
//		((Closeable) applicationDataSource).close();
//		((Closeable) loggerDataSource).close();
//	}
//
//	
//	public long sampleUsage(Joblog joblogger, Connection appConnection) throws SqlSplitterException, Exception {
//
//		joblogger.ensureDatabaseObjects();
//
//		joblogger.prepareConnection(); // TODO should be in dblogger
//		final String processName = "Process Name";
//		// Start the job
//
//		final long logJobId = joblogger.startJobLogging(processName, getClass().getName(), null, null, 4);
//		joblogger.setModule("SplitLoggerTest", "simple example");
//		joblogger.setAction("Some work");
//		joblogger.insertStep("Full join", "Meaningless busy work", getClass().getName());
//		ConnectionHelper.exhaustQuery(appConnection, "select * from user_tab_columns, user_tables where rownum < 100");
//
//		joblogger.setAction("Another set of work");
//		ConnectionHelper.exhaustQuery(appConnection, "select count(*) from all_tab_columns");
//		// End the job
//		joblogger.endJob();
//		return logJobId;
//	}
//
//	@Test
//	public void testSplitLogger() throws SqlSplitterException, Exception {
//		Connection appConnection = applicationDataSource.getConnection();
//		Connection loggerConnection = loggerDataSource.getConnection();
//		logger.info("xeInstall");
//		
//		DbloggerOracleInstall orainst = new DbloggerOracleInstall(loggerConnection, true, showInstallSql);
//		orainst.process();
//		logger.info("DbloggerOracleInstall");
//		
////		orainst = new DbloggerOracleInstall(appConnection, true, showInstallSql);
////		orainst.process();
////		//
//		logger.info("new persistenceLogger");
//		JoblogPersistence persistenceLogger = new JoblogPersistenceSql(loggerDataSource.getConnection());
//		Joblog dblogger = new SplitLoggerForOracle(appConnection, persistenceLogger);
//		logger.info("sampleUsage");
//		long id = sampleUsage(dblogger, appConnection);
//		Connection conn = loggerDataSource.getConnection();
//		testResults(conn, id);
//		appConnection.close();
//		loggerConnection.close();
//		conn.close();
//	}
//
//	public void testH2logger() throws SqlSplitterException, Exception {
//		DataSource h2DataSource = new H2LoggerDataSource().getPopulatedH2FromDbLoggerProperties(this,
//				"h2.dblogger.properties");
//		Connection appConnection = applicationDataSource.getConnection();
//		Joblog dblogger = new SplitLoggerForOracle(appConnection, h2DataSource.getConnection());
//		long id = sampleUsage(dblogger, appConnection);
//		Connection conn = h2DataSource.getConnection();
//		testResults(conn, id);
//		conn.close();
//		appConnection.close();
//	}
//
//	public void testResults(Connection conn, long id) throws SQLException {
//		SqlStatement ss = new SqlStatement(conn, "select * from job_log where job_log_id = :job_log_id");
//		Binds binds = new Binds();
//		binds.put("job_log_id", id);
//		final NameValue status = ss.getNameValue(binds, false);
//		ss.close();
//		logger.debug(status.getSortedMultilineString());
//		// assertEquals(processName, status.getString("PROCESS_NAME"));
//		// assertEquals("C", status.getString("STATUS_ID"));
//		assertNotNull(status.getString("STATUS_TS"));
//		String tracefileName = status.getString("TRACEFILE_NAME");
//		int xeindex = tracefileName.indexOf("xe");
//		assertEquals(-1, xeindex);
//
//		// check out step
//		ss = new SqlStatement(conn, "select * from job_step where job_log_id = :job_log_id");
//		final NameValue stepStatusNv = ss.getNameValue(binds, false);
//		// logger.info("step: {}", stepStatusNv.getSortedMultilineString());
//	}
//}
