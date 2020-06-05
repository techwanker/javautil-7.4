package org.javautil.joblog.persistence;

import java.sql.Clob;
import java.sql.SQLException;

public class JoblogPersistenceNoOperation implements JoblogPersistence
{

	@Override
	public void prepareConnection() throws SQLException {
	}

	public String joblogInsert(String processName, String className, String moduleName) 
			throws SQLException
			{
				return "";
			}
	public String joblogInsert(String processName, String className, String moduleName, String statusMsg) throws SQLException
	{
		return "";
	};

	@Override
	public void abortJob(String jobToken, Exception e) {
	}

	@Override
	public void endJob(String token) {
	}

	@Override
	public void setAction(String actionName) {
	}

	@Override
	public void setModule(String moduleName, String actionName) throws SQLException {
	}

	@Override
	public void finishStep(long stepId) {
	}

	@Override
	public long insertStep(String jobToken,String stepName, String stepInfo, String className) {
		return -1;
	}

	@Override
	public long insertStep(String jobToken, String stepName, String stepInfo, String className, String stack) {
		return -1;
	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
	}


	public void ensureDatabaseObjects() {
	}

//
//	@Override
//	public Clob createClob() throws SQLException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void persistenceUpdateTrace(long jobId, Clob traceData) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public long getNextJobLogId() {
		// TODO Auto-generated method stub
		return 0;
	}

//	@Override
//	public long jobLogInsert(String processName, String className, String moduleName, long jobId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	@Override
	public void setPersistPlansOnSQLExceptionJobCompletion(boolean persistPlans) {
		// TODO Auto-generated method stub
		
	}




//    @Override
//    public long beginJob(String processName, String processInfo, int traceLevel) {
//        // TODO Auto-generated method stub
//        return 0;
//    }

}
