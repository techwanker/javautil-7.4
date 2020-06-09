package org.javautil.joblog.installer;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlParmRunner;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlRunnerParms;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogOracleInstall {

	private final Connection connection;

	private final  transient static Logger logger = LoggerFactory.getLogger(JoblogOracleInstall.class);
	private boolean showSql = true;

	public JoblogOracleInstall(Connection connection, boolean drop, boolean showSql) {
		this.connection = connection;
		this.showSql = showSql;
	}

	public JoblogOracleInstall(String outputFileName) {
		this.connection = null;
		this.showSql = true;
	}

	public void process() throws Exception, SqlSplitterException {
		logger.info("process begins===================================");
		logger.info("existing tables ==================");
		SqlStatement ss = new SqlStatement("select table_name from user_tables order by table_name");
		ss.setConnection(connection);
		ListOfLists lols = ss.getListOfLists(new Binds());
		StringBuilder sb = new StringBuilder();
		for (ArrayList<? extends Object> tablename : lols) {
			sb.append(tablename.get(0));
			sb.append("\n");
		}

		logger.info("tables: {}", sb.toString());
		ss.close();

		logger.info("creating tables showSql ");
		loggerObjectInstall();

	}
	
	void testResourceStream(Connection conn, String resourceName) throws IOException {
		InputStream is = conn.getClass().getResourceAsStream(resourceName);
		logger.warn("is is {} for {}", is, resourceName);
		if (is == null) {
			throw new IllegalStateException("unable to open " + resourceName);
		}
		is.close();
	}

	void createTheTables() throws SQLException, IOException {
			ArrayList<SqlRunnerParms> parmList = new ArrayList<>();
			
			//String loggerTablesName = "src/main/resources/ddl/oracle/logger_tables.sr.sql";
			String loggerTablesResourceName = "/ddl/oracle/logger_tables.sr.sql";
			testResourceStream(connection, loggerTablesResourceName);
			SqlRunnerParms loggerTablesParms = new SqlRunnerParms(connection, connection, loggerTablesResourceName, 3);
			loggerTablesParms.setContinueOnError(true);
			loggerTablesParms.setShowSql(true);
			parmList.add(loggerTablesParms);
			
			
			String dropName = "/ddl/oracle/joblog_drop.sr.sql";
			//InputStream dropIs = new FileInputStream(dropName);
			SqlRunnerParms dropParms = new SqlRunnerParms(connection, connection, dropName, 3);
			dropParms.setContinueOnError(true);
			dropParms.setShowSql(true);
			parmList.add(dropParms);
			
			
			String isname = "/ddl/oracle/job_tables.sr.sql";
			//InputStream is = new FileInputStream(isname);
			SqlRunnerParms installParms = new SqlRunnerParms(connection, connection, isname, 3);
			installParms.setShowSql(true);
			parmList.add(installParms);
			
			SqlParmRunner spr = new SqlParmRunner(parmList, new Binds());
			
			spr.process();
	}
			
//	void createTheTables() throws FileNotFoundException, SQLException {
//			ArrayList<SqlRunnerParms> parmList = new ArrayList<>();
//			
//			String loggerTablesName = "src/main/resources/ddl/oracle/logger_tables.sr.sql";
//			String loggerTablesResourceName = "ddl/oracle/logger_tables.sr.sql";
//			File loggerTablesFile = ResourceHelper.getResourceAsFile(this, loggerTablesResourceName);
//			InputStream loggerTablesIs = new FileInputStream(loggerTablesFile);
//	//  TODO make such a constructor		
//			SqlRunnerParms loggerTablesParms = new SqlRunnerParms(connection, loggerTablesIs, loggerTablesName, 3);
//			loggerTablesParms.setContinueOnError(true);
//			loggerTablesParms.setShowSql(true);
//			parmList.add(loggerTablesParms);
//			
//			
//			String dropName = "src/main/resources/ddl/oracle/joblog_drop.sr.sql";
//			InputStream dropIs = new FileInputStream(dropName);
//			SqlRunnerParms dropParms = new SqlRunnerParms(connection, dropIs, dropName, 3);
//			dropParms.setContinueOnError(true);
//			dropParms.setShowSql(true);
//			parmList.add(dropParms);
//			
//			
//			String isname = "src/main/resources/ddl/oracle/job_tables.sr.sql";
//			InputStream is = new FileInputStream(isname);
//			SqlRunnerParms installParms = new SqlRunnerParms(connection, is, isname, 3);
//			installParms.setShowSql(true);
//			parmList.add(installParms);
//			
//			SqlParmRunner spr = new SqlParmRunner(parmList, new Binds());
//			
//			spr.process();
//	}

	/*
	 * public void drop() throws SqlSplitterException, SQLException, IOException {
	 * logger.info("dropping tables"); new SqlRunner(this,
	 * "ddl/oracle/dblogger_uninstall.sr.sql").setConnection(connection).setShowSql(
	 * showSql) .setContinueOnError(true).setShowError(showErrorOnDrop).process(); }
	 * 
	 * public void createTables() throws SqlSplitterException, SQLException,
	 * IOException { // final String cursorTables =
	 * "ddl/oracle/cursor_tables.sr.sql"; final String jobTables =
	 * "ddl/oracle/job_tables.sr.sql";
	 * 
	 * logger.info("loggerObjectInstall showSql: {}", showSql);
	 * 
	 * // new SqlRunner(this,
	 * cursorTables.setConnection(connection).setContinueOnError(true).setShowSql(
	 * showSql) // .process();
	 * 
	 * new SqlRunner(this,
	 * jobTables).setConnection(connection).setContinueOnError(true).setShowSql(
	 * showSql) .process();
	 * 
	 * }
	 */
	public void loggerObjectInstall() throws SqlSplitterException, SQLException, IOException {

		final String loggerSpec = "ddl/oracle/logger.pks.sr.sql";
		final String loggerBody = "ddl/oracle/logger.pkb.sr.sql";
		final String joblogSpec = "ddl/oracle/joblog.pks.sr.sql";
		final String joblogBody = "ddl/oracle/joblog.pkb.sr.sql";

		logger.info("loggerObjectInstall showSql: {}", showSql);

		createTheTables();

		logger.info("======= about to compile specs " + loggerSpec);
		SqlRunner runner = new SqlRunner(this, loggerSpec).setConnection(connection).
				setShowSql(showSql)
				.setContinueOnError(true);
		runner.setSqlSplitterTrace(3);
		runner.process();

		logger.info("======== creating logger package body " + loggerBody);
		new SqlRunner(this, loggerBody).setConnection(connection).setShowSql(showSql).
		setContinueOnError(true).process();

		runner = new SqlRunner(this, joblogSpec).setConnection(connection).
				setShowSql(showSql)
				.setContinueOnError(true);
		runner.setSqlSplitterTrace(3);
		runner.process();

		new SqlRunner(this, joblogBody).setConnection(connection).setShowSql(showSql).
		setContinueOnError(true).process();
		
		ensureLoggerPackage(connection);

	}

	public JoblogOracleInstall setDrop(boolean drop) {
		return this;
	}


	public static void ensureLoggerPackage(Connection connection) throws SQLException {
		String sql = "select object_name, object_type, status from user_objects\n" + 
				"where object_name in ('LOGGER', 'JOBLOG')";

		SqlStatement ss = new SqlStatement(connection,sql);
		ListOfNameValue lonv = ss.getListOfNameValue();
		logger.debug("lnnv {}",lonv);
		for (NameValue nv : lonv) {
			assert("VALID".equals(nv.get("status")));
		}
		ss.close();	
	}

	public static void main(String[] args) throws Exception, SqlSplitterException {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource apds = dsf.getDatasource("integration");
		final Connection conn = apds.getConnection();
		new JoblogOracleInstall(conn, true, true).process();

	}

}
