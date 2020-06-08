package org.javautil.conditionidentification;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUtConditionDatabaseObjects {

	private final Logger  logger     = LoggerFactory.getLogger(this.getClass());
	private Connection    connection = null;
	InputStream           conditionDdl;
	private final boolean showSql;
	private boolean       drop;

	public CreateUtConditionDatabaseObjects(Connection conn, Dialect dialect, boolean showSql) {
		this.showSql = showSql;
		logger.warn("creating with dialect " + dialect);
		this.connection = conn;
		switch (dialect) {
		case H2:
			conditionDdl = getResource("ut_condition_tables.h2.sr.sql");
			break;
		case POSTGRES:
			conditionDdl = getResource("ut_condition_tables.postgres.sr.sql");
			break;
		case ORACLE:
			conditionDdl = getResource("ut_condition_tables.oracle.sr.sql");
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
		logger.info("about to create condition tables");
		runner = new SqlRunner(conditionDdl);
		runner.setConnection(connection).setShowError(true).setContinueOnError(false).setShowSql(showSql);
		runner.process();

		conditionDdl.close();
	}

	/**
	 * @return the conditionDdl
	 */
	public InputStream getConditionDdl() {
		return conditionDdl;
	}

	/**
	 * @param conditionDdl the conditionDdl to set
	 */
	public void setConditionDdl(InputStream conditionDdl) {
		this.conditionDdl = conditionDdl;
	}

	/**
	 * @return the drop
	 */
	public boolean isDrop() {
		return drop;
	}

	/**
	 * @param drop the drop to set
	 */
	public void setDrop(boolean drop) {
		this.drop = drop;
	}

	/**
	 * @return the showSql
	 */
	public boolean isShowSql() {
		return showSql;
	}
}
