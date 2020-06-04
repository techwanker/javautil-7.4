package org.javautil.joblog.traceagent;

import java.sql.Clob;
import java.sql.SQLException;

public interface JoblogTracer {

	public String jobStart(final String processName, final String className, 
			String moduleName, String statusMsg,
			long jobLogId, int traceLevel) throws SQLException;
//
//	void dispose() throws SQLException;
//
	public Clob getMyTraceFile() throws SQLException;
//
//	void getMyTraceFile(File file) throws IOException, SQLException;
//
//	void getMyTraceFile(Writer writer) throws SQLException, IOException;
//
//	long getNextJobId();
//
//	String getTraceFileName() throws SQLException;
//
//	String openFile(String fileName) throws SQLException;
//
	void prepareConnection() throws SQLException;
//
//	String setTracefileIdentifier(long jobId);
//
	public void setTraceStep(final String stepName, long jobStepId) throws SQLException;
//
//	void showConnectionInformation();
//
//    // 
	void setAction(String actionName) throws SQLException;

	void setModule(String moduleName, String actionName) throws SQLException;
}