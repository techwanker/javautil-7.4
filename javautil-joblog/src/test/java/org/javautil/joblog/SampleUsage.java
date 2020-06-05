package org.javautil.joblog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ConnectionHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.util.NameValue;

public class SampleUsage {

	private JoblogPersistence dblogger;
	private Connection connection;
	private String processName;
	private String jobToken = null;
	
	public SampleUsage(Connection connection, JoblogPersistence dblogger, String processName) {
		this.connection = connection;
		this.dblogger = dblogger;
		this.processName = processName;
	}

	public String process() throws SQLException {
		dblogger.prepareConnection();

		jobToken = dblogger.joblogInsert(processName, getClass().getName(), "SampleUsage");

		actionNoStep();
		stepNoAction();
		dblogger.endJob(jobToken);
		return jobToken;

	}

	public String processException() throws SQLException, FileNotFoundException, IOException {
		// id = dblogger.startJobLogging(processName, "ExampleLogging", null, 12);
		String logJobId = dblogger.joblogInsert(processName, getClass().getName(), 
				"SamplueUsage");

		try {
			int x = 1 / 0;
		} catch (Exception e) {
			dblogger.abortJob(jobToken, e);
		}

		return logJobId;
	}

	private void actionNoStep() throws SQLException {

		dblogger.setAction("Some work");
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables");
	}

	private void stepNoAction() throws SQLException {

		dblogger.insertStep(jobToken,"Useless join", "full join", getClass().getName());
		ConnectionHelper.exhaustQuery(connection, "select * from user_tab_columns, user_tables");
	}

	NameValue getUtProcessStatus(Connection connection, long id) throws SQLException {
		final String sql = "select * from job_log " + "where job_log_id = :job_stat_id";
		final SqlStatement ss = new SqlStatement(connection, sql);
		Binds binds = new Binds();
		binds.put("job_stat_id", id);
		final NameValue retval = ss.getNameValue();
		ss.close();
		return retval;
	}

}
