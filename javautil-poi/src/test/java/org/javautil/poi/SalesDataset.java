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
public class SalesDataset {
	public Logger logger = LoggerFactory.getLogger(getClass());
	private static final String newline = System.getProperty("line.separator");
	private static final ColumnMetadata monthMeta = new ColumnMetadata().withColumnName("MONTH").withColumnIndex(0)
			.withDataType(DataType.DATE);
	private static final ColumnMetadata qtyMeta = new ColumnMetadata().withColumnName("QTY").withColumnIndex(1)
			.withDataType(DataType.DOUBLE);
	private static final ColumnMetadata qtyPriorYrMeta = new ColumnMetadata().withColumnName("QTY_PRIOR_YR")
			.withColumnIndex(2).withDataType(DataType.DOUBLE);

	@SuppressWarnings("boxing")
	private final DatasetMetadataImpl meta = new DatasetMetadataImpl() {
		{
			int ndx = 0;
			addColumn(new ColumnMetadata().withColumnName("CST_ID").withColumnIndex(ndx++)
					.withDataType(DataType.INTEGER));
			addColumn(new ColumnMetadata().withColumnName("PRODUCT_ID").withColumnIndex(ndx++)
					.withDataType(DataType.STRING));
			final ColumnMetadata column = new ColumnMetadata().withColumnName("PRODUCT_DESCR").withColumnIndex(ndx++)
					.withDataType(DataType.STRING).withHeading("Product\nDescription");
			addColumn(column);
			addColumn(monthMeta);
			addColumn(qtyMeta);
			addColumn(qtyPriorYrMeta);
		}
	};

	public List<ColumnMetadata> getColumnMetadata() {
		final ArrayList<ColumnMetadata> list = new ArrayList<ColumnMetadata>();

		list.add(qtyMeta);
		list.add(qtyPriorYrMeta);
		meta.getColumnMetadata();
		final ColumnMetadata differenceColumn = new ColumnMetadata().withColumnName("DIFFERENCE").withColumnIndex(3)
				.withHorizontalAlignment(HorizontalAlignment.RIGHT).withExcelFormat("###,###,###");
//		
//		final ColumnMetadata differenceColumn = new ColumnMetadata(
//				"DIFFERENCE", 3, null, null, null, 12,
//				HorizontalAlignment.RIGHT, "###,###,###", null);
		final String formula = "${QTY} - ${QTY_PRIOR_YR}";
		differenceColumn.withWorkbookFormula(formula);
		differenceColumn.withHeading("Difference\nFrom\nLast Year");
		list.add(differenceColumn);
		final ColumnMetadata ratioColumn = new ColumnMetadata().withColumnName("RATIO").withColumnIndex(3)
				.withPrecision(12).withHorizontalAlignment(HorizontalAlignment.RIGHT).withExcelFormat("#,###.000");
//		final ColumnMetadata ratioColumn = new ColumnMetadata("RATIO", 3, null,
//				null, null, 12, HorizontalAlignment.RIGHT, "#,###.000", null);

		final String ratioFormula = "${QTY} / ${QTY_PRIOR_YR}";
		ratioColumn.withWorkbookFormula(ratioFormula);
		list.add(ratioColumn);
		return list;
	}

	@SuppressWarnings("unchecked")
	private final MatrixDataset purchases = new MatrixDataset(meta) {
		{
			addRow(toList(Integer.valueOf(1), "1-1", "Nacho Dip", new Day(2009, 9, 01), new Double(10),
					new Double(42)));
			addRow(toList(Integer.valueOf(1), "1-1", "Nacho Dip", new Day(2009, 8, 01), new Double(2), new Double(27)));

			addRow(toList(Integer.valueOf(2), "1-1", "Nacho Dip", new Day(2009, 9, 01), new Double(10),
					new Double(35)));
			addRow(toList(Integer.valueOf(2), "1-1", "Nacho Dip", new Day(2009, 8, 01), new Double(2), new Double(12)));
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
