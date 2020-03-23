package org.javautil.core.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.javautil.dataset.DataType;

public class ResultSetMetaDataCache {

	private ArrayList<ResultSetMetadataColumnCache> columns = new ArrayList<>();

	public ResultSetMetaDataCache(ResultSet rset) throws SQLException {
		ResultSetMetaData rmd = rset.getMetaData();
		int columnCount = rmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			ResultSetMetadataColumnCache cm = new ResultSetMetadataColumnCache();
			cm.setColumnIndex(i);
			cm.setColumnDisplaySize(rmd.getColumnDisplaySize(i));
			cm.setColumnName(rmd.getColumnName(i));
			cm.setColumnTypeName(rmd.getColumnTypeName(i));
			cm.setPrecision(rmd.getPrecision(i));
			cm.setScale(rmd.getScale(i));
			DataType dt = DataType.getAsDataType(rmd.getColumnType(i));
			cm.setDataType(dt);

			columns.add(cm);
		}
	}

	/**
	 * @return the columns
	 */
	public ArrayList<ResultSetMetadataColumnCache> getColumns() {
		return columns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ResultSetMetaDataCache [columns=%s]", columns);
	}
}
