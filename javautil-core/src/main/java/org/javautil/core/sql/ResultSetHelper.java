package org.javautil.core.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetHelper {
	private static transient final Logger logger = LoggerFactory.getLogger(ResultSetHelper.class
			);

	private ResultSetMetaDataCache resultSetMetaDataCache;

	/**
	 * @param rset        the ResultSet
	 * @param toLowerCase column names will be lowercase
	 * @return column names
	 * @throws SQLException If you did bad things
	 */

	public static String[] getColumnNames(ResultSet rset, boolean toLowerCase) throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final String[] columnNames = new String[metaData.getColumnCount()];
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			final String columnName = toLowerCase ? metaData.getColumnName(i).toLowerCase() : metaData.getColumnName(i);
			columnNames[i - 1] = columnName;
		}
		return columnNames;
	}

//	/**
//	 * @param rset      the ResultSet
//	 * @param lowerCase names will be lowercased,
//	 * @return NameValue
//	 * @throws SQLException if you did bad things
//	 */
//	public static NameValue getNameValue(ResultSet rset, boolean lowerCase) throws SQLException {
//		NameValue nv;
//		boolean tooManyRows = false;
//		try {
//			nv = getRowAsNameValue(rset, lowerCase);
//		} catch (final SQLException sqe) {
//			rset.close();
//			logger.error(sqe.getMessage());
//			throw new SQLException("could not get row? " + sqe.getMessage(), sqe);
//		} 
//		try {
//			rset.next();
//			tooManyRows = true;
//		} 
//		catch (SQLException sqe) {
//			logger.debug("failed the intended result{}",sqe.getMessage());
//		} 
//		
//		rset.close();
//
//
//		if (tooManyRows) {
//			throw new TooManyRowsException();
//		}
//		return nv;
//	}
	/**
	 * @param rset      the ResultSet
	 * @param lowerCase names will be lowercased,
	 * @return NameValue
	 * @throws SQLException if you did bad things
	 */
	public static NameValue getNameValue(ResultSet rset, boolean lowerCase) throws SQLException {
		NameValue nv;
		ListOfNameValue list = getListOfNameValue(rset,lowerCase);
		if (list.size() == 0) {
			throw new DataNotFoundException("no data");
		}
		if (list.size() > 1) {
			throw new TooManyRowsException();
		}
		return list.get(0);
	}

	public static NameValue getRowAsNameValue(ResultSet rset, boolean lowerCase) throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final String[] columnNames = getColumnNames(rset, lowerCase);
		final NameValue row = new NameValue();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			row.put(columnNames[i - 1], rset.getObject(i));
		}
		return row;
	}

	public static NameValue getRowAsNameValue(ResultSet rset) throws SQLException {
		return getRowAsNameValue(rset, false);
	}

	public static ArrayList<Object> getRowAsList(ResultSet rset) throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final ArrayList<Object> row = new ArrayList<>(metaData.getColumnCount());
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			row.add(rset.getObject(i));
		}
		return row;
	}

	public static ListOfNameValue getListOfNameValue(ResultSet rset, boolean toLowerCase) throws SQLException {
		final ListOfNameValue list = new ListOfNameValue();
		while (rset.next()) {
			list.add(getRowAsNameValue(rset, toLowerCase));
		}
		rset.close();
		return list;
	}

	/**
	 * Convenience method for getListOfNameValue(rset, true) lower case names
	 * 
	 * @param rset the ResultSet
	 * @return ListOfNameValue
	 * @throws SQLException if you did bad things
	 */
	public static ListOfNameValue getListOfNameValue(ResultSet rset) throws SQLException {
		return getListOfNameValue(rset, true);
	}

	public static ListOfLists toListOfLists(ResultSet rset) throws SQLException {
		final ListOfLists rows = new ListOfLists();
		while (rset.next()) {
			rows.add(getRowAsList(rset));
		}
		rset.close();
		rows.trimToSize();
		return rows;
	}

}
