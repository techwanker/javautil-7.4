package com.pacificdataservices.pdssr;
//import org.javautil.sql.H2MemDatasource;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static DataSourceFactory dataSourceFactory = new DataSourceFactory();

	private Dialect dialect;

	public DbTest() {
		
	}
	public DbTest(Dialect dialect) {
		this.dialect = dialect;
	}

	Connection getConnection() throws SQLException, PropertyVetoException, FileNotFoundException {
		Connection connection = null;
		switch (this.dialect) {
		case H2:
			connection = getH2Connection();
			break;
		case POSTGRES:
			connection = getPostgresConnection();
			break;
		default:
			throw new IllegalStateException();
		}
		return connection;
	}

	Connection getH2Connection() throws SQLException, PropertyVetoException {
		return getH2FileConnection();
	}

	Connection getH2MemoryConnection() throws SQLException, PropertyVetoException {
		DataSource ds = DataSourceFactory.getInMemoryDataSource();
		return ds.getConnection();
	}

	Connection getH2FileConnection() throws SQLException, PropertyVetoException {
		HashMap<String, Object> parms = new HashMap<String, Object>();
		parms.put("driver_class", "org.h2.Driver");
		parms.put("url", "jdbc:h2:/tmp/it.dbf");
		DataSource ds = DataSourceFactory.getDatasource(parms);
		return ds.getConnection();
	}
	
	Connection getOracleConnection() throws PropertyVetoException, SQLException {
		DataSource ds = dataSourceFactory.getDatasource("integration_oracle");
		return ds.getConnection();
		
	}

	Connection getPostgresConnection() throws FileNotFoundException, PropertyVetoException, SQLException {
		logger.debug("getting connection");
		// String homeDir = System.getProperty("user.home");
		// String yamlName = homeDir + "/connections_java.yaml";
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDatasource("integration_postgres");
		Connection conn = ds.getConnection();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
//		stmt.execute("set schema 'integration_test'");
//		logger.warn("using schema integration_test");
		logger.debug("returning connection");
		return conn;
	}

	Dialect getDialect() {
		return dialect;
	}

}
