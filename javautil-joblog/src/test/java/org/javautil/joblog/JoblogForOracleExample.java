package org.javautil.joblog;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ConnectionHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogForOracleExample {

	private JoblogPersistence dblogger;
	private Connection connection;
	private String processName;
	private boolean testAbort = false;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String jobToken;

	public JoblogForOracleExample(Connection connection, JoblogPersistence dblogger, String processName, boolean testAbort,
			int traceLevel) {
		this.connection = connection;
		this.dblogger = dblogger;
		this.processName = processName;
		this.testAbort = testAbort;
	}

	public String process() throws SQLException {
		dblogger.prepareConnection();

		try {
			jobToken = dblogger.joblogInsert(processName, getClass().getName(), "ExampleLogging");
			logger.debug("============================ token: {}", jobToken);
			limitedFullJoin();
			fullJoin();
			userTablesCount();
			if (testAbort) {
				int x = 1 / 0;
			}
			logger.debug("calling endJob");
			dblogger.endJob();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			dblogger.abortJob(e);
			throw e;
		}
		return jobToken;
	}

	/**
	 * This will set the v$session.action
	 */
	private void limitedFullJoin() throws SQLException {
		long stepId = 	dblogger.insertStep(jobToken, "limitedFullJoin", null, getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables where rownum < 200");
		dblogger.finishStep(stepId);
	}

	private void fullJoin() throws SQLException {
		// TODO insertStep should set the action
		long stepId = dblogger.insertStep(jobToken,"fullJoin", "fullJoin", getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables");
		dblogger.finishStep(stepId);
	}

	private void userTablesCount() throws SQLException {
		long stepId = dblogger.insertStep(jobToken,"count full", "userTablesCount", getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select count(*) dracula from user_tables");
		dblogger.finishStep(stepId);
		// TODO support implicit finish step
	}

	NameValue getJobLog(Connection connection, long id) throws SQLException {
		final String sql = "select * from job_log " + "where job_log_id = :job_stat_id";
		final SqlStatement ss = new SqlStatement(connection, sql);
		Binds binds = new Binds();
		binds.put("job_stat_id", id);
		final NameValue retval = ss.getNameValue();
		ss.close();
		return retval;
	}
}
