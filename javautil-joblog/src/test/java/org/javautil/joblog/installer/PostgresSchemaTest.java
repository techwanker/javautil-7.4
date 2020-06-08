package org.javautil.joblog.installer;




import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlStatement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

// TOD collapse these installers
public class PostgresSchemaTest  {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static DataSource dataSource;
	private static Connection connection;

	@BeforeClass
	public static void beforeClass() throws SQLException {
		if (dataSource == null) {
			String url= "jdbc:postgresql://localhost/joblog";
			String username = System.getenv("USER");
			String password = "";
			dataSource = getDataSource(url,username,password);
			connection = dataSource.getConnection();
		}
	}

	public static void afterClass() throws IOException, SQLException {
		connection.close();
		((Closeable) dataSource).close();
	}

	@Test
	public void createSchemaTest() throws Exception {
		String dropSchema = "drop schema joblog cascade";
		Statement stmt  = connection.createStatement();
		stmt.execute(dropSchema);
		String createSchema = "create schema joblog";
		stmt.execute(createSchema);
		String searchPath = "set search_path to joblog";
		stmt.execute(searchPath);
		String create= "create table x (y integer)";
		stmt.execute(create);
		connection.commit();
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
