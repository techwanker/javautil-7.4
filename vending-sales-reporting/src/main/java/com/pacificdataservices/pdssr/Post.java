package com.pacificdataservices.pdssr;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.MappedResultSetIterator;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.sql.SqlStatementRunner;
import org.javautil.core.sql.SqlStatements;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Populates post_sale record from qualifying etl_sale records // // Updates the
 * etl_sale.product_id based on case_gtin // // Upserts product_nomen with
 * distributor identifier for authoritative manufacturer information // """
 * 
 * @author jjs
 *
 */
public class Post {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Connection connection;

	private SqlStatements sqlStatements;
	private SqlStatements postQueries; // TODO not used??????
	private Dialect dialect;
	private int verbosity;

	public Post(Connection conn, Dialect dialect, int verbosity) throws IOException {
		this.connection = conn;
		this.dialect = dialect;
		this.verbosity = verbosity;
		loadDml();
	}

	Date getEffectiveDate(long etlFileId) throws SQLException, InvalidLoadFileException {

		Date retval = null;
		String sql = "select file_create_dt from etl_sale_tot " + "where etl_file_id = %(ETL_FILE_ID)s";
		SqlStatement ss = new SqlStatement(sql, connection);
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID", etlFileId);
		// MappedResultSetIterator it = ss.iterator(connection, dialect, binds);
		// TODO use getNameValue
		MappedResultSetIterator it = ss.iterator(binds);
		NameValue result;
		if (it.hasNext()) {
			result = it.next();
			retval = (Date) result.get("file_create_dt");
		} else {
			throw new InvalidLoadFileException("load " + etlFileId + " has no etl_sale_tot");
		}
		return retval;
	}

	public void process(long etlFileId) throws SQLException, InvalidLoadFileException {
		logger.info("processing etl_file_id " + etlFileId);
		Binds binds = new Binds();
		Date effectiveDate = getEffectiveDate(etlFileId);
		binds.put("EFF_DT", effectiveDate);
		binds.put("ETL_FILE_ID", etlFileId);

		SqlStatementRunner runner = new SqlStatementRunner(connection, dialect, sqlStatements, verbosity);
		runner.process(binds);
		connection.commit();
	}

	private void loadDml() throws IOException {
		String path = null;
		String qpath = null;
		switch (this.dialect) {
		case H2:
			path = "post_dml.h2.yaml"; // eliminated with for record counts should use
			qpath = "etl_posting_queries.h2.yaml";
			break;
		case POSTGRES:
			path = "post_dml.postgres.yaml"; // eliminated with for record counts should use
			qpath = "etl_posting_queries.postgres.yaml";
			break;
		default:
			throw new IllegalStateException("unsupported Dialect " + dialect);

		}
		InputStream is = getClass().getResourceAsStream(path);
		sqlStatements = new SqlStatements(is, connection);
		is.close();

		is = getClass().getResourceAsStream(qpath);
		postQueries = new SqlStatements(is, connection);
		is.close();

	}

	// SqlStatements getSqlStatements(String path, boolean isMap) throws IOException
	// {
	// if (path == null) {
	// throw new IllegalArgumentException("path is null");
	// }
	// SqlStatements retval = null;
	// ClassLoader classLoader = getClass().getClassLoader();
	// URL url = classLoader.getResource(path);
	// if (url == null) {
	// throw new IllegalArgumentException("unable to get url for " + path);
	// }
	// File file = new File(classLoader.getResource(path).getFile());
	// logger.info(file.getAbsolutePath());
	// try {
	// //retval = new SqlStatements(file.getAbsolutePath(), connection, isMap);
	// retval = new SqlStatements(file.getAbsolutePath(), connection);
	// } catch (FileNotFoundException e) {
	// throw new RuntimeException(e);
	// }
	// return retval;
	// }

}
