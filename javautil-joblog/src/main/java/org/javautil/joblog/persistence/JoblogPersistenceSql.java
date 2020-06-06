package org.javautil.joblog.persistence;

import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.NamedSqlStatements;
import org.javautil.core.sql.SequenceHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.lang.ThreadUtil;
import org.javautil.oracle.OracleConnectionHelper;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO these should all throw Dblogger exception, don't want
//  to blow up a job because of an error in the logger
public class JoblogPersistenceSql implements JoblogPersistence {

	private Logger logger = LoggerFactory.getLogger(JoblogPersistenceSql.class);

	SequenceHelper sequenceHelper;
	protected List<CallableStatement> callableStatements = new ArrayList<>();
	protected final Connection joblogConnection;
	private boolean isJoblogConnectionOracle = false;
	protected final Connection applicationConnection;
	protected NamedSqlStatements statements;
	private long jobLogId = -1;
	private long jobStepId;
	private boolean persistTrace;
	private SqlStatement upsStatement = null;
	private SqlStatement jobStepOrderSqlStatement;
	private SqlStatement jobLogSelectSqlStatement;
	private SqlStatement updateJobLogSqlStatement;
	private SqlStatement jobLogSelectSqlStatement2;
	
	private static transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSS");

	private boolean persistPlans;

	private boolean throwExceptions;
	public JoblogPersistenceSql(Connection joblogConnection,
			Connection applogConnection) throws IOException, SQLException {
		this.joblogConnection = joblogConnection;
		this.applicationConnection = applogConnection;
		statements = NamedSqlStatements.getNameSqlStatementsFromSqlSplitterResource(this, "ddl/h2/dblogger_dml.ss.sql");
		sequenceHelper = new SequenceHelper(joblogConnection);
		isJoblogConnectionOracle = OracleConnectionHelper.isOracleConnection(joblogConnection) ;

	}

	CallableStatement prepareCall(String sql) throws SQLException {
		final CallableStatement retval = joblogConnection.prepareCall(sql);
		callableStatements.add(retval);
		return retval;
	}

	public void dispose() throws SQLException {
		statements.close();
	}

