package org.javautil.poi;

import java.util.ArrayList;
import java.util.List;

import org.javautil.dataset.AbstractDataset;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetCrosstabber;
import org.javautil.dataset.DatasetMetadataImpl;
import org.javautil.dataset.MatrixDataset;
import org.javautil.document.crosstab.CrosstabColumns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs @ dbexperts
 * 
 */
public class CrosstabDataset {
	public Logger logger = LoggerFactory.getLogger(getClass());
	private static final String newline = System.getProperty("line.separator");

	private final DatasetMetadataImpl meta = new DatasetMetadataImpl() {
		{
			addColumn(new ColumnMetadata().withColumnName("STATE").withColumnIndex(0).withDataType(DataType.STRING));
			addColumn(new ColumnMetadata().withColumnName("CITY").withColumnIndex(1).withDataType(DataType.STRING));
			addColumn(new ColumnMetadata().withColumnName("MONTH").withColumnIndex(2).withDataType(DataType.INTEGER));
			addColumn(new ColumnMetadata().withColumnName("TICKETS").withColumnIndex(3).withDataType(DataType.DOUBLE));
		}
	};

	@SuppressWarnings("unchecked")
	private final MatrixDataset tickets = new MatrixDataset(meta) {
		{
			addRow(toList("TX", "DALLAS", new Integer(1), new Double(42)));
			addRow(toList("TX", "DALLAS", new Integer(2), new Double(27)));
			addRow(toList("TX", "HOUSTON", new Integer(1), new Double(32)));
			addRow(toList("TX", "Quoted\"Text", new Integer(3), new Double(17)));

		}
	};

	@SuppressWarnings("unchecked")
	ArrayList<Object> toList(final Object... o) {
		final ArrayList<Object> al = new ArrayList<Object>(o.length);
		for (final Object element : o) {
			al.add(element);
		}
		return al;
	}

	@SuppressWarnings("unchecked")
	ArrayList<String> toList(final String... o) {
		final ArrayList<String> al = new ArrayList<String>(o.length);
		for (final String element : o) {
			al.add(element);
		}
		return al;
	}

	public ArrayList<String> getRowIdentifyingColumns() {
		final ArrayList<String> rowId = toList("STATE", "CITY");
		return rowId;
	}

	public String getColumnIdentifyingColumns() {
		final String columnId = "MONTH";
		return columnId;
	}

	public ArrayList<String> getCellColumnNames() { 
		return toList("TICKETS");
	}

	public Dataset getCrosstabbedDataset() {
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final ArrayList<String> rowId = getRowIdentifyingColumns();
		final String columnId = getColumnIdentifyingColumns();
		final ArrayList<String> cellId = getCellColumnNames();
		final CrosstabColumns ctc = new CrosstabColumns(rowId, columnId, cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(tickets);

		final Dataset ds = coke.getDataSet();
		return ds;
	}

}
