package org.javautil.cli;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import javax.sql.DataSource;

import org.javautil.core.csv.SqlCsvExporter;
import org.javautil.core.sql.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlCsvExporterCLI {

	static final Logger logger = LoggerFactory.getLogger(SqlCsvExporterCLI.class);

	void process(SqlCsvExporterArguments args) throws SQLException, IOException, ParseException {
		String[] upw = args.getUserNamePassword().split("/");
		String username = upw[0];
		logger.info("using username: '{}' url '{}'",username,args.getJDBCURL());
		
		DataSource ds = DataSourceFactory.getDataSource(args.getJDBCURL(), upw[0], upw[1]);
		
		Connection conn = ds.getConnection();
		SqlCsvExporter exporter = new SqlCsvExporter(conn, args.getSelectStatement());
		exporter.write(args.getOutFile());
	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {
		for (String arg :args) { 
		   logger.info("arg " + arg);
		}
		SqlCsvExporterCLI invocation = new SqlCsvExporterCLI();
		SqlCsvExporterArguments arguments = new SqlCsvExporterArguments();
		arguments.parseArguments(args);
		invocation.process(arguments);
	}

	
}