	String generateToken () {
		return sdf.format(new Date());

	}
	@Override
	public String joblogInsert(final String processName, 
			String className, String moduleName 
			) throws SQLException {
		return joblogInsert(processName, className, moduleName,"");
	}
	@Override
	public String joblogInsert(final String processName, 
			String className, String moduleName, String statusMsg 
			) throws SQLException {
		SequenceHelper sh = new SequenceHelper(joblogConnection); // TOdo make class level
		long joblogId = sh.getSequence("JOB_LOG_ID_SEQ");
		
		String token = generateToken();
		SqlStatement ss = statements.getSqlStatement("job_log_insert");
		ss.setConnection(joblogConnection);
		Binds binds = new Binds();
		binds.put("job_log_id", joblogId);
		binds.put("job_token",token);
		binds.put("process_name", processName);
		binds.put("classname", className);
		binds.put("module_name", moduleName);
		binds.put("status_msg", statusMsg);
		binds.put("thread_name", Thread.currentThread().getName());
		binds.put("status_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		if (logger.isDebugEnabled()) {
			logger.debug("job_log_insert\n{}", ss.getSql());
		}
		ss.executeUpdate(binds);
		joblogConnection.commit();
		if (logger.isDebugEnabled()) {
			logger.debug("started job {} ", jobLogId);
			if (jobLogSelectSqlStatement2 == null) {
				jobLogSelectSqlStatement2 = new SqlStatement("select * from job_log where job_log_id = :job_log_id");
			}
			jobLogSelectSqlStatement2.setConnection(joblogConnection);
			Binds selBinds = new Binds();
			selBinds.put("job_log_id", jobLogId);
			NameValue nvs = jobLogSelectSqlStatement2.getNameValue(selBinds, true);
			logger.debug("persistJob select: {}", nvs.toString());
		}

		joblogConnection.commit();
		return token;
	}
	/*
	create or replace view my_session_process as 
    select 
    s.SADDR, s.SID, s.SERIAL#, s.AUDSID , s.PADDR,
    s.USER#, s.USERNAME, s.COMMAND, s.OWNERID, s.TADDR, 
    s.LOCKWAIT, s.STATUS , s.SERVER , s.SCHEMA#, s.SCHEMANAME, 
    s.OSUSER , s.PROCESS, s.MACHINE, s.PORT, s.TERMINAL, 
    s.PROGRAM, s.TYPE, s.SQL_ADDRESS, s.SQL_HASH_VALUE, s.SQL_ID ,
    s.SQL_CHILD_NUMBER, s.SQL_EXEC_START , s.SQL_EXEC_ID, 
    s.PREV_SQL_ADDR, s.PREV_HASH_VALUE, s.PREV_SQL_ID, s.PREV_CHILD_NUMBER, s.PREV_EXEC_START, s.PREV_EXEC_ID, 
    s.PLSQL_ENTRY_OBJECT_ID, s.PLSQL_ENTRY_SUBPROGRAM_ID, s.PLSQL_OBJECT_ID, s.PLSQL_SUBPROGRAM_ID, s.MODULE , 
    s.MODULE_HASH, s.ACTION , s.ACTION_HASH, s.CLIENT_INFO, s.FIXED_TABLE_SEQUENCE,
    s.ROW_WAIT_OBJ#, s.ROW_WAIT_FILE# , s.ROW_WAIT_BLOCK#, s.ROW_WAIT_ROW#, s.TOP_LEVEL_CALL#,
    s.LOGON_TIME, s.LAST_CALL_ET, s.PDML_ENABLED, s.FAILOVER_TYPE, s.FAILOVER_METHOD,
    s.FAILED_OVER, s.RESOURCE_CONSUMER_GROUP,
    s.PDML_STATUS, s.PDDL_STATUS, s.PQ_STATUS, s.CURRENT_QUEUE_DURATION , s.CLIENT_IDENTIFIER, s.BLOCKING_SESSION_STATUS,
    s.BLOCKING_INSTANCE, s.BLOCKING_SESSION, s.FINAL_BLOCKING_SESSION_STATUS, s.FINAL_BLOCKING_INSTANCE, s.FINAL_BLOCKING_SESSION ,
    s.SEQ#, s.EVENT# , s.EVENT, s.P1TEXT , s.P1,
    s.P1RAW, s.P2TEXT , s.P2, s.P2RAW, s.P3TEXT ,
    s.P3, s.P3RAW, s.WAIT_CLASS_ID, s.WAIT_CLASS#, s.WAIT_CLASS,
    s.WAIT_TIME, s.SECONDS_IN_WAIT, s.STATE, s.WAIT_TIME_MICRO, s.TIME_REMAINING_MICRO,
    s.TIME_SINCE_LAST_WAIT_MICRO, s.SERVICE_NAME, s.SQL_TRACE, s.SQL_TRACE_WAITS, s.SQL_TRACE_BINDS,
    s.SQL_TRACE_PLAN_STATS, s.SESSION_EDITION_ID, s.CREATOR_ADDR, s.CREATOR_SERIAL#, s.ECID,
    s.SQL_TRANSLATION_PROFILE_ID, s.PGA_TUNABLE_MEM, s.SHARD_DDL_STATUS, s.CON_ID , s.EXTERNAL_NAME,
    s.PLSQL_DEBUGGER_CONNECTED, p.PID, p.SOSID, p.SPID, p.STID,
    p.EXECUTION_TYPE , p.PNAME, p.TRACEID, p.TRACEFILE, p.BACKGROUND,
    p.LATCHWAIT, p.LATCHSPIN, p.PGA_USED_MEM, p.PGA_ALLOC_MEM, p.PGA_FREEABLE_MEM,
    p.PGA_MAX_MEM, p.NUMA_DEFAULT, p.NUMA_CURR 
    from    sys.v$session s, sys.v$process p
    where s.audsid = userenv('sessionid') and
    s.paddr = p.addr;
  */

