package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.misc.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlStatementRunner {
	private final Logger  logger = LoggerFactory.getLogger(this.getClass());

	private Connection    connection;
	private SqlStatements sqlStatements;
	private Dialect       dialect;
	private int           verbosity;

	public SqlStatementRunner(Connection connection, Dialect dialect, SqlStatements sqlStatements, int verbosity) {
		this.connection = connection;
		this.dialect = dialect;
		this.sqlStatements = sqlStatements;
		this.verbosity = verbosity;
	}

	public void process(Binds binds) throws SQLException {
		for (SqlStatement stmt : sqlStatements) {
			stmt.setConnection(connection);

			Timer t = new Timer();
			if (verbosity > 3) {
				logger.info("running " + stmt.getName());
			}
			int updateCount = stmt.executeUpdate(binds);
			if (verbosity > 0) {
				logger.info(stmt.getName() + " records " + updateCount + " millis " + t.getElapsedMillis());
			}
		}

	}

}
