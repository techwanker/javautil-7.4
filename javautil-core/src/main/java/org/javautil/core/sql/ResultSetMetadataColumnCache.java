package org.javautil.core.sql;

import org.javautil.dataset.DataType;

public class ResultSetMetadataColumnCache {
	private int      columnIndex;

	private int      columnDisplaySize;

	private String   columnName;

	private DataType dataType;

	private String   columnTypeName;

	private int      precision;

	private int      scale;

	public ResultSetMetadataColumnCache() {
		super();
	}

	public ResultSetMetadataColumnCache(int columnIndex, int columnDisplaySize, String columnName) {
		super();
		this.columnIndex = columnIndex;
		this.columnDisplaySize = columnDisplaySize;
		this.columnName = columnName;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(int columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		return String.format("ResultSetMetadataColumnCache [columnIndex=%s, columnDisplaySize=%s, columnName=%s]",
		    columnIndex, columnDisplaySize, columnName);
	}
}
