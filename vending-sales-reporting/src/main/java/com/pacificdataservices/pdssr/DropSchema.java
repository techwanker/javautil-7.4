package com.pacificdataservices.pdssr;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropSchema {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Connection connection = null;
	private Dialect dialect = null;
	InputStream salesReportingDdl;
	private boolean showSql = true;

	public DropSchema(Connection conn, Dialect dialect) {
		logger.warn("creating with dialect " + dialect);
		this.connection = conn;
		this.dialect = dialect;
		switch (dialect) {
//		case H2:
//			salesReportingDdl = getResource("sales_reporting_ddl.h2.sql");
//		//	conditionDdl= getResource("ut_condition_tables.h2.sr.sql");
//			break;
		case POSTGRES:
			salesReportingDdl = getResource("drop_sales_reporting_ddl.postgres.sr.sql");
			// conditionDdl= getResource("ut_condition_tables.postgres.sr.sql");
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect " + dialect);

		}
	}

	InputStream getResource(String resourceName) {
		URL url = this.getClass().getResource(resourceName);
		logger.info("resourceName " + resourceName + " url " + url);
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		if (is == null) {
			throw new IllegalStateException("resource " + resourceName + " failed to load");
		}
		return is;
	}

	public void process() throws SQLException, IOException {
		logger.info("about to create sales tables " + salesReportingDdl);
		SqlRunner runner = new SqlRunner(salesReportingDdl);
		runner.setConnection(connection).setShowError(true).setContinueOnError(true).setShowSql(showSql);
		runner.setCommitOrRollbackEveryStatement(true);
		runner.process();
		salesReportingDdl.close();
	}
}
