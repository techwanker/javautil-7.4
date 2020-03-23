package org.javautil.dataset;

import java.util.ArrayList;
import java.util.List;

import org.javautil.document.crosstab.CrosstabColumns;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.util.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 */
public class ExtendedSalesDataset {
	public Logger logger = LoggerFactory.getLogger(getClass());

	static {
	}
	@SuppressWarnings("boxing")
	private final DatasetMetadataImpl meta = new DatasetMetadataImpl() {
		{
			int ndx = 0;
			addColumn(new ColumnMetadata().withColumnName("CST_ID").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
			addColumn(new ColumnMetadata().withColumnName("PRODUCT_ID").withColumnIndex(ndx++).withDataType(DataType.STRING));
			addColumn(new ColumnMetadata().withColumnName("PRODUCT_DESCR").withColumnIndex(ndx++)
			    .withDataType(DataType.STRING).withHeading("Product\nDescription"));
			addColumn(new ColumnMetadata().withColumnName("INVOICE_DT").withColumnIndex(ndx++).withDataType(DataType.DATE)
			    .withJavaFormat("yyyy/MM/dd"));
			addColumn(new ColumnMetadata().withColumnName("MONTH").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
			addColumn(new ColumnMetadata().withColumnName("BOXES").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
			addColumn(
			    new ColumnMetadata().withColumnName("BOXES_PRIOR_YR").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
			addColumn(new ColumnMetadata().withColumnName("CASES").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
			addColumn(
			    new ColumnMetadata().withColumnName("CASES_PRIOR_YR").withColumnIndex(ndx++).withDataType(DataType.INTEGER));
		}
	};

	public List<ColumnMetadata> getColumnMetadata() {
		final ArrayList<ColumnMetadata> list = new ArrayList<ColumnMetadata>();

		meta.getColumnMetadata();
		final ColumnMetadata differenceColumn = new ColumnMetadata();
		differenceColumn.setColumnIndex(3);
		differenceColumn.setPrecision(12);
		differenceColumn.setHorizontalAlignment(HorizontalAlignment.RIGHT);

		// = new ColumnMetadata("DIFFERENCE", 3, null, null, null, 12,
		// HorizontalAlignment.RIGHT, "###,###,###", null);
		final String formula = "${QTY} - ${QTY_PRIOR_YR}";
		differenceColumn.setWorkbookFormula(formula);
		differenceColumn.setHeading("Difference\nFrom\nLast Year");
		list.add(differenceColumn);
//		final ColumnMetadata ratioColumn = new ColumnMetadata("RATIO", 3, null, null, null, 12,
//				HorizontalAlignment.RIGHT, "#,###.000", null);
//		
		final ColumnMetadata ratioColumn = new ColumnMetadata();
		ratioColumn.setColumnIndex(3);
		final String ratioFormula = "${QTY} / ${QTY_PRIOR_YR}";
		ratioColumn.setWorkbookFormula(ratioFormula);
		list.add(ratioColumn);
		return list;
	}

	@SuppressWarnings("unchecked")
	private final MatrixDataset purchases = new MatrixDataset(meta) {
		{
			addRow(toList(Integer.valueOf(1), "1-1", "Nacho Dip", new Day(2009, 9, 01), new Day(2009, 9, 30), new Double(10),
			    new Double(42), new Integer(3), new Integer(17)));
			addRow(toList(Integer.valueOf(1), "1-1", "Nacho Dip", new Day(2009, 8, 01), new Day(2009, 8, 31), new Double(2),
			    new Double(27), new Integer(15), new Integer(19)));

			addRow(toList(Integer.valueOf(2), "1-1", "Nacho Dip", new Day(2009, 9, 01), new Day(2009, 9, 30), new Double(10),
			    new Double(35), new Integer(4), new Integer(15)));
			addRow(toList(Integer.valueOf(2), "1-1", "Nacho Dip", new Day(2009, 8, 01), new Day(2009, 8, 31), new Double(2),
			    new Double(12), new Integer(3), new Integer(8)));
		}
	};

	public Dataset getDataset() {
		return purchases;
	}

	@SuppressWarnings("unchecked")
	ArrayList<Object> toList(final Object... o) {
		final ArrayList<Object> al = new ArrayList<Object>(o.length);
		for (final Object element : o) {
			al.add(element);
		}
		return al;
	}

	List<String> toList(final String... o) {
		final ArrayList<String> al = new ArrayList<String>(o.length);
		for (final String element : o) {
			al.add(element);
		}
		return al;
	}

	public List<String> getRowIdentifyingColumns() {
		final List<String> rowId = toList("CST_ID", "PRODUCT_ID", "PRODUCT_DESCR");
		return rowId;
	}

	// public List<String> getNonRowIdentifyingColumns() {
	// List<String> rowId = toList("CST_ID", "PRODUCT_ID",
	// "PRODUCT_DESCR","DIFFERENCE");
	// return rowId;
	// }

	public String getColumnIdentifyingColumns() {
		final String columnId = "MONTH";
		return columnId;
	}

	public List<String> getCellColumnNames() {
		return toList("QTY", "QTY_PRIOR_YR");
	}

	public List<String> getRenderCellColumnNames() {
		return toList("RATIO", "QTY", "QTY_PRIOR_YR", "DIFFERENCE");
	}

	public Dataset getCrosstabbedDataset() {
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = getRowIdentifyingColumns();
		final String columnId = getColumnIdentifyingColumns();
		final List<String> cellId = getCellColumnNames();
		final CrosstabColumns ctc = new CrosstabColumns(rowId, columnId, cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(purchases);

		final Dataset ds = coke.getDataSet();
		return ds;
	}

}
