package org.javautil.core.sql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.javautil.util.StringStores;

public class BreakingResultSetHelper {

	private long                     rowNumber       = 0;

	private Logger                   logger          = Logger.getLogger(this.getClass().getName());

	private HashMap<String, String>  stringPool      = new HashMap<String, String>();

	private HashMap<Date, Date>      datePool        = new HashMap<Date, Date>();

	private ArrayList<String>        breaks          = new ArrayList<String>();

	private StringStores             stringStores    = new StringStores();

	private ResultSet                rset;

	private HashMap<String, Integer> columnNameIndex = new HashMap<String, Integer>();

	private HashMap<Integer, String> indexColumnName = new HashMap<Integer, String>();

	private int                      datesFetched    = 0;

	private int                      stringsFetched  = 0;

	private ResultSetMetaData        meta;

	private SimpleDateFormat         dateFormatter   = new SimpleDateFormat("yyyy-MM-dd");

	private Object[]                 previousRow     = null;

	private Object[]                 currentRow      = null;

	private boolean                  debug           = false;

	private HashMap<String, String>  whenColumnMap   = new HashMap<String, String>();

	private CursorBreakScripts       breakScripts;

	private String                   cursorName;

	public BreakingResultSetHelper(ResultSet rset) throws SQLException {
		if (rset == null) {
			throw new IllegalArgumentException("rset is null");
		}
		this.rset = rset;
		meta = rset.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i).toUpperCase();
			columnNameIndex.put(columnName, i);
			indexColumnName.put(i, columnName);
			stringStores.addStore(columnName);
		}
		currentRow = new Object[meta.getColumnCount()];
		previousRow = currentRow;
	}

	public void checkWhen() {

	}

	public void executeBreakScripts() {
		int breakLevel = getBreakLevel();
		String breakColumn = indexColumnName.get(breakLevel);
	}

	public ArrayList<Object[]> getAllRows(int max) throws SQLException {
		ArrayList<Object[]> rows = new ArrayList<Object[]>();
		Object[] row;
		int cnt = 0;
		while ((row = getRow()) != null) {
			rows.add(row);
			cnt++;
			if (cnt > max) {
				throw new SQLException("returns more than " + max + " rows");
			}
		}

		return rows;
	}

	public String getBreak(int breakNbr) {
		return breaks.get(breakNbr);
	}

	public int getBreakCount() {
		int retval = breaks == null ? 0 : breaks.size();
		return retval;
	}

	/**
	 * Determines if there is a break in the data.
	 * 
	 * todo throws bizarre null pointer exception if column not in select list
	 *       while doing a
	 * @return -1 if there is no break
	 */
	public int getBreakLevel() {
		int breakLevel = -1;
		if (breaks == null) {
			return breakLevel;
		}

		for (int a = 0; breakLevel == -1 && a < breaks.size(); a++) {
			String currentBreak = breaks.get(a);
			// currentBreak values are already uppercase (see setBreaks)
			if (debug) {
				StringBuilder b = new StringBuilder();
				logger.finest("columNameIndex " + columnNameIndex);
				logger.finest("currentBreak " + currentBreak);
				logger.finest("previousRow " + previousRow);
				logger.finest("currentRow " + currentRow);
				for (String columnName : columnNameIndex.keySet()) {
					logger.info("columnName " + columnName + " " + columnNameIndex.get(columnName));
				}
			}
			// if column not in select list this throws a null pointer exception
			// even though columnName index is not null and neither is
			// currentBreak
			Integer column = columnNameIndex.get(currentBreak);
			if (column == null) {
				StringBuilder b = new StringBuilder();
				for (String columnName : columnNameIndex.keySet()) {
					b.append(columnName + " ");
				}
				throw new IllegalStateException("column " + currentBreak + "is not in resultSet " + b.toString());
			}
			Object lastValue = previousRow[column - 1];
			Object currentValue = currentRow[column - 1];
			// System.out.println(lastValue + " ?= " + currentValue);
			// are both values null or otherwise equal?
			// @todo what if the previous value was a null that could be a break
			// @todo unit test this
			if ((lastValue != null && currentValue != null && !lastValue.equals(currentValue))
			    || (lastValue != null && currentValue == null) || (lastValue == null && currentValue != null)) {
				breakLevel = a + 1; // values are not equal
			}
			// else the values are equal, try the next break
		}

		return breakLevel;
	}

	public ArrayList<String> getBreaks() {
		return breaks;
	}

	public String getBreakScript(String columnName) {
		if (cursorName == null) {
			throw new IllegalStateException("cursorName has not been set");
		}
		if (columnName == null) {
			throw new IllegalStateException("columnName is null");
		}
		String retval = breakScripts.getScript(cursorName, columnName);
		return retval;
	}

	public java.sql.Date getDate(int columnIndex) throws SQLException {
		java.sql.Date d = rset.getDate(columnIndex);
		Date retval = datePool.get(d);
		if (retval == null) {
			retval = d;
			datePool.put(d, d);
		}
		datesFetched++;
		// String poolName = indexColumnName.get(columnIndex);
		// stringStores.add(poolName, retval);
		return retval;
	}

	public Date getDate(String columnName) throws SQLException {
		Date d = null;
		Integer columnIndex = columnNameIndex.get(columnName);
		if (columnIndex != null) {
			d = rset.getDate(columnIndex);
		} else {

			d = rset.getDate(columnName);
		}
		datesFetched++;
		Date retval = datePool.get(d);
		if (retval == null) {
			retval = d;
			datePool.put(d, d);
		}
		// stringStores.add(columnName.toUpperCase(), retval);
		return retval;
	}

	public int getDatePoolSize() {
		return datePool.size();
	}

	public int getDatesFetchedCount() {
		return datesFetched;
	}

	public Object getObject(int index) throws SQLException {

		switch (meta.getColumnType(index)) {
		case Types.DATE:
			return getDate(index);

		case Types.VARCHAR:
		case Types.CHAR:
			return getString(index);

		default:
			return rset.getObject(index);
		}

	}

	public Object getObject(String columnName) throws SQLException {
		return getObject(columnNameIndex.get(columnName.toUpperCase()));
	}

	public Object[] getRow() throws SQLException {
		return getRow(new Object[meta.getColumnCount()]);
	}

	public Object[] getRow(Object[] dataArray) throws SQLException {
		if (!rset.next()) {
			return null;
		}
		rowNumber++;

		for (int a = 0; a < dataArray.length; a++) {
			// @todo implement data type specific assignments with formatting
			// here
			dataArray[a] = getObject(a + 1);
		}

		previousRow = currentRow;
		currentRow = dataArray;
		return dataArray;
	}

	public long getRowCount() {
		return rowNumber;
	}

	public long getRowNumber() {
		return rowNumber;
	}

	public String getString(int columnIndex) throws SQLException {
		String s = getDateAsString(columnIndex);
		String retval = stringPool.get(s);
		if (retval == null) {
			retval = s;
			stringPool.put(s, s);
		}
		stringsFetched++;
		String poolName = indexColumnName.get(columnIndex);
		stringStores.add(poolName, retval);
		return retval;
	}

	public String getString(String columnName) throws SQLException {
		String s = null;
		Integer columnIndex = columnNameIndex.get(columnName);
		if (columnIndex != null) {
			s = getDateAsString(columnIndex);
		} else {

			s = rset.getString(columnName);
		}
		stringsFetched++;
		String retval = stringPool.get(s);
		if (retval == null) {
			retval = s;
			stringPool.put(s, s);
		}
		stringStores.add(columnName.toUpperCase(), retval);
		return retval;
	}

	public int getStringPoolSize() {
		return stringPool.size();
	}

	public int getStringsFetchedCount() {
		return stringsFetched;
	}

	public StringStores getStringStores() {
		return stringStores;
	}

	public void setBreaks(ArrayList<String> _breaks) {
		this.breaks = _breaks;
		if (breaks != null) {
			// set uppercase one time for later processing
			for (int a = 0; a < breaks.size(); a++) {
				breaks.set(a, breaks.get(a).toUpperCase());
			}
		}
	}

	public void setCursorBreakScripts(CursorBreakScripts breakScripts) {
		breaks.addAll(breakScripts.getBreakColumns(cursorName));
		this.breakScripts = breakScripts;
	}

	public void setDateFormat(String format) {
		dateFormatter = new SimpleDateFormat(format);
	}

	private String getDateAsString(int column) throws SQLException {
		String returnValue = null;
		int type = meta.getColumnType(column);
		switch (type) {
		case java.sql.Types.DATE:
		case Types.TIMESTAMP:
			java.sql.Date d = rset.getDate(column);
			if (d != null) {
				returnValue = dateFormatter.format(d);
			}
			break;
		default:
			returnValue = rset.getString(column);
		}
		return returnValue;
	}

	/**
	 * @return the whenColumnMap
	 */
	private HashMap<String, String> getWhenColumnMap() {
		return whenColumnMap;
	}

	/**
	 * @return boolean previous value
	 * @param value
	 */
	private boolean setDebug(boolean value) {
		boolean formerValue = debug;
		debug = value;
		return formerValue;
	}

	/**
	 * @param whenColumnMap the whenColumnMap to set
	 */
	private void setWhenColumnMap(HashMap<String, String> whenColumnMap) {
		this.whenColumnMap = whenColumnMap;
	}

}
