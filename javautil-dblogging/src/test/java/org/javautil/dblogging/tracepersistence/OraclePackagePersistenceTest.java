package org.javautil.dblogging.tracepersistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.SqlStatement;
import org.javautil.dblogging.BaseTest;
import org.javautil.util.NameValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OraclePackagePersistenceTest extends BaseTest {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private boolean showSql = false;

	@Test
	public void dummmy() {

	}

	// @Test TODO restore
	public void test() throws SQLException {
		OraclePackagePersistence opp = getOraclePackagePersistence();
		long jobLogId = opp.getNextJobLogId();
		opp.persistJob("A", getClass().getName(), "moduleName", "statusMsg", "target/tmp/x", "SR", jobLogId);
		opp.insertStep("stepName", "stepInfo", getClass().getName(), "stack");
		Connection testConnection = getApplicationDataSource().getConnection();
		//
		SqlStatement jobStmt = new SqlStatement("select * from job_log where job_log_id = :job_log_id", testConnection);
		Binds b = new Binds();
		b.put("job_log_id", jobLogId);
		NameValue jobNv = jobStmt.getNameValue(b, true);
		logger.debug("jobNv: {}", jobNv.toString());
		//
		SqlStatement stepStmt = new SqlStatement("select * from job_step where job_log_id = :job_log_id");
		NameValue stepNv = stepStmt.getNameValue(b, true);
		logger.debug("stepNv: {}", stepNv.toString());

	}
}
