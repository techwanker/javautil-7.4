package org.javautil.oracle.dao;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.sql.CLOB;

public class UtXmlS implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(UtXmlS.class.getName());
	// int insertBatchSize = 100;
	// private OracleConnection conn;
	static String sequenceText = "select ut_surrogate_nbr.nextval from dual";
	/**
	 * sql text for inserting all rows into the table
	 */
	// static String insertText = "insert into UT_clob " + "(\n" +
	// "ut_clob_nbr,\n" + " text\n" + ") values(?,?)\n";
	static String insertText = "insert into Ut_xml " + "(\n" + "ut_xml_nbr,\n" + "		xml\n"
			+ ") values(?,xmltype(?))\n";
	private Connection conn;
	private PipedInputStream xmlReader;
	private Exception exception;
	private Boolean done = false;

	// public static void insert(Connection conn, Document doc) throws
	// java.sql.SQLException {
	// /**
	// * Insert all tuples into persistent store.
	// *
	// * @throws IOException
	// */
	// }

	/**
	 * Insert all tuples into persistent store.
	 * 
	 * @throws IOException
	 */
	public static void insert(Connection conn, File file) throws java.sql.SQLException, IOException {
		PreparedStatement sequenceStmt = conn.prepareStatement(sequenceText);
		PreparedStatement insertStmt = conn.prepareStatement(insertText);
		ResultSet rset = sequenceStmt.executeQuery();
		rset.next();
		int pk = rset.getInt(1);
		sequenceStmt.close();
		// if (insertStmt == null && conn == null) {
		// throw new IllegalStateException("conn is null");
		// }
		if (insertStmt == null) {
			insertStmt = conn.prepareStatement(insertText);
		}
		FileReader fr = new FileReader(file);
		int charsRead = 0;
		char[] buff = new char[1024];
		insertStmt.setObject(1, pk);
		oracle.sql.CLOB clob = null;
		clob = oracle.sql.CLOB.createTemporary(conn, false, oracle.sql.CLOB.DURATION_SESSION);
		clob.open(CLOB.MODE_READWRITE);
		Writer clobWriter = clob.setCharacterStream(0L);
		while ((charsRead = fr.read(buff)) > -1) {
			clobWriter.write(buff, 0, charsRead);
		}
		clobWriter.close();
		// insertStmt.setObject(bindNbr++, row.getLogMsgTs());
		insertStmt.setObject(2, clob);
		insertStmt.executeUpdate();

		clob.freeTemporary();

		insertStmt.close();
	}

	public static int insert(Connection conn, String xml) throws java.sql.SQLException, IOException {
		PreparedStatement sequenceStmt = conn.prepareStatement(sequenceText);
		PreparedStatement insertStmt = conn.prepareStatement(insertText);
		ResultSet rset = sequenceStmt.executeQuery();
		rset.next();
		int pk = rset.getInt(1);
		sequenceStmt.close();
		// if (insertStmt == null && conn == null) {
		// throw new IllegalStateException("conn is null");
		// }
		if (insertStmt == null) {
			insertStmt = conn.prepareStatement(insertText);
		}
		StringReader sr = new StringReader(xml);
		int charsRead = 0;
		char[] buff = new char[1024];
		insertStmt.setObject(1, pk);
		oracle.sql.CLOB clob = null;
		clob = oracle.sql.CLOB.createTemporary(conn, false, oracle.sql.CLOB.DURATION_SESSION);
		clob.open(CLOB.MODE_READWRITE);
		Writer clobWriter = clob.setCharacterStream(0L);
		while ((charsRead = sr.read(buff)) > -1) {
			clobWriter.write(buff, 0, charsRead);
		}
		clobWriter.close();
		// insertStmt.setObject(bindNbr++, row.getLogMsgTs());
		insertStmt.setObject(2, clob);
		insertStmt.executeUpdate();

		clob.freeTemporary();

		insertStmt.close();
		return pk;
	}

	/**
	 * TODO RESTORE
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	// public static void main(String[] args) throws SQLException, IOException {
	// String fileName = args[0];
	// Connection conn = ConnectionHelper.getConnection(false);
	// UtXmlS.insert(conn, fileName);
	// conn.commit();
	// conn.close();
	// }

	public UtXmlS() {
	}

	public UtXmlS(Connection conn, PipedInputStream xml) {
		this.conn = conn;
		this.xmlReader = xml;
	}

	public UtXmlS(Connection conn, PipedOutputStream xml) throws IOException {
		this.conn = conn;
		this.xmlReader = new PipedInputStream(xml);
	}

	public Exception getException() {
		return exception;
	}

	public synchronized boolean isDone() {
		logger.info("returning " + done);
		return done;
	}

	/**
	 * Insert all tuples into persistent store.
	 * 
	 * @throws IOException
	 */
	@Override
	public void run() {
		try {
			synchronized (done) {
				PreparedStatement insertStmt = conn.prepareStatement(insertText);
				int pk;
				try {
					PreparedStatement sequenceStmt = conn.prepareStatement(sequenceText);
					ResultSet rset = sequenceStmt.executeQuery();
					rset.next();
					pk = rset.getInt(1);
					sequenceStmt.close();
				} catch (SQLException se) {
					String msg = "while processing " + sequenceText + " " + se.getMessage();
					se.printStackTrace();
					logger.error(msg);
					throw se;
				}
				// if (insertStmt == null && conn == null) {
				// throw new IllegalStateException("conn is null");
				// }
				if (insertStmt == null) {
					insertStmt = conn.prepareStatement(insertText);
				}
				int charsRead = 0;
				byte[] buff = new byte[1024];
				insertStmt.setObject(1, pk);
				oracle.sql.CLOB clob = null;
				clob = oracle.sql.CLOB.createTemporary(conn, false, oracle.sql.CLOB.DURATION_SESSION);
				clob.open(CLOB.MODE_READWRITE);
				OutputStream os = clob.setAsciiStream(0L);
				Writer clobWriter = clob.setCharacterStream(0L);
				while ((charsRead = xmlReader.read(buff)) > -1) {
					os.write(buff, 0, charsRead);
					// System.out.write(buff, 0, charsRead);
				}
				os.flush();
				// insertStmt.setObject(bindNbr++, row.getLogMsgTs());
				insertStmt.setObject(2, clob);
				insertStmt.executeUpdate();
				clobWriter.close();

				clob.freeTemporary();

				insertStmt.close();
				logger.info("setting done to true");
				done = true;
			}
		} catch (Exception e) {
			this.done = true;
			this.exception = e;
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
