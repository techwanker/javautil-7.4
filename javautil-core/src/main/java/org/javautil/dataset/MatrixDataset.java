package org.javautil.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.javautil.core.csv.CsvReader;
import org.javautil.core.sql.ResultSetHelper;
import org.javautil.dataset.csv.DatasetMetadataUnmarshallerCsv;
import org.javautil.dataset.filter.MutableDatasetFilter;
import org.javautil.dataset.math.CollectionMathOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixDataset extends ArrayList<ArrayList<Object>> implements Dataset, MutableDataset {
	/**
		 * 
		 */
	private static final long      serialVersionUID = 1L;

	/**
	 * 
	 * 
	 * 
	 * Recommended order of operation:
	 * 
	 * 1) Populate Matrix 2) Add CollectionMathOperation Footers 3) Insert
	 * MathOperation columns
	 * 
	 * todo make this truly generic
	 * 
	 * should just extend
	 * 
	 * 
	 */

	private MutableDatasetMetadata metadata;

	private Object[]               footerValues     = null;

	private final String           newline          = System.getProperty("line.separator");
	private final Logger           logger           = LoggerFactory.getLogger(getClass());

	private String                 name;

	public MatrixDataset(ResultSet rset) throws SQLException {
		metadata = DatasetMetadataFactory.getInstance(rset.getMetaData());
		for (ArrayList<Object> row : ResultSetHelper.toListOfLists(rset))
			;
		super.addAll(ResultSetHelper.toListOfLists(rset));
	}

	public MatrixDataset(InputStream dataStream, InputStream metaStream) throws IOException {
		DatasetMetadataUnmarshallerCsv metaUnmarshaller = new DatasetMetadataUnmarshallerCsv();

		MutableDatasetMetadata dm = metaUnmarshaller.getMetadata(metaStream);
		this.metadata = dm;

		CsvReader dataReader = new CsvReader(dataStream);
		ArrayList<Object> values = null; // use Opencsv to get the values
		while ((values = dataReader.readLineAsObjects()) != null) {
			appendRow(values);
		}
		dataReader.close();
	}

	public MatrixDataset(final MutableDatasetMetadata meta, final List<Object[]> data) {
		this(meta);
		for (final Object[] cursor : data) {
			final ArrayList<Object> row = new ArrayList<Object>(cursor.length);
			for (final Object element : cursor) {
				row.add(element);
			}
			add(row);
		}
	}

	public MatrixDataset(final MutableDatasetMetadata meta) {
		if (meta == null) {
			throw new IllegalArgumentException("meta is null");
		}
		this.metadata = meta;
	}

	public void setValue(final int rowIndex, final int columnIndex, final Object value) {
		final ArrayList<Object> row = get(rowIndex);
		row.set(columnIndex, (Object) value);
	}

	@SuppressWarnings("unchecked")
	public static MatrixDataset getInstance(final Dataset dataset) {
		MatrixDataset returnValue = null;
		if (dataset == null) {
			throw new IllegalArgumentException("dataset is null");
		}
		if (dataset instanceof MatrixDataset) {
			returnValue = (MatrixDataset) dataset;
		} else {
			final DatasetMetadata oldMeta = dataset.getMetadata();
			final MutableDatasetMetadata dm = DatasetMetadataFactory.getMutableCopy(oldMeta);
			final MatrixDataset md = new MatrixDataset(dm);
			returnValue = md;
		}
		return returnValue;
	}

	public MutableDatasetMetadata getMetadata() {
		return metadata;
	}

	public int getRowCount() {
		return size();
	}

//	public void setData(final ArrayList<ArrayList<Object>> arrayList) {
//		replaceAll(arrayList);
//	}

	public void addRow(final Object[] cols) {
		if (cols == null) {
			throw new IllegalArgumentException("cols is null");
		}
		final ArrayList<Object> row = new ArrayList<Object>(cols.length);
		for (final Object cell : cols) {
			row.add(cell);
		}

		add(row);
	}

	public void addRow(final List<Object> cols) {
		if (cols == null) {
			throw new IllegalArgumentException("cols is null");
		}
		ArrayList<Object> newRow = new ArrayList<Object>();
		newRow.addAll(cols);
		add(newRow);
	}

	public void addRow(final ArrayList<Object> cols) {
		if (cols == null) {
			throw new IllegalArgumentException("cols is null");
		}
		// todo check column types against
		add(cols);
	}

//	public ArrayList<ArrayList<?>> getRows() {
//		return this;
//	}

	public ArrayList<Object> getRow(final int rowIndex) {
		if (size() < rowIndex + 1) {
			throw new ArrayIndexOutOfBoundsException(
			    "requested rowIndex " + rowIndex + " but there are " + size() + " rows ");
		}
		final ArrayList<Object> c = get(rowIndex);
		return c;
	}

	public Object getValue(final int rowIndex, final int i) {
		Object o = null;
		final ArrayList<Object> c = getRow(rowIndex);

		if (c.size() > i) {
			o = c.get(i);
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(metadata);
			sb.append("size of row " + rowIndex + " " + c.size() + ": ");
			String comma = "";
			for (int j = 0; j < c.size(); j++) {
				sb.append(comma);
				sb.append(c.get(j));
				comma = ",";
			}
			// TODO consider that this might be dumping sensitive information
			throw new ArrayIndexOutOfBoundsException("requested column " + i + " but there are " + c.size()
			    + " columns in row " + rowIndex + newline + sb.toString());
		}
		return o;
	}

	public Object getValue(final int rowIndex, final String column) {
		return getValue(rowIndex, metadata.getColumnIndex(column));
	}

	public String toString() {
		final StringBuilder b = new StringBuilder();
		b.append(metadata.toString());
		b.append("rowCount: " + size() + newline);

		for (final ArrayList<Object> row : this) {
			b.append(asString(row));
			b.append(newline);
		}
		return b.toString();
	}

	public String asString(final ArrayList<Object> row) {
		final StringBuilder b = new StringBuilder();
		String retval = "null";
		if (row == null) {
			retval = "null";
		} else {
			for (int i = 0; i < row.size(); i++) {
				if (i > 0) {
					b.append(" ");
				}
				final Object obj = row.get(i);
				b.append(obj == null ? "null" : obj.toString());

			}
			retval = b.toString();
		}
		return retval;
	}

	public boolean equalData(final Dataset other) {
		final boolean retval = false;

		return retval;
	}

	public void insertColumn(final int index, final ColumnMetadata columnMeta) {
		if (footerValues != null) {
			throw new IllegalStateException("Please add all columns before computing footers.");
		}

		metadata.addColumn(index, columnMeta);
		for (final ArrayList<Object> row : this) {
			row.add(index, null);
		}
	}

	public void addColumn(final ColumnMetadata columnMeta) {
		if (footerValues != null) {
			throw new IllegalStateException("Please add all columns before computing footers.");
		}
		metadata.addColumn(columnMeta);
	}

	public void appendRow(final ArrayList<Object> values) {
		if (footerValues != null) {
			throw new IllegalStateException("Please add all columns before computing footers.");
		}
		ArrayList<Object> newValues;

		if (values == null) {
			throw new IllegalArgumentException("values is null");
		}
		if (values.size() < metadata.getColumnCount()) {
			newValues = new ArrayList<Object>(metadata.getColumnCount());
			logger.warn("values size " + values.size() + " but meta column count " + metadata.getColumnCount());
			while (values.size() < metadata.getColumnCount()) {
				newValues.add(null);
			}
		} else if (values.size() > metadata.getColumnCount()) {
			List<Object> sublist = values.subList(0, metadata.getColumnCount() - 1);
			newValues = new ArrayList<Object>();
			newValues.addAll(sublist);
		} else if (values.size() == metadata.getColumnCount()) {
			newValues = values;
		} else {
			throw new IllegalStateException("logic error");
		}
		validateRow(newValues);
		add(newValues);
	}

	private void validateRow(final List<Object> colData) {

		if (colData.size() != metadata.getColumnCount()) {
			throw new IllegalStateException("row size " + colData.size() + " meta cols " + metadata.getColumnCount());
		}
		for (int i = 0; i < colData.size(); i++) {
			final DataType dt = metadata.getColumnType(i);
			final Object o = colData.get(i);
			if (!dt.isType(o) && coerce(o, dt) != null) {
				throw new IllegalArgumentException("column at index " + i + " not valid for " + dt);
			}
		}
	}

	// TODO use DataType.coerce

	Object coerce(final Object o, final DataType type) {
		Object returnValue = null;
		if (type == null) {
			throw new IllegalArgumentException("type is null");
		}
		if (type == DataType.INTEGER && o != null) {

			// TODO support other number types and date
			if (o instanceof String) {
				final String string = (String) o;
				try {
					final Integer x = Integer.parseInt(string);
					returnValue = x;
				} catch (final NumberFormatException nfe) {
					throw new IllegalArgumentException(" string " + string + "cannot be parsed as an integer", nfe);
				}
			}

		}
		return returnValue;
	}

	public void appendToRow(final Integer rownum, final ArrayList<Object> values) {
		if (footerValues != null) {
			throw new IllegalStateException("Please add all columns before computing footers.");
		}
		final List<Object> newValues = get(rownum);
		if (newValues == null) {
			throw new IllegalArgumentException("row not found " + rownum);
		}
		if (values == null) {
			throw new IllegalArgumentException("values is null");
		}
		newValues.addAll(values);
		validateRow(newValues);
		ArrayList<Object> row = get(rownum);
		row.addAll(newValues);

	}

	public void appendToRow(final Integer rownum, final Object[] values) {
		if (footerValues != null) {
			throw new IllegalStateException("Please add all rows before computing footers.");
		}
		if (values == null) {
			throw new IllegalArgumentException("values is null");
		}
		final ArrayList<Object> colValues = new ArrayList<Object>(values.length);
		for (final Object value : values) {
			colValues.add(value);
		}
		appendToRow(rownum, colValues);
	}

	// TODO figure out what we are trying to accomplish here
	// should the type be constrained by T
	// resolving the value should be done as a formula in spreadsheets
	// perhaps this should be done in a subclass the is restricted to numbers or
	// supports comparable
	// public void insertColumn(String columnName, int firstIndex,
	// int secondIndex, int insertIndex, MathOperation mathOper) {
	// for (List<T> cellRow : cells) {
	// Object o1 = cellRow.get(firstIndex);
	// Object o2 = cellRow.get(secondIndex);
	// if (o1 != null && !(o1 instanceof Number)) {
	// throw new IllegalArgumentException(
	// "firstIndex references a non java.lang.Number value.");
	// }
	// if (o2 != null && !(o2 instanceof Number)) {
	// throw new IllegalArgumentException(
	// "secondIndex references a non java.lang.Number value.");
	// }
	//
	// Number num1 = (Number) o1;
	// Number num2 = (Number) o2;
	// double result = mathOper.compute(num1, num2);
	// cellRow.add(insertIndex, result);
	// }
	//
	// ColumnMetadata newColumn = new ColumnMetadata();
	// newColumn.setColumnName(columnName);
	// newColumn.setDataType(DataType.DOUBLE);
	// metadata.addColumn(newColumn);
	//
	// // If there is a footer, apply this MathOperation to the footer as well
	// if (footerValues != null) {
	// Object o1 = footerValues[firstIndex];
	// Object o2 = footerValues[secondIndex];
	// if (o1 != null && !(o1 instanceof Number)) {
	// throw new IllegalArgumentException(
	// "firstIndex references a non java.lang.Number value in the footer.");
	// }
	// if (o2 != null && !(o2 instanceof Number)) {
	// throw new IllegalArgumentException(
	// "secondIndex references a non java.lang.Number value in the footer.");
	// }
	//
	// Number num1 = (Number) o1;
	// Number num2 = (Number) o2;
	// double result = mathOper.compute(num1, num2);
	// if (insertIndex >= footerValues.length) {
	// Object[] newFooterValues = new Object[insertIndex + 1];
	// System.arraycopy(footerValues, 0, newFooterValues, 0,
	// footerValues.length);
	// footerValues = newFooterValues;
	// }
	// footerValues[insertIndex] = result;
	// }
	// }

	// todo figure out if we want to keep this or send this to get rendererer to
	// handle
	public void addFooter(final int columnIndex, final CollectionMathOperation footerMathOper) {
		final Number[] columnValues = new Number[size()];
		for (int index = 0; index < size(); index++) {
			final List<?> cellRow = get(index);
			final Object object = cellRow.get(columnIndex);
			if (object != null && !(object instanceof Number)) {
				throw new IllegalArgumentException("columnIndex references a non java.lang.Number value.");
			}
			final Number number = (Number) object;
			columnValues[index] = number;
		}

		if (footerValues == null) {
			footerValues = new Object[metadata.getColumnCount()];
		} else if (columnIndex >= footerValues.length) {
			throw new IllegalStateException("Please add all columns before computing footers.");
		}

		final double computedValue = footerMathOper.compute(columnValues);
		footerValues[columnIndex] = computedValue;
	}

	public void appendFooter() {
		addRow(footerValues);
	}

	public boolean hasFooterValues() {
		return footerValues != null;
	}

	public Object[] getFooterValues() {
		return footerValues;
	}

	// TODO jjs seems like this should be moved out to another class for
	// implementation purposes and used here
	// TODO document must pass all filters
	public void applyFilters(final MutableDatasetFilter... filters) {
		for (final MutableDatasetFilter filter : filters) {
			final Iterator<ArrayList<Object>> iter = iterator();
			while (iter.hasNext()) {
				final List<Object> row = iter.next();
				final String columnName = filter.getColumnName();
				final int columnIndex = metadata.getColumnIndex(columnName);
				final Object value = row.get(columnIndex);
				if (!filter.isMatch(value)) {
					iter.remove();
				}
			}
		}
	}

	public void applySorts(final SortColumn... sorts) {
		final DatasetComparator sorter = new DatasetComparator(this, sorts);
		Collections.sort(this, sorter);
	}

	@Override
	public void close() throws DatasetException {
		// does nothing
	}

	@SuppressWarnings("unchecked")
	@Override
	public DatasetIterator<Object> getDatasetIterator() {
		return new MatrixDatasetIterator(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String _name) {
		this.name = _name;

	}

	@Override
	public Collection<String> getColumnNames() {
		return metadata.getColumnNames();
	}

	public void setData(ArrayList<ArrayList<Object>> data) {
		this.clear();
		this.addAll(data);
	}

	public List<ArrayList<Object>> getCells() {
		return subList(0, size() - 1);
	}
}
