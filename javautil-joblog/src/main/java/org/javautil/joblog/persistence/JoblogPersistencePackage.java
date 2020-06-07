package org.javautil.joblog.persistence;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Timestamp;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.SequenceHelper;
import org.javautil.lang.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogPersistencePackage extends AbstractJoblogPersistence implements JoblogPersistence {

	private Connection connection;
	private CallableStatement persistJobStatement;
	private CallableStatement abortJobStatement;
	private CallableStatement endJobStatement;
	private CallableStatement insertStepStatement;
	SequenceHelper sequenceHelper;
	Logger logger = LoggerFactory.getLogger(getClass());
	boolean throwExceptions;
	private CallableStatement finishStepStatement;
	boolean persistTraceOnJobCompletion;
	boolean persistPlansOnJobCompletion;

	public JoblogPersistencePackage(Connection connection) throws SQLException {
		this.connection = connection;
		this.sequenceHelper = new SequenceHelper(connection);
	}	@Override
	
	
	public String joblogInsert(String processName, String className, String moduleName) throws SQLException {
		return joblogInsert(processName, className, moduleName,"");
	}
	// TODO externalize
	@Override
	public String joblogInsert(String processName, String className, 
			String moduleName, String statusMsg ) throws SQLException {
		String callSql = "begin " + 
			    ":token := joblog.job_log_insert ( \n" 
				+ "		p_process_name => :p_process_name,\n"  
				+ "     p_classname     => :p_classname, \n" 
				+ "     p_module_name   => :p_module_name,\n"
				+ "		p_thread_name  => :p_thread_name,\n"
				+ "		p_status_msg   => :p_status_msg\n"
				+ ");" 
				+ "end;";

		if (persistJobStatement == null) {
			persistJobStatement = connection.prepareCall(callSql);
		}
		persistJobStatement.registerOutParameter("token", java.sql.Types.VARCHAR);
		//persistJobStatement.setLong("p_job_log_id", jobLogId);
		//persistJobStatement.setString("p_schema_name", null);
		persistJobStatement.setString("p_process_name", processName);
		persistJobStatement.setString("p_classname", className);
		persistJobStatement.setString("p_module_name", moduleName);
		persistJobStatement.setString("p_thread_name", Thread.currentThread().getName());
		persistJobStatement.setString("p_status_msg", statusMsg);
		//persistJobStatement.setTimestamp("p_status_ts", new Timestamp(System.currentTimeMillis()));
		//persistJobStatement.setLong("p_sid", 0l);// TODO
		//persistJobStatement.setString("p_tracefile_name", tracefileName);
		persistJobStatement.execute();
		String token = persistJobStatement.getString("token");
		return token;
	}

	@Override
	public void endJob(String token) throws SQLException {
		String callSql = "begin joblog.end_job(p_job_token => :p_token); end;";
		if (endJobStatement == null) {
			endJobStatement = connection.prepareCall(callSql);
		}
		endJobStatement.setString("p_token",token);
		endJobStatement.execute();
	}
	
	
	@Override
	public long insertStep(String jobToken, String stepName, String stepInfo, String className) {
		long jobStepId = -1L;
		String callSql = "begin \n"
				+ ":p_job_step_id := joblog.job_step_insert (\n"
				+ "                p_job_token   => :p_job_token,"
				+ "                p_step_name   => :p_step_name, \n"
				+ "                p_step_info   => :p_step_info, \n"
				+ "                p_classname   => :p_classname,     \n"
				+ "                p_start_ts    => :p_start_ps,\n"
				+ "                p_stacktrace  => :p_stacktrace\n"
				+ ");"
				+ "end;";
		try {
			if (insertStepStatement == null) {
				insertStepStatement = connection.prepareCall(callSql);
			}
			insertStepStatement.registerOutParameter("p_job_step_id", java.sql.Types.INTEGER);
			insertStepStatement.setString("p_job_token", jobToken);
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
	public void finishStep(long stepId) throws SQLException {
		String callSql = "begin joblog.finish_step(stepid => :step_id); end;";
		if (finishStepStatement == null) {
			finishStepStatement = connection.prepareCall(callSql);
		}
		finishStepStatement.setLong("step_id", stepId);
		finishStepStatement.execute();
	}

	@Override
	public void abortJob(String jobToken, Exception e) throws SQLException {
		String abortJobSql  = "begin\n"
				+ "joblog.abort_job("
				+ "  p_job_token => :job_token,\n"
				+ "  p_stacktrace => :p_stack_trace);\n"
				+ "end;";
		
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage());
		sb.append("\n");
		for (StackTraceElement el : e.getStackTrace()) {
			sb.append(el.toString());
			sb.append("\n");
		}
		String abortMessage = sb.toString();

		if (abortJobStatement == null) {
			abortJobStatement = connection.prepareCall(abortJobSql);
		}
		abortJobStatement.setString("job_token", jobToken);
		abortJobStatement.setString("p_stack_trace", abortMessage);
		abortJobStatement.execute();
		logger.warn("job terminated with: '{}'", abortMessage);
	}

//	@Override
//	public Clob createClob() throws SQLException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void persistenceUpdateTrace(long jobId, Clob traceData) throws SQLException {
		// TODO Auto-generated method stub
	}



	
}
