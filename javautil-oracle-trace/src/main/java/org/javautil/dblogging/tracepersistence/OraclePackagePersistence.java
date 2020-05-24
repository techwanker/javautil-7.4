package org.javautil.dblogging.tracepersistence;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.javautil.core.sql.SequenceHelper;
import org.javautil.lang.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OraclePackagePersistence implements DbloggerPersistence {

	private Connection connection;
	private CallableStatement persistJobStatement;
	private CallableStatement abortJobStatement;
	private CallableStatement endJobStatement;
	private CallableStatement insertStepStatement;
	private SequenceHelper sequenceHelper;
	private long jobLogId;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private boolean throwExceptions;
	private CallableStatement finishStepStatement;
	private boolean persistTraceOnJobCompletion;
	private boolean persistPlansOnJobCompletion;

	public OraclePackagePersistence(Connection connection) throws SQLException {
		this.connection = connection;
		this.sequenceHelper = new SequenceHelper(connection);
	}

	// TODO externalize
	@Override
	public long persistJob(String processName, String className, String moduleName, String statusMsg,
			String tracefileName, String schema, long jobLogId) throws SQLException {
		String callSql = "begin " + "persist_job_log ( \n" + "					 p_job_log_id   => :p_job_log_id,\n"
				+ "					 p_schema_name  => :p_schema_name,\n"
				+ "					 p_process_name => :p_process_name,\n" + "					 \n"
				+ "					 p_thread_name  => :p_thread_name,\n"
				+ "					 p_status_msg   => :p_status_msg,\n"
				+ "					 p_status_ts    => :p_status_ts,\n" + "					 \n"
				+ "					 p_sid          => :p_sid,\n"
				+ "                   p_module_name   => :p_module_name,\n"
				+ "                   p_classname     => :p_classname, \n" + "                   \n"
				+ "                   p_tracefile_name => :p_tracefile_name);" + "end;";

		if (persistJobStatement == null) {
			persistJobStatement = connection.prepareCall(callSql);
		}
		persistJobStatement.setLong("p_job_log_id", jobLogId);
		persistJobStatement.setString("p_schema_name", null);
		persistJobStatement.setString("p_process_name", processName);
		persistJobStatement.setString("p_thread_name", Thread.currentThread().getName());
		persistJobStatement.setString("p_status_msg", statusMsg);
		persistJobStatement.setTimestamp("p_status_ts", new Timestamp(System.currentTimeMillis()));
		persistJobStatement.setLong("p_sid", 0l);// TODO
		persistJobStatement.setString("p_classname", className);
		persistJobStatement.setString("p_module_name", moduleName);
		persistJobStatement.setString("p_tracefile_name", tracefileName);
		persistJobStatement.execute();
		return jobLogId;
	}

	@Override
	public void endJob() throws SQLException {
		String callSql = "logger.end_job";
		if (endJobStatement == null) {
			endJobStatement = connection.prepareCall(callSql);
		}
		endJobStatement.execute();
	}

	@Override
	public long insertStep(String stepName, String stepInfo, String className, String stack) {
		long jobStepId = -1L;
		String callSql = ":p_job_step_id := logger_persistence.save_job_step (\n"
				+ "                p_job_step_id => :p_job_step,   \n"
				+ "                p_job_log_id  => :p_job_log_id, \n"
				+ "                p_step_name   => :p_step_name, \n"
				+ "                p_step_info   => :p_step_info, \n"
				+ "                p_classname   => :p_classname,     \n"
				+ "                p_start_ts    => :p_start_ps,\n" + "                p_stacktrace  => :p_stacktrace";
		try {
			if (insertStepStatement == null) {
				insertStepStatement = connection.prepareCall(callSql);
			}
			insertStepStatement.registerOutParameter("p_job_step_id", java.sql.Types.INTEGER);
			insertStepStatement.setLong("p_job_log_id", jobLogId);
			insertStepStatement.setString("p_step_name", stepName);
			insertStepStatement.setString("p_step_info", stepInfo);
			insertStepStatement.setString("p_classname", className);
			insertStepStatement.setTimestamp("p_start_ts", new Timestamp(System.currentTimeMillis()));
			insertStepStatement.setString("p_stacktrace", ThreadUtil.getStackTrace(4000));
			insertStepStatement.registerOutParameter("p_job_step_id", java.sql.Types.INTEGER);
			insertStepStatement.execute();
			jobStepId = insertStepStatement.getLong("p_job_step_id");
		} catch (Exception e) {
			if (throwExceptions) {
				throw new RuntimeException(e);
			} else {
				logger.error(e.getMessage());
			}
		}
		return jobStepId;
	}

	@Override
	public void finishStep() throws SQLException {
		String callSql = "logger.finish_step";
		if (finishStepStatement == null) {
			finishStepStatement = connection.prepareCall(callSql);
		}
	}

	@Override
	public void abortJob(Exception e) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		sb.append("\n");
		for (StackTraceElement el : e.getStackTrace()) {
			sb.append(el.toString());
			sb.append("\n");
		}

		if (abortJobStatement == null) {
			abortJobStatement = connection.prepareCall("begin logger_persistence.abort_job(:p_stacktrace); end;");
		}
		String abortMessage = sb.toString();
		abortJobStatement.setString("p_stacktrace", abortMessage);
		abortJobStatement.execute();
		logger.warn("job terminated with: '{}'", abortMessage);
	}

	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persistenceUpdateTrace(long jobId, Clob traceData) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
		this.persistTraceOnJobCompletion = persistTrace;
	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
		this.persistPlansOnJobCompletion = persistPlans;
	}

	@Override
	public long getJobLogId() {
		return jobLogId;
	}

	@Override
	public long getNextJobLogId() {
		long retval = -1L;
		try {
			retval = sequenceHelper.getSequence("job_log_id_seq");
		} catch (SQLException e) {
			if (throwExceptions) {
				throw new RuntimeException(e);
			} else {
				logger.error(e.getMessage());
			}
		}
		return retval;
	}

}
