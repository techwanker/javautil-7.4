package org.javautil.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.javautil.document.style.HorizontalAlignment;

/**
 * 
 * @author jjs
 * 
 */
public interface DatasetMetadata {
	public int getColumnCount() throws DatasetException;

	public Integer getColumnDisplaySize(int columnNumber) throws DatasetException;

	public Integer getColumnIndex(String column);

	/**
	 * columnIndex is relative 0 as in everything sane in java not 1 as in jdbc TODO
	 * need to resolve these cases TODO change to getColumnMetadata
	 * 
	 * @param columnIndex the index of the column relative 0
	 * @return the column metadata
	 */
	public ColumnMetadata getColumnMetaData(int columnIndex);

	/**
	 * 
	 * @param index the column index
	 * @return the metdata
	 */
	public ColumnMetadata getColumnMetaData(Integer index);

	public ColumnMetadata getColumnMetaData(String columnName);

	public ArrayList<ColumnMetadata> getColumnMetadata();

	public LinkedHashMap<String, ColumnMetadata> getColumnMetadataMap();
	
	
	public Map<String,Integer> getSqlTypeMap();
	/**
	 * Returns the name of the column at the specified index.
	 * 
	 * @param index the column index
	 * @return the column name
	 * @throws DatasetException Not likely
	 */
	public String getColumnName(int index) throws DatasetException;

	public DataType getColumnType(int index) throws DatasetException;

	public MutableDatasetMetadata getMutable();

	/**
	 * Get the designated column's number of decimal digits.
	 * 
	 * Indexes are relative 0. This may return a null result. Get the designated
	 * column's number of decimal digits.
	 * 
	 * @param columnNumber the column index
	 * @return the precision
	 * @throws DatasetException Not likely
	 */
	public Integer getPrecision(int columnNumber) throws DatasetException;

	public Integer getScale(int columnNumber) throws DatasetException;

	/**
	 * @return the number of rows in the dataset.
	 * @throws DatasetException Possible.
	 */
	public int getRowCount() throws DatasetException;

	public HorizontalAlignment getAlignment(int index);

	/**
	 * Returns the ExcelFormat associated with the column at the specified index.
	 * 
	 * @param index column index
	 * @return The formula
	 */
	public String getExcelFormat(int index);

	/**
	 * TODO what is this?
	 * 
	 * @param index column index
	 * @return The java format
	 */
	public String getJavaFormat(int index);

	/**
	 * Return the indexes, relative 0 of columns with the specified name. More than
	 * one column may have the same name if this is a crosstabbed Dataset.
	 * 
	 * @param columnName the name of the column
	 * @return The column indexes
	 */
	public Collection<Integer> getColumnIndexes(String columnName);

	public int[] getColumnIndexes(String... columnNames);

	/*
	 * Returns the indexes of everyColumn except those indicated.
	 * 
	 * @param columnNames
	 * 
	 * @return
	 */
	public int[] getNonColumnIndexes(String... columnNames);

	public MutableDatasetMetadata getMetadataForColumns(String... columnNames);

	public DatasetMetadata getMetadataForNonColumns(String... columnNames);

	/*
	 * @param meta
	 */
	public void enhance(Map<String, ColumnMetadata> meta);

	public String getColumnTypeName(int columnNumber);

	public ArrayList<String> getColumnNames();

	
}

