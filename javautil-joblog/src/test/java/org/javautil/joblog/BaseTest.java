package org.javautil.joblog;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.joblog.installer.JoblogOracleInstall;
import org.javautil.joblog.persistence.AbstractJoblogPersistence;
import org.javautil.joblog.persistence.JoblogPersistence;
import org.javautil.joblog.persistence.JoblogPersistencePackage;
import org.javautil.joblog.persistence.JoblogPersistenceSql;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//"integration_oracle":
//    driver_class: "oracle.jdbc.driver.OracleDriver"
//    url: "jdbc:oracle:thin:@localhost:1521/sales_reporting"
//    username: "sr"
//    password: "tutorial"

public class BaseTest {

	static DataSource applicationDataSource;
	static Connection applicationConnection;

	static DataSource loggerDataSource;
	static Connection loggerConnection;
	static JoblogPersistence joblogPersistence;
	private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
	static JoblogPersistence oraclePackagePersistence;
	boolean showSql = false;

	@BeforeClass
	public static void beforeClass() throws SqlSplitterException, Exception {
		DataSourceFactory dsf = new DataSourceFactory();

		applicationDataSource = dsf.getDatasource("integration_oracle");
		applicationConnection = applicationDataSource.getConnection();
		loggerDataSource = dsf.getDatasource("integration_oracle");
		loggerConnection = loggerDataSource.getConnection();
		joblogPersistence = new JoblogPersistenceSql(loggerConnection, applicationConnection);

		new JoblogOracleInstall(applicationConnection, true, false).process();
		new JoblogOracleInstall(loggerConnection, true, false).process();
		oraclePackagePersistence = new JoblogPersistencePackage(applicationConnection);

	}

	@AfterClass
	public static void afterClass() throws IOException {
		logger.warn("\n***********\nclosing applicationDatasource\n*************");

		((Closeable) applicationDataSource).close();
	}

	public static DataSource getApplicationDataSource() {
		return applicationDataSource;
	}

	public static Connection getApplicationConnection() {
		return applicationConnection;
	}

	public static DataSource getLoggerDataSource() {
		return loggerDataSource;
	}

	public static Connection getLoggerConnection() {
		return loggerConnection;
	}

	public static JoblogPersistence getDblogger() {
		return joblogPersistence;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

//	public static JoblogPersistence getOraclePackagePersistence() {
//		return oraclePackagePersistence;
//	}
}
