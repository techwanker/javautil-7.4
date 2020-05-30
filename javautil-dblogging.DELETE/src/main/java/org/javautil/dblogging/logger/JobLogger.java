package org.javautil.dblogging.logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.dblogging.traceagent.DbloggerApplication;
import org.javautil.dblogging.traceagent.DbloggerForOracleApplication;
import org.javautil.dblogging.tracepersistence.DbloggerPersistence;
import org.javautil.dblogging.tracepersistence.DbloggerPersistenceImpl;
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
public class JobLogger implements Dblogger {

	private DbloggerPersistence persistenceLogger;


	private long jobId;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private boolean throwExceptions = true; // TODO implement as RunTimeExceptions

	private Connection connection ;

	public JobLogger( Connection loggerPersistenceConnection)
			throws IOException, SQLException, SqlSplitterException {
		this.connection = loggerPersistenceConnection;
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
//		logger.debug("JobLogger constructor: {}", OracleSessionInfo.getConnectionInfo(connection));

	}

	public JobLogger(Connection connection, DbloggerPersistence persistenceLogger)
			throws IOException, SQLException, SqlSplitterException {

		logger.debug("SplitLogger constructor: {}", OracleSessionInfo.getConnectionInfo(connection));
		this.persistenceLogger = persistenceLogger;
	}


	@Override

	public long startJobLogging(String processName, String className, String moduleName, String statusMsg,
			int traceLevel) throws SQLException {
		logger.warn("tracefileName ignored");
		jobId = persistenceLogger.getNextJobLogId();
		//SqlStatement ss = new SqlStatement("select user from dual");
		//ss.setConnection(applicationConnection);
		//NameValue userNv = ss.getNameValue();
		//String schema = userNv.getString("user");
		String schema=null;
		persistenceLogger.persistJob(processName, className, moduleName, statusMsg, schema, null, jobId);
		return jobId;
	}

	@Override
	public void abortJob(Exception e) {
		try {
			persistenceLogger.abortJob(e);
		} catch (SQLException e1) {
			logger.error("", e1);
		}

	}

	@Override
	public void endJob() throws SQLException {
		persistenceLogger.endJob();
	}

	@Override
	public long insertStep(String stepName, String stepInfo, String className, String stack)
 {
		//String stack = ThreadUtil.getStackTrace();
		if (stack != null  && stack.length() > 4000) {
			stack = stack.substring(0, 4000);
		}
		if (jobId != persistenceLogger.getJobLogId()) {
			String message = String.format("this jobId %d  persistenceJobId %d", this.jobId,
					persistenceLogger.getJobLogId());
			throw new IllegalStateException(message);
		}
		long stepId = persistenceLogger.insertStep(stepName, stepInfo, className, stack);
		return stepId;
	}


//	@Override
//	public long insertStep(String stepName, String stepInfo, String className, String stack) {
//		return persistenceLogger.insertStep(stepName, stepInfo, className, null);
//	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
		persistenceLogger.setPersistTraceOnJobCompletion(persistTrace);

	}

	@Override
	public void setAction(String string) {
		// nooP
		
	}

	@Override
	public void setModule(String moduleName, String actionName) throws SQLException {
		// noop
	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
		// noop
		
	}

	@Override
	public void finishStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareConnection() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long insertStep(String string, String ruleName, String name) {
		// TODO Auto-generated method stub
		return 0;
	}


}