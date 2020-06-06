package org.javautil.oracle;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ConnectionHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.io.FileUtil;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.OracleConnection;

/**
 * References
 * https://docs.oracle.com/cd/B28359_01/server.111/b28320/dynviews_3027.htm#REFRN30232
 *
 * @author jjs
 *         https://docs.oracle.com/cd/B28359_01/server.111/b28310/diag006.htm#ADMIN12484
 */
public class OracleConnectionHelper extends ConnectionHelper {

	public static final String  startTraceCall = "begin dbms_session.set_sql_trace(true); end;";
	private final static Logger logger         = LoggerFactory.getLogger(OracleConnectionHelper.class);

	public static boolean isOracleConnection(Connection connection) {
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		try {
			return connection.isWrapperFor(OracleConnection.class);
		} catch (final SQLException e) {
			logger.error("Error when checking isOracleConnection: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static void purgeRdbmsPipe(Connection conn, String pipeName) throws SQLException {
		final String text = String.format("begin sys.dbms_pipe.purge('%s'); end;", pipeName);
		logger.info(String.format("text: %s", text));
		final CallableStatement purgeStatement = conn.prepareCall(text);
		purgeStatement.execute();
		purgeStatement.close();
	}

	public static void testConnection(Connection connection) throws SQLException {
		final Statement s = connection.createStatement();
		s.close();
	}

	public static String getPLSQLErrors(Connection connection) throws SQLException {
		String retval = null;

		final String sql = "	select name, type, sequence, line, position, text, attribute, message_number\n"
		    + "		from user_errors\n" + "		order by name, sequence";
		final SqlStatement errorStatement = new SqlStatement(connection, sql);
		final ListOfNameValue lonv = errorStatement.getListOfNameValue(new Binds(), true);
		final StringBuilder sb = new StringBuilder();
		for (final NameValue nv : lonv) {
			final String line = String.format("%s %d, %s\n", nv.getString("name"), nv.getLong("line"), nv.getString("text"));
			sb.append(line);
		}
		if (sb.length() > 0) {
			retval = sb.toString();
		}
		return retval;

	}

	public static String getDuplicateCursors(Connection connection) throws SQLException {
		final SqlStatement dupeCursors = new SqlStatement(connection, "select * from my_duplicate_cursor");
		final StringBuilder sb = new StringBuilder();
		final ListOfNameValue dupes = dupeCursors.getListOfNameValue(new Binds());
		for (final NameValue nv : dupes) {
			sb.append(String.format("count: %d sqlText: %s\n", nv.getLong("sql_count"), nv.getString("sql_text")));
		}
		dupeCursors.close();
		return sb.toString();
	}

	public static String getCursors(Connection connection) throws SQLException {
		final String sql = "select count(*) sql_count, sql_text from my_open_cursor group by sql_text";
		final SqlStatement cursors = new SqlStatement(connection, sql);
		final StringBuilder sb = new StringBuilder();
		final ListOfNameValue cursorsRows = cursors.getListOfNameValue(new Binds());
		for (final NameValue nv : cursorsRows) {
			((BigDecimal) nv.get("sql_count")).longValue();
			sb.append(String.format("count: %d sqlText: %s\n", nv.getLong("sql_count"), nv.get("sql_text")));
		}
		cursors.close();
		return sb.toString();
	}

	public void commitNoWait(Connection connection) {
		final String commitBatchNoWaitText = "commit work write batch nowait";
		try {

			final PreparedStatement ps = connection.prepareStatement(commitBatchNoWaitText);
			ps.executeUpdate();
			ps.close();

		} catch (final SQLException e) {
			logger.error("Cannot commit because: {}", e);
			throw new RuntimeException(e);
		}
	}

	public static String getMyTraceFile(Connection conn) throws SQLException, IOException {
		String myTraceFileName = getMyTraceFileName(conn);
		return getTextFromRdbmsTraceFile(conn, myTraceFileName);
	}

	public static String getMyTraceFileName(Connection conn) throws SQLException {
		String filesSql = "SELECT VALUE trace_filename FROM V$DIAG_INFO WHERE NAME = 'Default Trace File'";
		SqlStatement ss = new SqlStatement(conn, filesSql);
		ListOfNameValue lonv = ss.getListOfNameValue(new Binds(), true);
		String traceFileName = lonv.get(0).getString("trace_filename");
		return traceFileName;
	}

	public static String getTextFromRdbmsTraceFile(Connection connection, String fileName)
	    throws SQLException, IOException {
		return getTextFromRdbmsFile(connection, "UDUMP_DIR", fileName);
	}

	public static String getTextFromRdbmsFile(Connection connection, String directoryName, String fileName)
	    throws SQLException, IOException {
		String requestFileName = fileName;
		final String getTextFileSql = "begin :text := read_file(p_dir_name => :directory_name, p_file_name => :file_name); end;";

		logger.info("getTextFromRdbmsFile: directory: '{}' fileName '{}", directoryName, fileName);

		//
		String filenameBase = FileUtil.basename(fileName);
		if (!filenameBase.equals(fileName)) {
			requestFileName = filenameBase;
			logger.warn("requested file {} changed to {}", fileName, requestFileName);
		}

		logger.info("getTextFromRdbmsFile: directory: '{}' fileName '{}' requestFileName '{}'", directoryName, fileName,
		    requestFileName);

		CallableStatement getTextStatement = connection.prepareCall(getTextFileSql);
		getTextStatement.registerOutParameter("text", java.sql.Types.CLOB);
		getTextStatement.setString("directory_name", directoryName);
		getTextStatement.setString("file_name", requestFileName);

		try {
			getTextStatement.execute();
			final Clob text = getTextStatement.getClob("text");
			return readClob(text);
		} catch (SQLException sqe) {
			String directorySql = "select * from all_directories where directory_name = :directory_name";
			SqlStatement directorySS = new SqlStatement(connection, directorySql);
			Binds b = new Binds();
			b.put("directory_name", directoryName);
			ListOfNameValue nv = directorySS.getListOfNameValue(b);
			String message = String.format("Error while trying to read '%s' from directory '%s'\ndirectories %s\n%d %s",
			    fileName, directoryName, nv, sqe.getErrorCode(), sqe.getMessage());
			logger.error(message);
			throw new SQLException(message, sqe.getCause());
		}
	}

	public void kill(Connection connection, int sid, int serialNbr) throws SQLException {
		final String killSQL = String.format("alter system kill session(%d,%d)", sid, serialNbr);
		final Statement killStatement = connection.createStatement();
		killStatement.execute(killSQL);
		killStatement.close();
	}

//	/**
//	 * https://techgoeasy.com/how-to-turn-on-trace-in-database-and/
//	 * https://docs.oracle.com/cd/A57673_01/DOC/net/doc/NWTR23/ch3trc.htm#settrc
//	 * https://docs.oracle.com/cd/A57673_01/DOC/net/doc/NWTR23/ch3trc.htm#settrc
//	 * https://asktom.oracle.com/pls/asktom/asktom.search?tag=trace-files-location
//	 * https://docs.oracle.com/en/database/oracle/oracle-database/12.2/refrn/V-DIAG_TRACE_FILE_CONTENTS.html#GUID-D5750193-4789-4D39-B57C-250A38961605
//	 * grant select on sys.v_$diag_sess_opt_trace_records to public
//
//	 *
//	 * Trace files 
//	 * @param conn the connection to be traced
//	 * @param waits trace waits
//	 * @param binds trace binds
//	 * @throws SQLException
//	 */
//	public static void enableTrace(Connection conn, boolean waits, boolean binds) throws SQLException {
//		// TODO use the parms
//		String sql = "begin sys.trace_my_session(p_bind => true, p_wait => true); end;" ;
//		Statement ss = conn.createStatement();
//		ss.execute(sql);
//	}

	public static void enableTrace(Connection conn, boolean emitWaits, boolean emitBinds) throws SQLException {
		// TODO use the parms
		int myEmitBinds = emitBinds ? 1 : 0;
		int myEmitWaits = emitWaits ? 1 : 0;
		String sql = "begin sys.trace_my_session_jdbc(p_bind => :show_binds, p_wait => :show_wait); end;";
		SqlStatement ss = new SqlStatement(conn, sql);
		Binds binds = new Binds();
		binds.put("show_binds", myEmitBinds);
		binds.put("show_wait", myEmitWaits);
		ss.execute(binds);
	}
	

	public static String getDBMSDirectoryForTraceFiles(Connection conn) throws SQLException {
		String filesystemDir = getFileSystemDirectoryForTraceFiles(conn);
		String sql = "select * from all_directories where directory_path = :path";
		SqlStatement ss = new SqlStatement(conn,sql);
		Binds binds = new Binds();
		binds.put("path",filesystemDir);
		ListOfNameValue  lon = ss.getListOfNameValue();
		ss.close();
		switch (lon.size())  {
		case 0:
			throw new IllegalArgumentException("No dbms directory for filesystem dir " + filesystemDir);
		case 1:
			return lon.get(0).getString("directory_name");
		default:
			String retval =  lon.get(0).getString("directory_name");
			logger.warn("multiple directories {} returning {}", lon, retval);
			return retval;
		}
	}
	
	public static String getFileSystemDirectoryForTraceFiles(Connection conn) throws SQLException {
		String fileName = getCurrentTraceFileName(conn);
		File   file = new File(fileName);
		File   directory = file.getParentFile();
		String directoryName = directory.getPath();
		return directoryName;
	}
	public static String getCurrentTraceFileName(Connection conn) throws SQLException {
		String sql = "SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File'";
		SqlStatement ss = new SqlStatement(conn,sql);
		NameValue nv = ss.getNameValue();
		ss.close();
		return (String) nv.get("value");
	}
}