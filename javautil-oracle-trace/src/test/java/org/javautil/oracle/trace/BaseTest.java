package org.javautil.oracle.trace;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseTest {

	static DataSource joblogDataSource;
	static Connection joblogConnection;

	static DataSource appDataSource;
	private static Connection appConnection;
	private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
	boolean showSql = false;

	@BeforeClass
	public static void beforeClass() throws SqlSplitterException, Exception {
		DataSourceFactory dsf = new DataSourceFactory();

		appDataSource = dsf.getDatasource("integration_oracle");
		setAppConnection(appDataSource.getConnection());
		joblogDataSource = dsf.getDatasource("integration_oracle");
		joblogConnection = joblogDataSource.getConnection();


	}

	@AfterClass
	public static void afterClass() throws IOException {
		logger.warn("\n***********\nclosing applicationDatasource\n*************");

		((Closeable) appDataSource).close();
		((Closeable) joblogDataSource).close();
	}

	public static Connection getAppConnection() {
		return appConnection;
	}

	public static void setAppConnection(Connection appConnection) {
		BaseTest.appConnection = appConnection;
	}

}
