package org.javautil.joblog.persistence;

import java.sql.Clob;
import java.sql.SQLException;

public interface JoblogPersistence {

	long persistJob(String processName, String className, String moduleName, String statusMsg, String tracefileName,
			String schema, long jobLogId) throws SQLException;

	void abortJob(Exception e) throws SQLException;

	void endJob() throws SQLException;

	long insertStep(String stepName, String stepInfo, String className, String stack);

	void finishStep() throws SQLException;

	Clob createClob() throws SQLException;

	public void persistenceUpdateTrace(long jobId, Clob traceData) throws SQLException;

	/**
	 * Store the trace file in job_log on job_abort or job_end.
	 * 
	 * This burns some cycles on the instrumented application but ensures the file
	 * is not lost.
	 * 
	 * @param persistTrace true if the trace file should be written to the database
	 */
	public void setPersistTraceOnJobCompletion(boolean persistTrace);

	public void setPersistPlansOnJobCompletion(boolean persistPlans);

	long getJobLogId();

	long getNextJobLogId();

}