package org.javautil.dblogging.traceagent;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

public interface DbloggerApplication {

	public String appJobStart(final String processName, final String className, String moduleName, String statusMsg,
			long jobLogId, int traceLevel) throws SQLException;

	void dispose() throws SQLException;

	public Clob getMyTraceFile() throws SQLException;

	void getMyTraceFile(File file) throws IOException, SQLException;

	void getMyTraceFile(Writer writer) throws SQLException, IOException;

	long getNextJobId();

	String getTraceFileName() throws SQLException;

	String openFile(String fileName) throws SQLException;

	void prepareConnection() throws SQLException;

	void setAction(String actionName) throws SQLException;

	void setModule(String moduleName, String actionName) throws SQLException;

	String setTracefileIdentifier(long jobId);

	public void setTraceStep(final String stepName, long jobStepId) throws SQLException;

	void showConnectionInformation();

}