	public Binds getSessionInfo(Connection conn) throws SQLException {
		Binds binds;
		
		String sessionSql= "select * from my_session";
		SqlStatement ss = new SqlStatement(conn,sessionSql);
		NameValue  sessionValues = ss.getNameValue();
		ss.close();
		
		binds = new Binds(sessionValues);
		
		
		String sql = "SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File'";
		ss = new SqlStatement(conn,sql);
		NameValue nv = ss.getNameValue();
		ss.close();
		binds.put("tracefile_name", nv.get("value"));
		
		return binds;
	}

	public long insertStep(String jobToken, String stepName, String stepInfo, String className) throws SQLException {
		String stackTrace = ThreadUtil.getStackTrace(2);
		return insertStep(jobToken, stepName, stepInfo, className, stackTrace);
	}

	@Override
	public long insertStep(String jobToken, String stepName, String className, String stepInfo, 
			String stacktrace) {
		long retval = -1;
		try {
			if (sequenceHelper == null) {
				throw new IllegalStateException("sequencehelper is null");
			}
			this.jobStepId = sequenceHelper.getSequence("job_step_id_seq");
			Binds binds = getSessionInfo(applicationConnection);
			
			SqlStatement ssJob = new SqlStatement(applicationConnection,
					"select * from job_log where job_token = :token");
			binds.put("token",jobToken);
			NameValue nvJob = ssJob.getNameValue(binds,true);

			binds.put("job_step_id", jobStepId);
			binds.put("job_log_id", nvJob.get("job_log_id"));
			binds.put("step_name", stepName);
			binds.put("step_info", stepInfo);
			binds.put("classname", className);
			binds.put("start_ts", new java.sql.Timestamp(System.currentTimeMillis()));
			binds.put("serial_nbr",binds.get("serial#"));
			binds.put("stacktrace", stacktrace);
			//binds.put("instance_name", );
			if (statements == null) {
				throw new IllegalStateException("statements is null");
			}
			SqlStatement ss = statements.get("job_step_insert");
			ss.setConnection(joblogConnection);
			logger.debug("job_step_id: {}",binds.get("job_step_id"));
			//logger.debug("token: {}",binds.get("token"));
			ss.executeUpdate(binds);
			joblogConnection.commit(); // TODO should be autocommit on transaction or
			logger.debug("insertStep {} with binds {} " + stepName, binds);
			retval = jobStepId;
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			logger.error(sqe.getMessage());
		}
		return retval;
	}

