package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class DataSourceHelper {

	private DataSource          dataSource;

	private List<String>        sqlTexts;

	private Map<String, Object> parms;

	public DataSourceHelper(DataSource ds, Map<String, Object> parms) {
		this.dataSource = ds;
		this.parms = parms;
	}

	public DataSourceHelper(DataSource ds, List<String> sqlTexts, Map<String, Object> parms) {
		this.dataSource = ds;
		this.sqlTexts = sqlTexts;
		this.parms = parms;
	}

	Dialect getDialect() {
		String driverName = (String) parms.get("driver_class");
		if (driverName == null) {
			throw new IllegalStateException("no driver_class in " + parms);
		}

		Dialect retval = null;
		if (driverName.contains("postgres")) {
			retval = Dialect.POSTGRES;
		} else if (driverName.contains("sqlite")) {
			retval = Dialect.SQLITE;
		} else if (driverName.contains("h2")) {
			retval = Dialect.H2;
		} else {
			throw new IllegalArgumentException("unknown driver " + driverName);
		}

		return retval;
	}

	public Connection getConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement s = conn.createStatement();
		if (sqlTexts != null) {
			for (String sql : sqlTexts) {
				s.execute(sql);
			}
		}
		return conn;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
