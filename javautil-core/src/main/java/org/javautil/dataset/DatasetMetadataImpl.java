package org.javautil.dataset; 
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.javautil.core.sql.ResultSetMetaDataCache;
import org.javautil.core.sql.ResultSetMetadataColumnCache;
import org.javautil.core.text.StringUtils;
import org.javautil.document.style.HorizontalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * 2019-01-26 had to eliminate overrides for setters and have just one to allow
 * jackson to deserialize TODO can this be supported
 * 
 */
public class DatasetMetadataImpl implements MutableDatasetMetadata {

	private final ArrayList<ColumnMetadata>       list                      = new ArrayList<ColumnMetadata>();
	@JsonIgnore
	private final TreeMap<String, List<Integer>>  columnIndexesByColumnName = new TreeMap<String, List<Integer>>();
	@JsonIgnore
	private static final Logger  logger  = LoggerFactory.getLogger(DatasetMetadataImpl.class);

	private LinkedHashMap<String, ColumnMetadata> columnMetaDataMap         = new LinkedHashMap<>();

	public DatasetMetadataImpl() {
	}

	public DatasetMetadataImpl(ResultSet rset) throws SQLException {
		ResultSetMetaData rmd = rset.getMetaData();
		int columnCount = rmd.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			ColumnMetadata cm = new ColumnMetadata();
			cm.setColumnIndex(i);
			cm.setColumnDisplaySize(rmd.getColumnDisplaySize(i));
			cm.setColumnName(rmd.getColumnName(i));
			addColumn(cm);
		}
		buildIndexes();
	}

	public DatasetMetadataImpl(final List<ColumnMetadata> columns) {
		for (final ColumnMetadata column : columns) {
			addColumn(column);
		}
		buildIndexes();
	}

	public DatasetMetadataImpl(ResultSetMetaDataCache cache) {
		if (cache == null) {
			throw new IllegalArgumentException("cache is null");
		}
		for (ResultSetMetadataColumnCache col : cache.getColumns()) {
			ColumnMetadata cm = new ColumnMetadata();
			cm.setColumnIndex(col.getColumnIndex());
			;
			cm.setColumnDisplaySize(col.getColumnDisplaySize());
			cm.setColumnName(col.getColumnName());
			cm.setDataType(col.getDataType());
			cm.setDataTypeName(col.getColumnTypeName());
			cm.setPrecision(col.getPrecision());
			cm.setScale(col.getScale());
			addColumn(cm);
		}
		buildIndexes();
	}

	/**
	 * Adds the column and sets the column index in column
	 * 
	 * @param column The ColumnMetadata
	 */
	@Override
	public void addColumn(final ColumnMetadata column) {
		list.add(column);
		columnMetaDataMap.put(column.getColumnName(), column);
		buildIndexes();
	}

	@Override
	public void addColumn(final int index, final ColumnMetadata column) {
		if (column == null) {
			throw new IllegalArgumentException("column is null");
		}
		list.add(index, column);
		buildIndexes();
	}

	@Override
	public int[] getColumnIndexes(final String... columnNames) {
		final int[] columnIndexes = new int[columnNames.length];
		int columnIndex = 0;

		for (final String columnName : columnNames) {
			columnIndexes[columnIndex++] = getColumnIndex(columnName);
		}
		return columnIndexes;
	}

	/**
	 * Returns the indexes of everyColumn except those indicated.
	 * 
	 * @param columnNames names of the columns not to be included
	 * @return indexes of he columns
	 */
	@Override
	public int[] getNonColumnIndexes(final String... columnNames) {
		final TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (int i = 0; i < list.size(); i++) {
			indexes.add(i);
		}
		for (final int j : getColumnIndexes(columnNames)) {
			indexes.remove(j);
		}
		final int[] retval = new int[indexes.size()];
		int k = 0;
		for (final Integer in : indexes) {
			retval[k++] = in;
		}
		return retval;
	}

	@Override
	public MutableDatasetMetadata getMetadataForColumns(final String... columnNames) {
		return getByIndex(getColumnIndexes(columnNames));
	}

	@Override
	public DatasetMetadata getMetadataForNonColumns(final String... columnNames) {
		return getByIndex(getNonColumnIndexes(columnNames));
	}

	public MutableDatasetMetadata getByIndex(final int[] indexes) {
		final DatasetMetadataImpl dmi = new DatasetMetadataImpl();
		for (final int index : indexes) {
			dmi.addColumn(getColumnMetaData(index));
		}
		return dmi;
	}

	private void buildIndexes() {
		columnIndexesByColumnName.clear();
		for (int index = 0; index < list.size(); index++) {
			final ColumnMetadata columnMetadata = list.get(index);
			final String columnName = columnMetadata.getColumnName();
			if (columnName == null) {
				throw new IllegalArgumentException("columnName is null " + columnMetadata);
			}
			List<Integer> indexList = columnIndexesByColumnName.get(columnName);
			if (indexList == null) {
				indexList = new ArrayList<Integer>();
				columnIndexesByColumnName.put(columnName, indexList);
			}
			indexList.add(index);
			if (logger.isDebugEnabled() && indexList.size() > 1) {
				// todo, what is this??
				logger.trace("duplicate column name : '" + columnName + "' at " + index);
			}
		}
	}

	public void enhance(Map<String, ColumnMetadata> columnMap) {
		LinkedHashMap<String, ColumnMetadata> mdc = getColumnMetadataMap();
		logger.debug("metadata.size() {}", getColumnMetadata().size());
		String oldHeading = null;
		for (Entry<String, ColumnMetadata> e : mdc.entrySet()) {
			ColumnMetadata newMeta = columnMap.get(e.getKey());
			ColumnMetadata datasetMeta = e.getValue();
			String newHeading = null;
			if (newMeta != null) {
				newHeading = newMeta.getHeading();
			}
			if (newHeading == null) {
				oldHeading = datasetMeta.getHeading();
				datasetMeta.setHeading(StringUtils.toPrettyName(datasetMeta.getColumnName()));
				logger.debug("changing heading from {} to {}", oldHeading, datasetMeta.getHeading());
			} else {
				datasetMeta.setHeading(newHeading);
				logger.debug("leaving heading '{}' for column {} ", oldHeading, datasetMeta.getColumnName());
			}

			if (DataType.DATE.equals(datasetMeta.getDataType())
					|| (newMeta != null && DataType.DATE.equals(newMeta.getDataType()))) {
				datasetMeta.setHorizontalAlignment(HorizontalAlignment.LEFT);
				datasetMeta.setColumnDisplaySize(10);
				logger.debug("columnName {} alignment {} displaysize{})", datasetMeta.getColumnName(),
						datasetMeta.getHorizontalAlignment(), datasetMeta.getColumnDisplaySize());
			}
			if (DataType.NUMERIC.equals(datasetMeta.getDataType())) {
				datasetMeta.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			}
		}
		logger.debug("after enhance:\n{}", this.toString());
	}

	private ColumnMetadata get(final int index) {
		try {
			return list.get(index);
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					"unable to get column information for index " + index + " columns " + list.size());
		}
	}

	@Override
	public int getColumnCount() throws DatasetException {
		return list.size();
	}

	@Override
	public Integer getColumnDisplaySize(final int columnNumber) throws DatasetException {
		return get(columnNumber).getColumnDisplaySize();
	}

	@Override
	public Integer getColumnIndex(final String columnName) {
		final List<Integer> retval = columnIndexesByColumnName.get(columnName);
		logger.debug("columnIndexesByColumnName {}", columnIndexesByColumnName);
		logger.debug("getColumnIndex columnName: '{}' {}", columnName, retval);
		if (retval == null || retval.size() != 1) {
			final List<Integer> cids = columnIndexesByColumnName.get(columnName);
			final StringBuilder sb = new StringBuilder();
			sb.append("no such column or duplicate column '");
			sb.append(columnName);
			sb.append("' ");
			sb.append("\n");
			sb.append(toString());
			sb.append("indexes are " + cids);
			throw new MetadataException(sb.toString());
		}
		return retval.get(0);
	}

	@Override
	public ColumnMetadata getColumnMetaData(final int columnIndex) {
		try {
			return list.get(columnIndex);
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("request for column at index " + columnIndex + " column count " + list.size());
		} catch (final IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("request for column at index " + columnIndex + " column count " + list.size());
		}
	}

	@Override
	public ColumnMetadata getColumnMetaData(final Integer index) {
		final ColumnMetadata retval = list.get(index);
		if (retval == null) {
			throw new IllegalArgumentException("no such column");
		}
		return retval;
	}

	@Override
	public String getColumnName(final int i) throws DatasetException {
		return get(i).getColumnName();
	}

	@Override
	public DataType getColumnType(final int index) throws DatasetException {
		return get(index).getDataType();
	}

	@Override
	public MutableDatasetMetadata getMutable() {
		return this;
	}

	@Override
	public Integer getPrecision(final int columnNumber) throws DatasetException {
		return get(columnNumber).getPrecision();
	}

	@Override
	public int getRowCount() throws DatasetException {
		return 0;
	}

	@Override
	public Integer getScale(final int columnNumber) throws DatasetException {
		return get(columnNumber).getScale();
	}

	@Override
	public HorizontalAlignment getAlignment(final int i) {
		return get(i).getHorizontalAlignment();
	}

	@Override
	public String getExcelFormat(final int i) {
		return get(i).getExcelFormat();
	}

	@Override
	public String getJavaFormat(final int i) {
		return get(i).getJavaFormat();
	}

	@Override
	public ColumnMetadata getColumnMetaData(final String columnName) {
		return getColumnMetaData(getColumnIndex(columnName));
	}

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata() {
		return list;
	}

	@Override
	public Collection<Integer> getColumnIndexes(final String columnName) {
		return columnIndexesByColumnName.get(columnName);
	}

	@Override
	public String getColumnTypeName(int columnNumber) {
		return getColumnMetaData(columnNumber).getColumnTypeName();
	}

	@Override
	public ArrayList<String> getColumnNames() {
		ArrayList<String> columnNames = new ArrayList<String>();
		for (ColumnMetadata cm : list) {
			columnNames.add(cm.getColumnName());
		}
		return columnNames;
	}

	@Override
	public LinkedHashMap<String, ColumnMetadata> getColumnMetadataMap() {
		return columnMetaDataMap;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();
		for (ColumnMetadata columnMetaData : list) {
			b.append(columnMetaData);
			b.append("\n");
		}
		return b.toString();
	}

	public static String columnList(ArrayList<ColumnMetadata> cols, String colon, int colLength, boolean castNulls) {

		StringBuilder sb = new StringBuilder();
		int columnPerLine = 4;
		int i = 0;
		int colsThisLine = 0;
		String leftMargin = "    ";
		int padToLength = leftMargin.length() + colLength + 2 + 19;

		StringBuilder sb2 = new StringBuilder();
		sb2.append(leftMargin);

		for (ColumnMetadata col : cols ) {
			if (col.getDataType().isNumeric() & castNulls) {
				sb2.append("cast (");
				sb2.append(colon);
				sb2.append(col.getColumnName());
				sb2.append(" as numeric) ");
			} else {
				sb2.append(colon);
				sb2.append(col.getColumnName());
			}

			if (++i < cols.size()) {
				sb2.append(", ");
			}
			int padAmount = padToLength - sb2.length();
			String pad = StringUtils.rightPad("",padAmount);
			sb2.append(pad);

			if (++colsThisLine >= columnPerLine) { 
				sb.append("\n");
				sb.append(leftMargin);
				colsThisLine = 0;
			}
			sb.append(sb2);
			sb2 = new StringBuilder();
		}
		return sb.toString();
	}

	public static String getInsertStatement(String tableName, DatasetMetadata meta, boolean castNullAsNumeric) {
		ArrayList<ColumnMetadata> cols = meta.getColumnMetadata();
		int maxNameLength= ColumnMetadata.maxColumnNameLength(cols);
		String retval = null;
		//final  String indent = "      ";
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(tableName);
		sb.append(" (\n");
		sb.append(columnList(cols,"",maxNameLength,false));
		sb.append("\n)values (\n");
		sb.append(columnList(cols,":",maxNameLength,true
				));
		sb.append("\n)");
		retval = sb.toString();
		logger.debug("getInsertStatement()\n{}",retval);
		return retval;
	}

	@Override
	public Map<String, Integer> getSqlTypeMap() {
		Map<String,Integer> sqlTypes = new TreeMap<>();
		for (String colName :  columnMetaDataMap.keySet()) {
			ColumnMetadata meta = columnMetaDataMap.get(colName);
			int sqlType = meta.getDataType().getSqlType();
			sqlTypes.put(colName,sqlType);
		}
		return sqlTypes;
	}
}
