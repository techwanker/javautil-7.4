package org.javautil.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.javautil.core.text.StringUtils;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dataset implementation backed by a List of Maps for use with templating
 * engines like FreeMarker that work best when the model object is of a simple
 * java collections type.
 * 
 * The backing arraylist is modifiable and is constructed from another dataset.
 * 
 */
public class ListOfNameValueDataset extends ArrayList<NameValue> implements Dataset {

	private static transient final long serialVersionUID = 1L;

	private DatasetMetadata             metadata         = null;

	private String                      name             = null;
	private transient final Logger      logger           = LoggerFactory.getLogger(getClass());

	public ListOfNameValueDataset(ListOfNameValue data) {
		super.addAll(data);
	}

	public ListOfNameValueDataset(ListOfNameValue data, DatasetMetadata metadata) {
		super.addAll(data);
		this.metadata = metadata;
	}

	public ListOfNameValueDataset(final Dataset dataset) {
		this.metadata = dataset.getMetadata();
		this.name = dataset.getName();
		populate(dataset);
	}

	public ListOfNameValueDataset(final Dataset dataset, final DatasetMetadata metadata) {
		this.name = dataset.getName();
		this.metadata = metadata;
		populate(dataset);
	}

	public ListOfNameValueDataset(final String name, final DatasetMetadata metadata, final ListOfNameValue data) {
		this.name = name;
		this.metadata = metadata;
		for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
			final NameValue row = data.get(rowIndex);
			if (row.size() != metadata.getColumnCount()) {
				throw new IllegalArgumentException(row.size() + " columns were found at rowIndex " + rowIndex + " ; but "
				    + metadata.getColumnCount() + " columns are expected for metadata of type \"" + name + "\":\n"
				    + metadata.toString() + "\n" + "actual row map: " + StringUtils.asString(row));
			}
			for (int n = 0; n < metadata.getColumnCount(); n++) {
				final String colName = metadata.getColumnName(n);
				if (!row.containsKey(colName)) {
					throw new IllegalArgumentException(
					    "expected column  '" + colName + "' does not exist in dataset row " + rowIndex);
				}
			}
		}
		addAll(data);
	}

	public ListOfNameValueDataset(DatasetMetadataImpl metadata) {
		this.metadata = metadata;
	}

	public ListOfNameValueDataset() {
	}

	@SuppressWarnings("unchecked")
	protected void populate(final Dataset sourceDataset) {
		final DatasetIterator iterator = sourceDataset.getDatasetIterator();
		final DatasetMetadata sourceMetadata = sourceDataset.getMetadata();
		final Set<String> colNames = new HashSet<String>();
		for (int n = 0; n < sourceMetadata.getColumnCount(); n++) {
			final String colName = sourceMetadata.getColumnName(n);
			if (colNames.contains(colName)) {
				throw new IllegalArgumentException("column " + colName + " is repeated in dataset");
			}
			colNames.add(colName);
		}
		int rowCount = 0;
		while (iterator.next()) {
			final NameValue row = new NameValue();
			for (int n = 0; n < sourceMetadata.getColumnCount(); n++) {
				final String colName = sourceMetadata.getColumnName(n);
				final Object value = iterator.getObject(n);
				row.put(colName, value);
			}
			this.add(row);
			rowCount++;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
			    "populated list of maps from dataset \"" + sourceDataset.getName() + "\"; " + rowCount + " rows were added");
		}
	}

	@Override
	public void close() throws DatasetException {
	}

	@Override
	public DatasetIterator<Object> getDatasetIterator() {
		return new ListOfMapsDatasetIterator(this);
	}

	@Override
	public DatasetMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	class ListOfMapsDatasetIterator extends AbstractDatasetIterator {

		private ListOfNameValueDataset dataset = null;

		public ListOfMapsDatasetIterator(final ListOfNameValueDataset dataset) {
			this.dataset = dataset;
		}

		@Override
		public int getRowCount() {
			return size();
		}

		@Override
		public Object getValue(final int rowIndex, final int columnIndex) {
			if (rowIndex < 0) {
				throw new IllegalArgumentException("rowIndex specified cannot be negative");
			}
			final Map<String, ? extends Object> row = get(rowIndex);
			if (row == null) {
				throw new IllegalArgumentException("rowIndex specified too large; last rowIndex is " + rowIndex);
			}
			final String columnName = metadata.getColumnName(columnIndex);
			if (columnName == null) {
				throw new IllegalArgumentException("no column at columnIndex " + columnIndex);
			}
			return row.get(columnName);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public Object getValue(final int rowIndex, final String columnName) {
			if (rowIndex < 0) {
				throw new IllegalArgumentException("rowIndex specified cannot be negative");
			}
			final Map<String, ? extends Object> row = get(rowIndex);
			if (row == null) {
				throw new IllegalArgumentException("rowIndex specified too large; last rowIndex is " + rowIndex);
			}
			if (metadata == null) {
				throw new IllegalStateException("metadata is null");
			}
			if (metadata.getColumnIndex(columnName) == -1) {
				throw new IllegalArgumentException("no column named " + columnName);
			}
			final Object value = row.get(columnName);
			return value;
		}

		@Override
		@SuppressWarnings("synthetic-access")
		public DatasetMetadata getDatasetMetadata() {
			return metadata;
		}

		@Override
		public Map<String, Object> getRowAsMap() throws DatasetException {
			return dataset.getRowAsMap(super.getRowIndex());
		}

		@Override
		public List<Object> getRowAsList() throws DatasetException {
			return dataset.getRowAsList(super.getRowIndex());
		}
	}

	public List<Object> getRowAsList(final int rowIndex) {
		final ArrayList<Object> ret = new ArrayList<Object>();
		final Map<String, ? extends Object> row = get(rowIndex);
		if (row == null) {
			throw new IllegalArgumentException("no row at index " + rowIndex);
		}
		for (final String columnName : row.keySet()) {
			ret.add(row.get(columnName));
		}
		return ret;
	}

	public Map<String, Object> getRowAsMap(final int rowIndex) {
		final Map<String, Object> row = get(rowIndex);
		if (row == null) {
			throw new IllegalArgumentException("no row at index " + rowIndex);
		}
		return row;
	}

	@Override
	public Collection<String> getColumnNames() {
		return metadata.getColumnNames();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("metadata:\n");
		sb.append(metadata.toString());
		sb.append("rows: " + this.size() + "\n");
		for (Map<String, Object> row : this) {
			for (Entry<String, Object> e : row.entrySet()) {
				sb.append("\"");
				sb.append(e.getKey());
				sb.append("\" = \" ");

				sb.append(e.getValue());
				sb.append("\" ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	public ArrayList<NameValue> getRows() {
		ArrayList<NameValue> rows = new ArrayList<>();
		for (NameValue row : this) {
			rows.add(row);
		}
		return rows;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(DatasetMetadata metadata) {
		this.metadata = metadata;
	};
}
