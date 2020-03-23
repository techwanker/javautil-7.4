package org.javautil.dataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.javautil.dataset.csv.DatasetCsvMarshaller;
import org.javautil.document.crosstab.CrosstabColumns;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs @ dbexperts
 * 
 */
public class CrosstabDatasetTest extends BaseTest {
	public Logger                     logger                      = LoggerFactory.getLogger(getClass());
	private static final String       newline                     = System.getProperty("line.separator");
	// TODO clean up
	// private ResultValidator resultValidator =new ResultValidator();

	@SuppressWarnings("boxing")
	private final DatasetMetadataImpl meta                        = new DatasetMetadataImpl() {
																																	{
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("STATE").withColumnIndex(0)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("CITY").withColumnIndex(1)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("MONTH").withColumnIndex(2)
																																		    .withDataType(DataType.INTEGER));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("TICKETS").withColumnIndex(3)
																																		    .withDataType(DataType.DOUBLE));
																																	}
																																};

	private final MatrixDataset       tickets                     = new MatrixDataset(meta) {
																																	{
																																		addRow(asList("TX", "DALLAS", new Integer(1),
																																		    new Double(42)));
																																		addRow(asList("TX", "DALLAS", new Integer(2),
																																		    new Double(27)));
																																		addRow(asList("TX", "HOUSTON", new Integer(1),
																																		    new Double(32)));
																																		addRow(asList("TX", "Quoted\"Text", new Integer(3),
																																		    new Double(17)));

																																	}
																																};

	@SuppressWarnings("boxing")

	private final DatasetMetadataImpl deferredAdjudicationSumMeta = new DatasetMetadataImpl() {
																																	{
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("STATE").withColumnIndex(0)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("CITY").withColumnIndex(1)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("Fine").withColumnIndex(2)
																																		    .withDataType(DataType.DOUBLE));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("Legal Fee").withColumnIndex(3)
																																		    .withDataType(DataType.DOUBLE));
																																	}
																																};

	private final MatrixDataset       feeSum                      = new MatrixDataset(deferredAdjudicationSumMeta) {
																																	{
																																		addRow(asList("TX", "DALLAS", new Double(633),
																																		    new Double(380)));
																																		addRow(asList("TX", "HOUSTON", new Double(312),
																																		    new Double(0)));
																																		addRow(asList("TX", "Southlake", new Double(333),
																																		    null));

																																	}
																																};

	private final DatasetMetadataImpl deferredAdjudicationMeta    = new DatasetMetadataImpl() {
																																	{
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("STATE").withColumnIndex(0)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("CITY").withColumnIndex(1)
																																		    .withDataType(DataType.STRING));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("MONTH").withColumnIndex(2)
																																		    .withDataType(DataType.INTEGER));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("Fine").withColumnIndex(3)
																																		    .withDataType(DataType.DOUBLE));
																																		addColumn(new ColumnMetadata()
																																		    .withColumnName("Legal Fee").withColumnIndex(4)
																																		    .withDataType(DataType.DOUBLE));
																																	}
																																};

	private final MatrixDataset       fees                        = new MatrixDataset(deferredAdjudicationMeta) {
																																	{
																																		addRow(asList("TX", "DALLAS", new Integer(1),
																																		    new Double(311), new Double(380)));
																																		addRow(asList("TX", "DALLAS", new Integer(2),
																																		    new Double(321), null));
																																		addRow(asList("TX", "HOUSTON", new Integer(1),
																																		    new Double(312), new Double(0)));
																																		addRow(asList("TX", "Southlake", new Integer(9),
																																		    new Double(333), null));

																																	}
																																};

	@SuppressWarnings("unchecked")
	ArrayList<Object> asList(final Object... o) {
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
		final List<String> rowId = toList("STATE", "CITY");
		return rowId;
	}

	public String getColumnIdentifyingColumns() {
		final String columnId = "MONTH";
		return columnId;
	}

	public Dataset getCrosstabbedDataset() {
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = getRowIdentifyingColumns();
		final String columnId = getColumnIdentifyingColumns();
		final List<String> cellId = toList("TICKETS");
		final CrosstabColumns ctc = new CrosstabColumns(rowId, columnId, cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(tickets);

		final Dataset ds = coke.getDataSet();
		return ds;
	}

	// TODO check that the metadata is what was expected
	@Ignore
	@Test
	public void test2() throws IOException {
		logger.debug("test2");
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("TICKETS");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(tickets);

		final Dataset ds = coke.getDataSet();

		assertExpected(ds);
	}

	@Test
	public void test3() throws IOException {
		logger.debug("test3");
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(fees);
		final Dataset ds = coke.getDataSet();
		assertExpected(ds);

	}

	@Test
	public void test4() throws IOException {
		logger.debug("test4");
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		final Dataset feesData = fees;
		logger.debug("about to crosstab " + newline + fees);
		coke.setDataSet(feesData);

		final Dataset ds = coke.getDataSet();
		assertExpected(ds);

	}

	/*
	 * Take a dataset, crosstab it and then right append to the crosstabbed result
	 * 
	 * todo cam create an expected resultset and compare
	 */

	@SuppressWarnings("serial")
	@Test
	public void test7() throws IOException {
		logger.debug("test7");
		// todo does this make any sense ?
		final Map<String, String> idKeyMap = new TreeMap<String, String>() {
			{
				put("STATE", "STATE");
				put("CITY", "CITY");
				// put("MONTH", "MONTH");
			}
		};

		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		logger.debug("about to crosstab " + newline + fees);
		logger.debug("using crosstab rule " + ctc);
		coke.setDataSet(fees);

		final MutableDataset crosstabbed = (MutableDataset) coke.getDataSet();
		logger.debug("crosstabbed result is " + newline + crosstabbed);

		final DatasetAppender dsa = new DatasetAppender();
		// TODO have to check DatasetAppender
		dsa.appendRight(crosstabbed, feeSum, idKeyMap);
		assertExpected(crosstabbed);
	}

	@SuppressWarnings("serial")
	@Test
	public void test8() {
		logger.debug("test7");
		new TreeMap<String, String>() {
			{
				put("STATE", "STATE");
				put("CITY", "CITY");
				put("MONTH", "MONTH");
			}
		};
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");
		logger.debug("dataset \n" + fees);
		logger.debug("dataset meta\n " + fees.getMetadata());
		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(fees);

		final MutableDataset crosstabbed = (MutableDataset) coke.getDataSet();
		logger.debug("crosstabbed meta\n " + crosstabbed.getMetadata());
		// TODO what is this? What is being tested here?
		new DatasetAppender();

	}

	public void testOeCustMfr() {

		final DatasetMetadataImpl meta = new DatasetMetadataImpl() {
			{
				addColumn(new ColumnMetadata().withColumnName("itemCd").withColumnIndex(0).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("custCd").withColumnIndex(1).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("mfrCd").withColumnIndex(2).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("restrict").withColumnIndex(3).withDataType(DataType.STRING));
			}
		};

		final MatrixDataset ds = new MatrixDataset(meta) {
			{
				addRow(asList("BACB30VT6K3", "BWJIT", "WINBAC", "I"));
				addRow(asList("BACB30VT6K3", "135701", "06725", "I"));
				addRow(asList("BACB30VT6K3", "135701", "06950", "I"));
				addRow(asList("BACB30VT6K3", "135701", "0PTK6", "I"));
				addRow(asList("BACB30VT6K3", "135701", "17446", "I"));
				addRow(asList("BACB30VT6K3", "135701", "56878", "I"));
				addRow(asList("BACB30VT6K3", "135701", "73197", "I"));
				addRow(asList("BACB30VT6K3", "135701", "1RC86", "I"));
				addRow(asList("BACB30VT6K3", "135701", "97928", "I"));
				addRow(asList("BACB30VT6K3", "135701", "INT01", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "06725", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "06950", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "0PTK6", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "17446", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "56878", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "60516", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "73197", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "1RC86", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "97928", "I"));
				addRow(asList("BACB30VT6K3", "BAJIT", "INT01", "I"));
			}
		};
		String dsString = DatasetCsvMarshaller.toString(ds);
		logger.info("dsString:\n{}", dsString);
		//
		DatasetCrosstabber ct = new DatasetCrosstabber();
		ct.setDataSet(ds);

		List<String> rowid = new ArrayList<String>();
		rowid.add("custCd");

		String columnId = "mfrCd";

		List<String> cellId = new ArrayList<String>();
		cellId.add("restrict");

		CrosstabColumns cc = new CrosstabColumns(rowid, columnId, cellId);
		ct.setCrosstabColumns(cc);
		MatrixDataset result = ct.getDataSet();

		String resultString = DatasetCsvMarshaller.toString(result);
		logger.info("resultString:\n{}", resultString);

		int rowCount = ds.getRowCount();
		int colCount = ds.getMetadata().getColumnCount();
		assertEquals(3, rowCount);
		assertEquals(12, colCount);
	}

}
