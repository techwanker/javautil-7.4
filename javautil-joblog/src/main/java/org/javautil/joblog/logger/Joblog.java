package org.javautil.joblog.logger;

import java.sql.SQLException;

public interface Joblog {

	/**
	 * Job terminated with error.
	 * 
	 * @param e the exception that was raised
	 * @throws SQLException not likely
	 */
	void abortJob(Exception e) throws SQLException;

	void endJob() throws SQLException;

	void finishStep();

	/**
	 * @param string   step name
	 * @param ruleName the rule name
	 * @param name     no idea
	 * @return the step number
	 */
	long insertStep(String string, String ruleName, String name);

	long insertStep(String stepName, String stepInfo, String className, String stack);

	void prepareConnection() throws SQLException;

	void setAction(String string);

	void setModule(String moduleName, String actionName) throws SQLException;

	void setPersistPlansOnJobCompletion(boolean persistPlans);

	void setPersistTraceOnJobCompletion(boolean persistTrace);

	/**
	 * This should be the first call in a job.
	 * 
	 * String tracefileName = applicationLogger.appJobStart(processName, className,
	 * moduleName, statusMsg, jobId, traceLevel);
	 * persistenceLogger.persistJob(processName, className, moduleName, statusMsg,
	 * schema, tracefileName, jobId);
	 * 
	 * @param processName ??? TODO
	 * @param className   the calling class
	 * @param moduleName  user defined module
	 * @param statusMsg   ??? TODO
	 * @param traceLevel  0 to 9
	 * @return job_log_id
	 * @throws SQLException if a SqlException occurs
	 */
	long startJobLogging(String processName, String className, String moduleName, String statusMsg, int traceLevel)
			throws SQLException;

}