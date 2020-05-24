package org.javautil.joblog.logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.joblog.persistence.DbloggerPersistenceImpl;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.joblog.traceagent.DbloggerApplication;
import org.javautil.joblog.traceagent.DbloggerForOracleApplication;
import org.javautil.lang.ThreadUtil;
import org.javautil.oracle.OracleSessionInfo;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence is performed in a different database
 * 
 * @author jjs
 *
 */
public class SplitLoggerForOracle implements Joblog {

	private JoblogPersistence persistenceLogger;

	private DbloggerApplication applicationLogger;

	private long jobId;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private boolean throwExceptions = true; // TODO implement as RunTimeExceptions

	private Connection applicationConnection;

	public SplitLoggerForOracle(Connection connection, Connection loggerPersistenceConnection)
			throws IOException, SQLException, SqlSplitterException {
		// super(connection);
		Dialect persistenceDialect = Dialect.getDialect(loggerPersistenceConnection);
		switch (persistenceDialect) {
		case ORACLE:
			this.persistenceLogger = new DbloggerPersistenceImpl(loggerPersistenceConnection);
			break;
		case H2:
			this.persistenceLogger = new DbloggerPersistenceImpl(loggerPersistenceConnection);
			break;
		default:
			throw new IllegalArgumentException("Unsupported logger Dialect: " + persistenceDialect);
		}
		applicationConnection = connection;
		applicationLogger = new DbloggerForOracleApplication(connection);
		logger.debug("SplitLogger constructor: {}", OracleSessionInfo.getConnectionInfo(connection));

	}

	public SplitLoggerForOracle(Connection connection, JoblogPersistence persistenceLogger)
			throws IOException, SQLException, SqlSplitterException {

		logger.debug("SplitLogger constructor: {}", OracleSessionInfo.getConnectionInfo(connection));
		this.persistenceLogger = persistenceLogger;
		applicationConnection = connection;
		applicationLogger = new DbloggerForOracleApplication(connection);
	}

	@Override
	public void prepareConnection() throws SQLException {
		applicationLogger.prepareConnection();

	}

	@Override

	public long startJobLogging(String processName, String className, String moduleName, String statusMsg,
			int traceLevel) throws SQLException {
		logger.warn("tracefileName ignored");
		jobId = persistenceLogger.getNextJobLogId();
		SqlStatement ss = new SqlStatement("select user from dual");
		ss.setConnection(applicationConnection);
		NameValue userNv = ss.getNameValue();
		String schema = userNv.getString("user");

		String tracefileName = applicationLogger.appJobStart(processName, className, moduleName, statusMsg, jobId,
				traceLevel);
		persistenceLogger.persistJob(processName, className, moduleName, statusMsg, schema, tracefileName, jobId);
		return jobId;
	}

	@Override
	public void abortJob(Exception e) {
		try {
			persistenceLogger.abortJob(e);
			updateJobWithTrace();
		} catch (SQLException e1) {
			logger.error("", e1);
		}

	}

	@Override
	public void endJob() throws SQLException {
		persistenceLogger.endJob();
		updateJobWithTrace();
	}

	@Override
	public long insertStep(String stepName, String stepInfo, String className) {
		String stack = ThreadUtil.getStackTrace();
		if (stack.length() > 4000) {
			stack = stack.substring(0, 4000);
		}
		if (jobId != persistenceLogger.getJobLogId()) {
			String message = String.format("this jobId %d  persistenceJobId %d", this.jobId,
					persistenceLogger.getJobLogId());
			throw new IllegalStateException(message);
		}
		long stepId = persistenceLogger.insertStep(stepName, stepInfo, className, stack);

		try {
			applicationLogger.setTraceStep(stepName, stepId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
		return stepId;
	}

	@Override
	public void finishStep() {
		try {
			persistenceLogger.finishStep();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		}

	}

	public String getTraceFileName(Connection conn, long id) throws SQLException {
		SqlStatement ss = new SqlStatement(conn, "select * from job_log where job_log_id = :job_log_id");
		Binds binds = new Binds();
		binds.put("job_log_id", id);
		final NameValue status = ss.getNameValue(binds, false);

		String tracefileName = status.getString("TRACEFILE_NAME");
		return tracefileName;
	}

	public void updateJobWithTrace() throws SQLException {
		StringWriter traceFileWriter = new StringWriter();

		Clob inputClob = null;
		try {
			inputClob = applicationLogger.getMyTraceFile();

		} catch (Exception e) {
			String message = "Does directory 'UDUMP_DIR' exist? Is this where the trace files are? Can this oracle reader read the diretory?";
			logger.error(message + " " + e.getMessage());
			throw new RuntimeException(e);
		}
		persistenceLogger.persistenceUpdateTrace(jobId, inputClob);

		logger.debug("updated {}", jobId);
	}

	@Override
	public void setAction(String actionName) {
		try {
			applicationLogger.setAction(actionName);
		} catch (SQLException e) {
			logger.error("setAction: {}", e.getMessage());
		}
	}

	@Override
	public void setModule(String moduleName, String actionName) throws SQLException {
		applicationLogger.setModule(moduleName, actionName);
	}

	@Override
	public long insertStep(String stepName, String stepInfo, String className, String stack) {
		return persistenceLogger.insertStep(stepName, stepInfo, className, null);
	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
		persistenceLogger.setPersistTraceOnJobCompletion(persistTrace);

	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
		persistenceLogger.setPersistPlansOnJobCompletion(persistPlans);

	}

}
