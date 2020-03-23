package org.javautil.oracle.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.javautil.oracle.dto.UtXsl;

import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

public class UtXslS {
	private static int maxScriptSize = 1024 * 1024 * 64;

	static final String selectText = "" + "SELECT\n" +

			"    ut_xsl_nbr,\n" + "     file_name,\n" + "      stylesheet \n" + "FROM UT_xsl \n";

	public static UtXsl getForUtXslNbr(Connection conn, long utXslNbr) throws java.sql.SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement(selectText + " where ut_Xsl_Nbr = :ut_xsl_nbr");
		stmt.setLong(1, utXslNbr);
		OracleResultSet rset = (OracleResultSet) stmt.executeQuery();
		if (!rset.next()) {
			throw new IllegalArgumentException("no such " + utXslNbr);
		}
		UtXsl returnValue = new UtXsl();
		String columnName = null;
		try {
			returnValue.setUtXslNbr(utXslNbr);
			CLOB stylesheetClob = (rset).getCLOB("stylesheet");
			stylesheetClob.open(CLOB.MODE_READONLY);
			InputStream scriptStream = stylesheetClob.getAsciiStream();
			StringBuilder scriptBuffer = new StringBuilder();
			int c;
			while ((c = scriptStream.read()) != -1) {
				char ch = (char) c;
				scriptBuffer.append(ch);
			}
			String script = scriptBuffer.toString();

			stylesheetClob.close();
			long length = stylesheetClob.getLength();
			if (length > maxScriptSize) {
				throw new SQLException("styleSheetClob exceeds " + length);
			}

			returnValue.setStylesheet(script);

			stmt.close();
			return returnValue;
		} catch (java.sql.SQLException s) {
			stmt.close();
			throw new java.sql.SQLException("error processing column" + columnName + "\n" + s.getMessage());
		}
	} // end of getRow

	String getSelectText() {
		return selectText;
	}

} // end of class
