package org.javautil.joblog.installer;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlStatement;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PostgresInstallTest {

	// TODO WT

	private static DataSource jobDataSource;
	private static Connection jobConnection;

	@BeforeClass
	public static void beforeClass() throws SQLException {
		if (jobDataSource == null) {
			String url= "jdbc:postgresql://localhost/joblog";
			String username = System.getenv("USER");
			String password = "";
			jobDataSource = getDataSource(url,username,password);
			jobConnection = jobDataSource.getConnection();
		}
	}

	public static void afterClass() throws IOException, SQLException {
		jobConnection.close();
		((Closeable) jobDataSource).close();
	}
	@Test
	public void testDrop() throws Exception  {
		DataSourceFactory dsf = new DataSourceFactory();
	
		PostgresInstall installer = new  PostgresInstall(jobConnection);
		//installer.drop();
		installer.process();
		jobConnection.close();
		((Closeable) jobDataSource).close();
	}
	
	public static DataSource getDataSource(String url, String username, String password) {
		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setAutoCommit(false);
		return new HikariDataSource(config);
	}
}