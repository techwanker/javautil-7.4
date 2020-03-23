package org.javautil.core.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;

public class ResultSetUtils {
	private ResultSetMetaDataCache resultSetMetaDataCache;
	private ResultSet              rset;

	/**
	 * @param rset        the ResultSet
	*/
	public ResultSetUtils(ResultSet rset) {
		this.rset = rset;
	}

	public String[] getColumnNames(boolean toLowerCase) throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final String[] columnNames = new String[metaData.getColumnCount()];
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			final String columnName = toLowerCase ? metaData.getColumnName(i).toLowerCase() : metaData.getColumnName(i);
			columnNames[i - 1] = columnName;
		}
		return columnNames;
	}

	/**
	 * @param lowerCase names will be lowercased,
	 * @return NameValue
	 * @throws SQLException if you did bad things
	 */
	public NameValue getNameValue(boolean lowerCase) throws SQLException {
		NameValue nv;
		try {
			nv = getRowAsNameValue(lowerCase);
			try {
				rset.next();
				throw new TooManyRowsException();
			} catch (SQLException sqe) {
				// It's all good, we didn't expect the next to work
			}

		} catch (final SQLException sqe) {
			rset.close();
			throw new SQLException("could not get row? " + sqe.getMessage(), sqe);
		} finally {
			rset.close();
		}
		return nv;
	}

	public NameValue getRowAsNameValue(boolean lowerCase) throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final String[] columnNames = getColumnNames(lowerCase);
		final NameValue row = new NameValue();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			row.put(columnNames[i - 1], rset.getObject(i));
		}
		return row;
	}

	public NameValue getRowAsNameValue() throws SQLException {
		return getRowAsNameValue(false);
	}

	public ArrayList<Object> getRowAsList() throws SQLException {
		final ResultSetMetaData metaData = rset.getMetaData();
		final ArrayList<Object> row = new ArrayList<>(metaData.getColumnCount());
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			row.add(rset.getObject(i));
		}
		return row;
	}

	public ListOfNameValue getListOfNameValue(ResultSet rset, boolean toLowerCase) throws SQLException {
		final ListOfNameValue list = new ListOfNameValue();
		while (rset.next()) {
			list.add(getRowAsNameValue(toLowerCase));
		}
		rset.close();
		return list;
	}

	/**
	 * Convenience method for getListOfNameValue(rset, true) lower case names
	 * 
	 * @return ListOfNameValue
	 * @throws SQLException if you did bad things
	 */
	public ListOfNameValue getListOfNameValue() throws SQLException {
		return getListOfNameValue(rset, true);
	}

	public ListOfLists getListOfLists() throws SQLException {
		final ListOfLists rows = new ListOfLists();
		while (rset.next()) {
			rows.add(getRowAsList());
		}
		rset.close();
		rows.trimToSize();
		return rows;
	}

}
