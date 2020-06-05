package org.javautil.joblog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogForOracleExampleTest extends BaseTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@AfterClass
	public static void afterClass() throws IOException {
		((Closeable) applicationDataSource).close();
		((Closeable) loggerDataSource).close();
	}

	@Test
	public void testDirectly() throws SQLException {
		String token = joblogPersistence.joblogInsert("DbLoggerForOracle", getClass().getName(), "ExampleLogging");
		SqlStatement ss = new SqlStatement("select * from job_log where job_token = :token");
		ss.setConnection(loggerConnection);
		Binds binds = new Binds();
		binds.put("token", token);
		NameValue jobNv = ss.getNameValue(binds, true);
		String jobId2 = joblogPersistence.joblogInsert("DbLoggerForOracle", getClass().getName(), "ExampleLogging");
	}

	@Test
	public void testSql() throws SQLException, IOException {
		test1(joblogPersistence);
}   
	@Test
	public void testPackage() throws SQLException, IOException {
		test1(oraclePackagePersistence);
}   

	public void test1(JoblogPersistence joblogPersistence) throws SQLException, IOException {
		// TODO look for waits
		JoblogForOracleExample example = new JoblogForOracleExample(applicationConnection, joblogPersistence, "example",
				false, 12);
		String token = example.process();
		logger.info("test1 token {}", token);
		assertNotNull(token);
		logger.debug("token: {}",token);
		SqlStatement ss = new SqlStatement("select * from job_log where job_token = :token");
		ss.setConnection(loggerConnection);
		Binds binds = new Binds();
		binds.put("token", token);
		NameValue jobNv = ss.getNameValue(binds, true);
		binds.put("job_log_id",jobNv.get("job_log_id"));
		logger.info("jobNv {}", jobNv.toString());
		// assertEquals("SA", jobNv.get("schema_name"));
		assertEquals("main", jobNv.get("thread_name"));
		// assertNotNull(jobNv.get("process_run_nbr"));
		//assertEquals("DONE", jobNv.get("status_msg")); //. TODO
		// assertEquals("C",jobNv.get("status_id"));
		assertNotNull(jobNv.get("status_ts"));
		assertEquals("N", jobNv.get("ignore_flg"));
		assertEquals("ExampleLogging", jobNv.get("module_name"));
		assertEquals("org.javautil.joblog.JoblogForOracleExample", jobNv.get("classname"));
	//	String tracefileName = jobNv.getString("tracefile_name");
	//	int jobInd = tracefileName.indexOf("job");
//		assertTrue(jobInd >= 0);
		//
		SqlStatement stepSs = new SqlStatement(
				"select * from job_step where job_log_id = :job_log_id order by job_step_id ");
		stepSs.setConnection(loggerConnection);
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
		// logger.debug(jobNv);
	}

}
