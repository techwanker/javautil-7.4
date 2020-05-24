package org.javautil.joblog;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ConnectionHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.logger.Joblog;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbloggerForOracleExample {

	private Joblog dblogger;
	private Connection connection;
	private String processName;
	private boolean testAbort = false;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public DbloggerForOracleExample(Connection connection, Joblog dblogger, String processName, boolean testAbort,
			int traceLevel) {
		this.connection = connection;
		this.dblogger = dblogger;
		this.processName = processName;
		this.testAbort = testAbort;
	}

	public long process() throws SQLException {
		dblogger.prepareConnection();
		long id = 0;

		try {
			id = dblogger.startJobLogging(processName, getClass().getName(), "ExampleLogging", null, 12);
			logger.debug("============================jobId: {}", id);
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
		return id;
	}

	/**
	 * This will set the v$session.action
	 */
	private void limitedFullJoin() throws SQLException {
		logger.debug("limitedFullJoin =============");
		dblogger.setAction("actionNoStep");
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables where rownum < 200");
		dblogger.setAction(null); // no longer performing that action, so clear
		logger.debug("limitedFullJoin complete =============");
	}

	private void fullJoin() throws SQLException {
		logger.debug("fullJoinBegins =============");
		// TODO insertStep should set the action
		dblogger.insertStep("fullJoin", "fullJoin", getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables");
		dblogger.finishStep();
		logger.debug("fullJoin complete =============");
	}

	private void userTablesCount() throws SQLException {
		dblogger.insertStep("count full", "userTablesCount", getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select count(*) dracula from user_tables");
		dblogger.finishStep();
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
