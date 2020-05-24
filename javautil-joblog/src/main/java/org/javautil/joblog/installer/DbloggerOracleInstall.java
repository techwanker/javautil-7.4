package org.javautil.joblog.installer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.core.sql.SqlStatement;
import org.javautil.util.ListOfLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbloggerOracleInstall {

	private final Connection connection;

	private boolean drop = true;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean showSql = false;

	private boolean dryRun;

	private String sqlOutputFilename;

	private boolean showErrorOnDrop = false;

	public DbloggerOracleInstall(Connection connection, boolean drop, boolean showSql) {
		this.connection = connection;
		this.drop = drop;
		this.showSql = showSql;
	}

	public DbloggerOracleInstall(String outputFileName) {
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

		logger.info("loggerObjectInstall showSql: {}", showSql);

		createTables();

//		logger.info("======= creating logger_message_formatter");
//		new SqlRunner(this, "ddl/oracle/logger_message_formatter.plsql.sr.sql").setConnection(connection)
//				.setShowSql(showSql).setProceduresOnly(true).setContinueOnError(true).process();

		logger.info("======= about to compile specs " + loggerSpec);
		new SqlRunner(this, loggerSpec).setConnection(connection).setShowSql(showSql).setProceduresOnly(true)
				.setContinueOnError(true).process();

		logger.info("======== creating logger package body " + loggerBody);
		new SqlRunner(this, loggerBody).setConnection(connection).setShowSql(showSql).setProceduresOnly(true)
				.setContinueOnError(true).process();

	}

	public DbloggerOracleInstall setDrop(boolean drop) {
		this.drop = drop;
		return this;
	}

	public static void main(String[] args) throws Exception, SqlSplitterException {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource apds = dsf.getDatasource("integration");
		final Connection conn = apds.getConnection();
		new DbloggerOracleInstall(conn, true, true).process();

	}

}
