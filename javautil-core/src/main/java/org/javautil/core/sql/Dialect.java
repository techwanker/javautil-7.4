package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Dialect {

	POSTGRES, SQLITE, H2, ORACLE, UNSPECIFIED;

	public static Dialect getDialect(Connection connection) throws SQLException {
		Logger logger = LoggerFactory.getLogger(Dialect.class);
		// logger.info("Connection class is " + connection.getClass().getName());
		DatabaseMetaData metadata = connection.getMetaData();
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Database Product Name: " + metadata.getDatabaseProductName() + "\n");
			sb.append("Database Product Version: " + metadata.getDatabaseProductVersion() + "\n");
			sb.append("Logged User: " + metadata.getUserName() + "\n");
			sb.append("JDBC Driver: " + metadata.getDriverName() + "\n");
			sb.append("Driver Version: " + metadata.getDriverVersion() + "\n");

		}
		if (metadata.getDatabaseProductName().equals("PostgreSQL")) {
			return POSTGRES;
		}
		if (connection.isWrapperFor(oracle.jdbc.OracleConnection.class)) {
			return ORACLE;
		}
		if (connection.isWrapperFor(org.h2.jdbc.JdbcConnection.class)
		    || connection.getClass().equals(org.h2.jdbc.JdbcConnection.class)) {
			return H2;
		}
		throw new IllegalArgumentException("Unknown driver " + connection);
	}

	boolean useQuestionBinds() {
		switch (this) {
		case ORACLE:
			return false;
		default:
			return true;
		}
	}

}
