package org.javautil.dblogging.tracepersistence;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.NamedSqlStatements;
import org.javautil.core.sql.SequenceHelper;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.io.FileUtil;
import org.javautil.lang.ThreadUtil;
import org.javautil.oracle.trace.CursorsStats;
import org.javautil.oracle.tracehandlers.OracleTraceProcessor;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDbloggerPersistence implements DbloggerPersistence {

	protected final Connection connection;

	protected NamedSqlStatements statements;

	Logger logger = LoggerFactory.getLogger(getClass());
	private long jobStartMilliseconds;

	private String moduleName;

	private String actionName;

	private SequenceHelper sequenceHelper;

	private long jobLogId = -1;

	private long jobStepId;

	private boolean persistTrace;

	private boolean persistPlans;

	private SqlStatement upsStatement = null;

	// private long jobId;

	private boolean throwExceptions = true;

	private SqlStatement jobStepOrderSqlStatement;

	private SqlStatement jobLogSelectSqlStatement;

	private SqlStatement updateJobLogSqlStatement;

	private SqlStatement jobLogSelectSqlStatement2;

	public AbstractDbloggerPersistence(Connection connection) throws SQLException, SqlSplitterException, IOException {
		super();
		this.connection = connection;
		if (connection == null) {
			throw new IllegalArgumentException("null connection");
		}

		sequenceHelper = new SequenceHelper(connection);
		statements = NamedSqlStatements.getNameSqlStatementsFromSqlSplitterResource(this, "ddl/h2/dblogger_dml.ss.sql");
	}

	@Override
	public long persistJob(final String processName, String className, String moduleName, String statusMsg,
			String schema, String tracefileName, long jobLogId) throws SQLException {

		SqlStatement ss = statements.getSqlStatement("job_log_insert");
		ss.setConnection(connection);
		Binds binds = new Binds();
		binds.put("job_log_id", jobLogId);
		binds.put("process_name", processName);
		binds.put("classname", className);
		binds.put("module_name", moduleName);
		binds.put("status_msg", statusMsg);
		binds.put("thread_name", Thread.currentThread().getName());
		binds.put("schema_name", schema);
		binds.put("tracefile_name", tracefileName);
		binds.put("status_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		if (logger.isDebugEnabled()) {
			logger.debug("job_log_insert\n{}", ss.getSql());
		}
		ss.executeUpdate(binds);
		connection.commit();
		if (logger.isDebugEnabled()) {
			logger.debug("started job {} ", jobLogId);
			if (jobLogSelectSqlStatement2 == null) {
				jobLogSelectSqlStatement2 = new SqlStatement("select * from job_log where job_log_id = :job_log_id");
			}
			jobLogSelectSqlStatement2.setConnection(connection);
			Binds selBinds = new Binds();
			selBinds.put("job_log_id", jobLogId);
			NameValue nvs = jobLogSelectSqlStatement2.getNameValue(selBinds, true);
			logger.debug("persistJob select: {}", nvs.toString());
		}

		this.jobLogId = jobLogId;

		connection.commit();
		return jobLogId;
	}

	public long insertStep(String stepName, String stepInfo, String className) throws SQLException {
		String stackTrace = ThreadUtil.getStackTrace(2);
		return insertStep(stepName, stepInfo, className, stackTrace);
	}

	@Override
	public long insertStep(String stepName, String stepInfo, String className, String stacktrace) {
		long retval = -1;
		try {
			if (sequenceHelper == null) {
				throw new IllegalStateException("sequencehelper is null");
			}
			this.jobStepId = sequenceHelper.getSequence("job_step_id_seq");

			Binds binds = new Binds();
			binds.put("job_step_id", jobStepId);
			binds.put("job_log_id", jobLogId);
			binds.put("step_name", stepName);
			binds.put("step_info", stepInfo);
			binds.put("classname", className);
			binds.put("start_ts", new java.sql.Timestamp(System.currentTimeMillis()));
			binds.put("stacktrace", stacktrace);
			if (statements == null) {
				throw new IllegalStateException("statements is null");
			}
			SqlStatement ss = statements.get("job_step_insert");
			ss.setConnection(connection);
			ss.executeUpdate(binds);
			connection.commit(); // TODO should be autocommit on transaction or
			logger.debug("insertStep {} with binds {} " + stepName, binds);
			retval = jobStepId;
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			logger.error(sqe.getMessage());
		}
		return retval;
	}

	@Override
	public void finishStep() throws SQLException {
		Binds binds = new Binds();
		binds.put("job_step_id", jobStepId);
		binds.put("end_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		SqlStatement ss = statements.get("end_step");
		ss.setConnection(connection);
		int rowCount = ss.executeUpdate(binds);
		if (rowCount != 1) {
			// should have option to abort
			logger.error(String.format("finishStep stepId %d updated %d rows", jobStepId, rowCount));
			// now what? rollback TODO
		}

		connection.commit();
	}

	private void finishJob(SqlStatement ss) throws SQLException {
		logger.debug("finishJob {} {}", jobLogId, ss.getSql());
		ss.setConnection(connection);
		Binds binds = new Binds();
		binds.put("job_log_id", jobLogId);
		binds.put("end_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		int rowcount = ss.executeUpdate(binds);
		// connection.commit(); // TODO is this a hack?
		if (rowcount != 1) {
			logger.warn("job_log not updated for {}", jobLogId);
		} else {
			logger.info("finishJob: {}", jobLogId);
		}

		updateJob(jobLogId);

		connection.commit();
		logger.info("job " + jobLogId + " finished =====");
	}

	@Override
	public void abortJob(Exception e) throws SQLException {
		finishJob(statements.getSqlStatement("abort_job"));
	}

	@Override
	public void endJob() throws SQLException {

		finishJob(statements.getSqlStatement("end_job"));
	}

	protected ListOfNameValue getUtProcessStatus(long jobNbr) throws SQLException {
		String sql = "select * from job_log order by job_log_id";
		if (jobLogSelectSqlStatement == null) {
			jobLogSelectSqlStatement = new SqlStatement(connection, sql);
		}
		return jobLogSelectSqlStatement.getListOfNameValue(new Binds());

	}

	public void showUtProcessStep() throws SQLException {
		String sql = "select * from job_step order by job_step_id";
		if (jobStepOrderSqlStatement == null) {
			jobStepOrderSqlStatement = new SqlStatement(connection, sql);
		}
		for (NameValue nv : jobStepOrderSqlStatement.getListOfNameValue(new Binds())) {
			System.out.println(nv);
		}

	}

	public void updateJob(long jobId) throws SQLException {
		String ups = "select tracefile_name from job_log " + "where job_log_id = :job_log_id";

		String upd = "update job_log " + "set tracefile_data =  ?, " + "    tracefile_json =  ? "
				+ "where job_log_id = ?";

		logger.info("updating job {}", jobId);
		if (upsStatement == null) {
			upsStatement = new SqlStatement(connection, ups);
		}
		Binds binds = new Binds();
		binds.put("job_log_id", jobId);
		NameValue upsRow = upsStatement.getNameValue(binds, true);
		//
		String traceFileName = upsRow.getString("tracefile_name");
		if (traceFileName == null) {
			logger.warn("tracefileName is null");
			//throw new IllegalStateException("traceFileName is null");
		} else {
			//
			//
			// TODO this is reading directly from the file 
			Clob clob = connection.createClob();
			String tracefileData = null;
			try {
				tracefileData = FileUtil.getAsString(traceFileName);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			clob.setString(1, tracefileData);
			//
			Clob jsonClob = connection.createClob();


			OracleTraceProcessor tfr;
			try {
				tfr = new OracleTraceProcessor(connection, traceFileName);
				tfr.process();
				CursorsStats cursorStats = tfr.getCursors();
				String jsonString = cursorStats.toString();
				jsonClob.setString(1, jsonString);

				PreparedStatement updateTraceFile = connection.prepareStatement(upd);

				updateTraceFile.setClob(1, clob);
				updateTraceFile.setClob(2, jsonClob);
				updateTraceFile.setLong(3, jobId);
				int count = updateTraceFile.executeUpdate();

				binds.put("tracefile_data", clob);
				if (count != 1) {
					throw new IllegalArgumentException("unable to update job_log_id " + jobId);
				}
				logger.info("updated {}", jobId);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void updateTraceFileName(String appTracefileName) throws SQLException {
		logger.info("*** updating trace to {}", appTracefileName);
		if (updateJobLogSqlStatement == null) {
			updateJobLogSqlStatement = new SqlStatement(connection,
					"update job_log set tracefile_name = :tracefile_name " + "where job_log_id = :job_log_id");
		}
		Binds binds = new Binds();
		binds.put("tracefile_name", appTracefileName);
		binds.put("job_log_id", jobLogId);
		int rowCount = updateJobLogSqlStatement.executeUpdate(binds);
		connection.commit();
		if (rowCount != 1) {
			logger.error("Unable to update job_log for {}", jobLogId);
		} else {
			logger.info("updated job_log {}", appTracefileName);
		}

	}

	@Override
	public void persistenceUpdateTrace(long jobId, Clob traceClob) throws SQLException {
		if (traceClob == null) {
			throw new IllegalArgumentException("null traceClob");
		}
		Clob clob = connection.createClob();
		String upd = "update job_log " + "set tracefile_data =  ? " + "where job_log_id = ?";

		Reader traceReader = traceClob.getCharacterStream();

		PreparedStatement updateTraceFile = connection.prepareStatement(upd);

		updateTraceFile.setCharacterStream(1, traceReader);
		updateTraceFile.setLong(2, jobId);

		int rowcount = updateTraceFile.executeUpdate();

		if (rowcount != 1) {
			throw new IllegalArgumentException("unable to update job_log_id " + jobId);
		}

	}

	@Override
	public Clob createClob() throws SQLException {
		return connection.createClob();
	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
		this.persistTrace = persistTrace;

	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
		this.persistPlans = persistPlans;

	}

	@Override
	public long getNextJobLogId() {
		long retval = -1;
		try {
			retval = sequenceHelper.getSequence("job_log_id_seq");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		}
		return retval;
	}

	@Override
	public long getJobLogId() {
		return jobLogId;
	}

}