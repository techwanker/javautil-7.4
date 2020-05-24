package org.javautil.joblog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.SqlStatement;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbloggerForOracleExampleTest extends BaseTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@AfterClass
	public static void afterClass() throws IOException {
		((Closeable) applicationDataSource).close();
		((Closeable) loggerDataSource).close();
	}

	@Test
	public void testDirectly() throws SQLException {
		long jobId = dblogger.startJobLogging("DbLoggerForOracle", getClass().getName(), "ExampleLogging", null, 12);
		SqlStatement ss = new SqlStatement("select * from job_log where job_log_id = :job_log_id");
		ss.setConnection(loggerConnection);
		Binds binds = new Binds();
		binds.put("job_log_id", jobId);
		NameValue jobNv = ss.getNameValue(binds, true);
		assertTrue(jobId > 0);
		long jobId2 = dblogger.startJobLogging("DbLoggerForOracle", getClass().getName(), "ExampleLogging", null, 12);
		assertTrue(jobId2 > jobId);
	}

	@Test
	public void test1() throws SQLException, IOException {
		// TODO look for waits
		DbloggerForOracleExample example = new DbloggerForOracleExample(applicationConnection, dblogger, "example",
				false, 12);
		long jobId = example.process();
		logger.info("test1 jobId {}", jobId);
		assertTrue(jobId > 0);
		SqlStatement ss = new SqlStatement("select * from job_log where job_log_id = :job_log_id");
		ss.setConnection(loggerConnection);
		Binds binds = new Binds();
		binds.put("job_log_id", jobId);
		NameValue jobNv = ss.getNameValue(binds, true);
		logger.info("jobNv {}", jobNv.toString());
		// assertEquals("SA", jobNv.get("schema_name"));
		assertEquals("main", jobNv.get("thread_name"));
		// assertNotNull(jobNv.get("process_run_nbr"));
		assertEquals("DONE", jobNv.get("status_msg"));
		// assertEquals("C",jobNv.get("status_id"));
		assertNotNull(jobNv.get("status_ts"));
		assertEquals("N", jobNv.get("ignore_flg"));
		assertEquals("ExampleLogging", jobNv.get("module_name"));
		assertEquals("org.javautil.dblogging.DbloggerForOracleExample", jobNv.get("classname"));
		String tracefileName = jobNv.getString("tracefile_name");
		int jobInd = tracefileName.indexOf("job");
		assertTrue(jobInd >= 0);
		//
		SqlStatement stepSs = new SqlStatement(
				"select * from job_step where job_log_id = :job_log_id order by job_step_id ");
		stepSs.setConnection(loggerConnection);
		ListOfNameValue nvSteps = stepSs.getListOfNameValue(binds, true);
		assertEquals(2, nvSteps.size());
		logger.debug(nvSteps.toString());
		NameValue step1 = nvSteps.get(0);
		assertEquals(step1.get("step_name"), "fullJoin");
		assertEquals(step1.get("classname"), "org.javautil.dblogging.DbloggerForOracleExample");
		assertEquals(step1.get("step_info"), "fullJoin");
		assertNotNull(step1.get("start_ts"));
		assertNotNull(step1.get("end_ts"));
		// TODO continue with cursor stuff
		// logger.debug(jobNv);
	}

}