	@Override
	public void finishStep(long jobStepId) throws SQLException {
		Binds binds = new Binds();
		binds.put("job_step_id", jobStepId);
		binds.put("end_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		SqlStatement ss = statements.get("end_step");
		ss.setConnection(joblogConnection);
		int rowCount = ss.executeUpdate(binds);
		if (rowCount != 1) {
			logger.error(String.format("finishStep stepId %d updated %d rows", jobStepId, rowCount));
		}
		joblogConnection.commit();
	}

	private void finishJob(SqlStatement ss) throws SQLException {
		logger.debug("finishJob {} {}", jobLogId, ss.getSql());
		ss.setConnection(joblogConnection);
		Binds binds = new Binds();
		binds.put("job_log_id", jobLogId);
		binds.put("end_ts", new java.sql.Timestamp(System.currentTimeMillis()));
		int rowcount = ss.executeUpdate(binds);
		if (rowcount != 1) {
			logger.warn("job_log not updated for {}", jobLogId);
		} else {
			logger.info("finishJob: {}", jobLogId);
		}

		joblogConnection.commit();
		logger.info("job " + jobLogId + " finished =====");
	}

	@Override
	public void abortJob(String token,Exception e) throws SQLException {
		finishJob(statements.getSqlStatement("abort_job"));
	}

	@Override
	public void endJob(String token) throws SQLException {

		finishJob(statements.getSqlStatement("end_job"));
	}

	protected ListOfNameValue getUtProcessStatus(long jobNbr) throws SQLException {
		String sql = "select * from job_log order by job_log_id";
		if (jobLogSelectSqlStatement == null) {
			jobLogSelectSqlStatement = new SqlStatement(joblogConnection, sql);
		}
		return jobLogSelectSqlStatement.getListOfNameValue(new Binds());

	}

	public void showUtProcessStep() throws SQLException {
		String sql = "select * from job_step order by job_step_id";
		if (jobStepOrderSqlStatement == null) {
			jobStepOrderSqlStatement = new SqlStatement(joblogConnection, sql);
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
			upsStatement = new SqlStatement(joblogConnection, ups);
		}
		Binds binds = new Binds();
		binds.put("job_log_id", jobId);
		NameValue upsRow = upsStatement.getNameValue(binds, true);
		//
		//		String traceFileName = upsRow.getString("tracefile_name");
		//		if (traceFileName == null) {
		//			logger.warn("tracefileName is null");
		//			//throw new IllegalStateException("traceFileName is null");
		//		} else {
		//			//
		//			//
		//			// TODO this is reading directly from the file 
		//			Clob clob = connection.createClob();
		//			String tracefileData = null;
		//			try {
		//				tracefileData = FileUtil.getAsString(traceFileName);
		//			} catch (IOException e) {
		//				logger.error(e.getMessage());
		//			}
		//			clob.setString(1, tracefileData);
		//			//
		//			Clob jsonClob = connection.createClob();


		//			OracleTraceProcessor tfr;
		//			try {
		//				tfr = new OracleTraceProcessor(connection, traceFileName);
		//				tfr.process();
		//				CursorsStats cursorStats = tfr.getCursors();
		//				String jsonString = cursorStats.toString();
		//				jsonClob.setString(1, jsonString);
		//
		//				PreparedStatement updateTraceFile = connection.prepareStatement(upd);
		//
		//				updateTraceFile.setClob(1, clob);
		//				updateTraceFile.setClob(2, jsonClob);
		//				updateTraceFile.setLong(3, jobId);
		//				int count = updateTraceFile.executeUpdate();
		//
		//				binds.put("tracefile_data", clob);
		//				if (count != 1) {
		//					throw new IllegalArgumentException("unable to update job_log_id " + jobId);
		//				}
		//				logger.info("updated {}", jobId);
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//				logger.error(e.getMessage());
		//			}
		//		}
	}

	public Connection getConnection() {
		return joblogConnection;
	}

	public void updateTraceFileName(String appTracefileName) throws SQLException {
		logger.info("*** updating trace to {}", appTracefileName);
		if (updateJobLogSqlStatement == null) {
			updateJobLogSqlStatement = new SqlStatement(joblogConnection,
					"update job_log set tracefile_name = :tracefile_name " + "where job_log_id = :job_log_id");
		}
		Binds binds = new Binds();
		binds.put("tracefile_name", appTracefileName);
		binds.put("job_log_id", jobLogId);
		int rowCount = updateJobLogSqlStatement.executeUpdate(binds);
		joblogConnection.commit();
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
		Clob clob = joblogConnection.createClob();
		String upd = "update job_log " + "set tracefile_data =  ? " + "where job_log_id = ?";

		Reader traceReader = traceClob.getCharacterStream();

		PreparedStatement updateTraceFile = joblogConnection.prepareStatement(upd);

		updateTraceFile.setCharacterStream(1, traceReader);
		updateTraceFile.setLong(2, jobId);

		int rowcount = updateTraceFile.executeUpdate();

		if (rowcount != 1) {
			throw new IllegalArgumentException("unable to update job_log_id " + jobId);
		}

	}
//
//	@Override
//	public Clob createClob() throws SQLException {
//		return joblogConnection.createClob();
//	}

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
	public void prepareConnection() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setModule(String string, String string2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAction(String string) {
		// TODO Auto-generated method stub

	}



	@Override
	public void setPersistPlansOnSQLExceptionJobCompletion(boolean persistPlans) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ensureDatabaseObjects() throws SQLException {
		// TODO Auto-generated method stub

	}



}
