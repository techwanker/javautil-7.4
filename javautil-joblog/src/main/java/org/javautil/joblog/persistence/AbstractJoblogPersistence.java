package org.javautil.joblog.persistence;

import java.sql.SQLException;

import org.javautil.core.sql.SequenceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJoblogPersistence  implements JoblogPersistence{

	private Logger logger  = LoggerFactory.getLogger(getClass());
	private boolean persistTraceOnJobCompletion;
	private boolean persistPlansOnJobCompletion;
	private SequenceHelper sequenceHelper;
	private boolean throwExceptions;
	private boolean persistPlans;
	//void setPersistPlansOnSQLExceptionJobCompletion(boolean persistPlans)o	void setPersistPlansOnSQLExceptionJobCompletion(boolean persistPlans);
	public AbstractJoblogPersistence() {
		super();
	}

	@Override
	public void setPersistTraceOnJobCompletion(boolean persistTrace) {
		this.persistTraceOnJobCompletion = persistTrace;
	}

	@Override
	public void setPersistPlansOnJobCompletion(boolean persistPlans) {
		this.persistPlansOnJobCompletion = persistPlans;
	}

	@Override
	public long getNextJobLogId() {
		long retval = -1L;
		try {
			retval = sequenceHelper.getSequence("job_log_id_seq");
		} catch (SQLException e) {
			if (throwExceptions) {
				throw new RuntimeException(e);
			} else {
				logger.error(e.getMessage());
			}
		}
		return retval;
	}
	@Override
	public String joblogInsert(final String processName, String className, String moduleName 
			) throws SQLException {
		return joblogInsert(processName, className, moduleName,"");
	}

	@Override
	public void prepareConnection() throws SQLException {
		// TODO Auto-generated method s

	}

	@Override
	public void setModule(String module, String action) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAction(String string) {
		// TODO Auto-generated method stub

	}

	//	@Override
	//	public void setPersistPlansOnSQLExceptionJobCompletion(boolean persistPlans) {
	//		this.persistPlans = persistPlans;
	//	}

	@Override
	public void ensureDatabaseObjects() throws SQLException {
		// TODO Auto-generated method stub

	}


}