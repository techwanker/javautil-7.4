package com.pacificdataservices.pdssr;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.javautil.conditionidentification.CreateUtConditionDatabaseObjects;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlSplitterException;
import org.javautil.joblog.installer.JoblogInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSchema {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * Loads a CDS format file into ETL tables
	 * 
	 */

	private Connection connection = null;
	private Dialect dialect = null;
	InputStream salesReportingDdl;
	// InputStream conditionDdl;
	private boolean showSql = true;
	private JoblogInstaller joblogInstaller;

	public CreateSchema(Connection conn, Dialect dialect) {
		logger.warn("creating with dialect " + dialect);
		this.connection = conn;
		this.dialect = dialect;
		switch (dialect) {
		case H2:
			salesReportingDdl = getResource("sales_reporting_ddl.h2.sql");
			// conditionDdl= getResource("ut_condition_tables.h2.sr.sql");
			break;
		case POSTGRES:
			salesReportingDdl = getResource("sales_reporting_ddl.postgres.sr.sql");
			break;
		case ORACLE:
			salesReportingDdl = getResource("sales_reporting_ddl.oracle.sr.sql");
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect " + dialect);

		}
	}

	InputStream getResource(String resourceName) {
		URL url = this.getClass().getResource(resourceName);
		logger.info("resourceName " + resourceName + " url " + url);
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		if (is == null) {
			throw new IllegalStateException("resource " + resourceName + " failed to load");
		}
		return is;
	}

	public void process() throws SqlSplitterException, Exception {
		nukeSchema(connection);
		CreateUtConditionDatabaseObjects condi = new CreateUtConditionDatabaseObjects(connection, dialect, showSql);
		condi.setDrop(true);
		condi.process();
		SqlRunner runner;
		logger.info("about to create sales tables " + salesReportingDdl);
		runner = new SqlRunner(salesReportingDdl);
		runner.setConnection(connection).setShowError(true).setContinueOnError(false).setShowSql(showSql);

		runner.process();
		salesReportingDdl.close();
		JoblogInstaller	joblogInstaller = new JoblogInstaller(connection);
		joblogInstaller.process();
	}
	
	public void nukeSchema(Connection conn) throws SQLException {
		String dropObjects = 
				"declare\n" + 
				"	stmt varchar(255);   \n" + 
				"begin\n" + 
				"	for table_rec in (select table_name from user_tables)\n" + 
				"	loop\n" + 
				"		stmt :=  'drop table ' || table_rec.table_name || ' cascade constraints';\n" + 
				"		dbms_output.put_line(stmt);\n" + 
				"		execute immediate stmt;\n" + 
				"	end loop;\n" + 
				"\n" + 
				"	for obj_rec in (\n" + 
				"		select object_type, object_name \n" + 
				"		from user_objects\n" + 
				"		where object_type not like '%LOB'"
				+ "     and object_type != 'PACKAGE BODY')\n" + 
				"	loop\n" + 
				"		stmt :=  'drop ' || obj_rec.object_type || ' ' ||  obj_rec.object_name ;\n" + 
				"		        dbms_output.put_line(stmt);\n" + 
				"		        execute immediate stmt;\n" + 
				"	end loop;\n" + 
				"end;";
		
		if (Dialect.getDialect(conn).equals(Dialect.ORACLE)) {
			logger.info("nuking oracle schema");
			Statement stmt = conn.createStatement();
			stmt.execute(dropObjects);
			logger.info("nuked oracle schema");
		}
		
		
		
	}
}
