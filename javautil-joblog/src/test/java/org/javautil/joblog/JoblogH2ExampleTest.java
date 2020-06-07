package org.javautil.joblog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.installer.H2Install;
import org.javautil.joblog.installer.JoblogOracleInstall;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.joblog.persistence.JoblogPersistencePackage;
import org.javautil.joblog.persistence.JoblogPersistenceSql;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogH2ExampleTest  {

	private Logger logger = LoggerFactory.getLogger(getClass());
	static DataSource applicationDataSource;
	static Connection applicationConnection;

	static DataSource jobDataSource;
	static Connection joblogConnection;
	static JoblogPersistence joblogPersistence;
	static JoblogPersistencePackage oraclePackagePersistence;
	boolean showSql = false;
	
	@BeforeClass
	public static void beforeClass() throws SqlSplitterException, Exception {
		DataSourceFactory dsf = new DataSourceFactory();

		applicationDataSource = dsf.getDatasource("integration_oracle");
		applicationConnection = applicationDataSource.getConnection();
		
		jobDataSource = DataSourceFactory.getH2Permanent("/tmp/J0bLogTest", "sa", "tutorial");
		joblogConnection = jobDataSource.getConnection();
		SqlStatement dropJoblog = new SqlStatement(joblogConnection,"drop all objects");
		dropJoblog.execute(new Binds());
		joblogPersistence = new JoblogPersistenceSql(joblogConnection, applicationConnection);
		new  H2Install(joblogConnection).process();
		oraclePackagePersistence = new JoblogPersistencePackage(applicationConnection);

	}

	@AfterClass
	public static void afterClass() throws IOException {
		((Closeable) applicationDataSource).close();
		((Closeable) jobDataSource).close();
	}

	@Test
	public void testDirectly() throws SQLException {
		String token = joblogPersistence.joblogInsert("DbLoggerForOracle", getClass().getName(), "ExampleLogging");
		SqlStatement ss = new SqlStatement("select * from job_log where job_token = :token");
		ss.setConnection(joblogConnection);
		Binds binds = new Binds();
		binds.put("token", token);
		NameValue jobNv = ss.getNameValue(binds, true);
		String jobId2 = joblogPersistence.joblogInsert("DbLoggerForOracle", getClass().getName(), "ExampleLogging");
	}

	@Test
	public void testSql() throws SQLException, IOException {
		test1(joblogPersistence);
}   

	// TODO should be commo9n with Oracle
	public void test1(JoblogPersistence joblogPersistence) throws SQLException, IOException {
		// TODO look for waits
		JoblogForOracleExample example = new JoblogForOracleExample(applicationConnection, joblogPersistence, "example",
				false, 12);
		String token = example.process();
		logger.info("test1 token {}", token);
		assertNotNull(token);
		logger.debug("token: {}",token);
		SqlStatement ss = new SqlStatement("select * from job_log where job_token = :token");
		ss.setConnection(joblogConnection);
		Binds binds = new Binds();
		binds.put("token", token);
		NameValue jobNv = ss.getNameValue(binds, true);
		binds.put("job_log_id",jobNv.get("job_log_id"));
		logger.info("jobNv {}", jobNv.toString());
		assertEquals("main", jobNv.get("thread_name"));
		assertNotNull(jobNv.get("start_ts"));
		assertEquals("N", jobNv.get("ignore_flg"));
		assertEquals("ExampleLogging", jobNv.get("module_name"));
		assertEquals("org.javautil.joblog.JoblogForOracleExample", jobNv.get("classname"));
		//
		SqlStatement stepSs = new SqlStatement(
				"select * from job_step where job_log_id = :job_log_id order by job_step_id ");
		stepSs.setConnection(joblogConnection);
		ListOfNameValue nvSteps = stepSs.getListOfNameValue(binds, true);
		assertEquals(3, nvSteps.size());
		logger.debug(nvSteps.toString());
		NameValue step1 = nvSteps.get(0);
		assertEquals("limitedFullJoin", step1.get("step_name"));
		assertEquals("org.javautil.joblog.JoblogForOracleExample",step1.get("classname"));
		assertNull(step1.get("step_info"));
		assertNotNull(step1.get("start_ts"));
		assertNotNull(step1.get("end_ts"));
		// TODO continue with cursor stuff
	}

}
