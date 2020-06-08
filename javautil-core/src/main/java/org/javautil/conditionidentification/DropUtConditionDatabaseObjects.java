package org.javautil.conditionidentification;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropUtConditionDatabaseObjects {

	private final Logger  logger     = LoggerFactory.getLogger(this.getClass());
	private Connection    connection = null;
	InputStream           conditionDdl;
	private final boolean showSql;

	public DropUtConditionDatabaseObjects(Connection conn, Dialect dialect, boolean showSql) {
		this.showSql = showSql;
		logger.info("creating with dialect " + dialect);
		this.connection = conn;
		switch (dialect) {
		case H2:
			conditionDdl = getResource("drop_ut_condition_tables.h2.sr.sql");
			break;
		case POSTGRES:
			conditionDdl = getResource("drop_ut_condition_tables.postgres.sr.sql");
			break;
		case ORACLE:
			conditionDdl = getResource("drop_ut_condition_tables.h2.sr.sql");
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect " + dialect);
		}
	}

	InputStream getResource(String resourceName) {
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		if (is == null) {
			throw new IllegalStateException("resource " + resourceName + " failed to load");
		}
		return is;
	}

	public void process() throws SQLException, IOException {
		SqlRunner runner;
		logger.info("about to drop condition tables");
		runner = new SqlRunner(conditionDdl);
		runner.setConnection(connection).setShowError(true).setContinueOnError(true).setShowSql(showSql);
		runner.setCommitOrRollbackEveryStatement(true);
		runner.process();
	}
}
