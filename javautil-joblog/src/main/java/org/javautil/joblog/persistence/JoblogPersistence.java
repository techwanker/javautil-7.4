package org.javautil.joblog.persistence;

import java.sql.Clob;
import java.sql.SQLException;

public interface JoblogPersistence {
	String joblogInsert(String processName, String className, String moduleName) throws SQLException;
	String joblogInsert(String processName, String className, String moduleName, String statusMsg) throws SQLException;
	
	void abortJob(String jobToken, Exception e) throws SQLException;

	void endJob(String jobToken) throws SQLException;

	long insertStep(String jobToken, String stepName, String className, String stepInfo) throws SQLException;
	void finishStep(long jobStepId) throws SQLException;


	public void persistenceUpdateTrace(long jobId, Clob traceData) throws SQLException;

	/**
	 * Store the trace file in job_log on job_abort or job_end.
	 * 
	 * This burns some cycles on the instrumented application but ensures the file
	 * is not lost.
	 * 
	 * @param persistTrace true if the trace file should be written to the database
	 */


	long getNextJobLogId();

	void prepareConnection() throws SQLException;

	void setModule(String string, String string2) throws SQLException;


	void setAction(String string);

	void ensureDatabaseObjects() throws SQLException;
	void setPersistTraceOnJobCompletion(boolean persistTrace);
	void setPersistPlansOnJobCompletion(boolean persistPlans);


}