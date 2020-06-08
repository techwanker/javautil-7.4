package org.javautil.joblog;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlStatement;

public class DataSources {

	private static DataSourceFactory dsf = new DataSourceFactory();

	public static DataSource  getOracleDataSource() throws PropertyVetoException {
		return  dsf.getDatasource("integration_oracle");
	}

	public static DataSource getPostgresJoblogDataSource() throws PropertyVetoException {
		String url= "jdbc:postgresql://localhost/joblog";
		String username = System.getenv("USER");
		String password = "";
		DataSource 	jobDataSource = DataSourceFactory.getDataSource(url,username,password);
		return jobDataSource;
	}

	public static void initializePostgresJoblogConnection(Connection connection) throws SQLException {
		String dropSchema = "drop schema joblog cascade";
		SqlStatement ss = new SqlStatement(connection,dropSchema);
		ss.execute(new Binds());
		String createSchema = "create schema joblog";
		ss = new SqlStatement(connection,createSchema);
		ss.execute(new Binds());
		ss = new SqlStatement(connection,"set search_path to joblog");
		ss.execute(new Binds());
	}

	public static DataSource getPostgresDataSource() throws PropertyVetoException {
		return  dsf.getDatasource("joblog_postgres");
	}

	public static DataSource getH2DataSource() throws PropertyVetoException {
		return  DataSourceFactory.getH2Permanent("/tmp/JobLogTest1", "sa", "tutorial");
	}
}
