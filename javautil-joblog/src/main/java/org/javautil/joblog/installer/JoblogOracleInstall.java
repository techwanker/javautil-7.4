package org.javautil.joblog.installer;



import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoblogOracleInstall {

	private final Connection connection;

	private boolean drop = true;
	private final  transient static Logger logger = LoggerFactory.getLogger(JoblogOracleInstall.class);
	private boolean showSql = true;

	private boolean dryRun;

	private String sqlOutputFilename;

	private boolean showErrorOnDrop = false;

	public JoblogOracleInstall(Connection connection, boolean drop, boolean showSql) {
		this.connection = connection;
		this.drop = drop;
		this.showSql = showSql;
	}

	public JoblogOracleInstall(String outputFileName) {
		this.connection = null;
		this.showSql = true;
		this.dryRun = true;
		this.sqlOutputFilename = outputFileName;
	}

	public void process() throws Exception, SqlSplitterException {
		logger.info("process begins===================================");
		if (drop) {
			drop();
		}
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

	public void drop() throws SqlSplitterException, SQLException, IOException {
		logger.info("dropping tables");
		new SqlRunner(this, "ddl/oracle/dblogger_uninstall.sr.sql").setConnection(connection).setShowSql(showSql)
		.setContinueOnError(true).setShowError(showErrorOnDrop).process();
	}

	public void createTables() throws SqlSplitterException, SQLException, IOException {
		//	final String cursorTables = "ddl/oracle/cursor_tables.sr.sql";
		final String jobTables = "ddl/oracle/job_tables.sr.sql";

		logger.info("loggerObjectInstall showSql: {}", showSql);

		//		new SqlRunner(this, cursorTables.setConnection(connection).setContinueOnError(true).setShowSql(showSql)
		//				.process();

		new SqlRunner(this, jobTables).setConnection(connection).setContinueOnError(true).setShowSql(showSql)
		.process();

	}

	public void loggerObjectInstall() throws SqlSplitterException, SQLException, IOException {

		final String loggerSpec = "ddl/oracle/logger.pks.sr.sql";
		final String loggerBody = "ddl/oracle/logger.pkb.sr.sql";
		final String joblogSpec = "ddl/oracle/joblog.pks.sr.sql";
		final String joblogBody = "ddl/oracle/joblog.pkb.sr.sql";

		logger.info("loggerObjectInstall showSql: {}", showSql);

		createTables();

		//		logger.info("======= creating logger_message_formatter");
		//		new SqlRunner(this, "ddl/oracle/logger_message_formatter.plsql.sr.sql").setConnection(connection)
		//				.setShowSql(showSql).setProceduresOnly(true).setContinueOnError(true).process();

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
		//		String sql = "select object_type, status from user_objects\n" + 
		//				"where object_name = 'LOGGER'";
		//
		//		SqlStatement ss = new SqlStatement(connection,sql);
		//		ListOfNameValue lonv = ss.getListOfNameValue();
		//		for (NameValue nv : lonv) {
		//		assertEquals("VALID",nv.get("status"));
		//		}
		//	ss.close();
	}

	public JoblogOracleInstall setDrop(boolean drop) {
		this.drop = drop;
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
