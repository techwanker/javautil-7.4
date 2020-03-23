package org.javautil.dataset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.stream.StreamResult;

import org.javautil.document.crosstab.CrosstabColumns;
import org.javautil.document.renderer.CsvRenderer;
import org.javautil.document.renderer.CsvRendererRequest;
import org.javautil.document.renderer.CsvRendererRequestImpl;
import org.javautil.test.StackTraceUtils;
import org.javautil.util.Day;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 *         todo test output
 */
public class MatrixDatasetTest extends BaseTest {
	public Logger logger = LoggerFactory.getLogger(getClass());

	private void processResults(final StackTraceElement ste, final byte[] actual) {
		new StackTraceUtils();
		final String destinationPath = StackTraceUtils.getResourceMethodPath(ste);
		final File outputFile = new File(destinationPath);
		logger.debug("processResults " + outputFile.getPath());
	}

	@SuppressWarnings("boxing")
	private final DatasetMetadataImpl meta                     = new DatasetMetadataImpl() {
																																{
																																	addColumn(new ColumnMetadata().withColumnName("STATE")
																																	    .withColumnIndex(0).withDataType(DataType.STRING));
																																	addColumn(new ColumnMetadata().withColumnName("CITY")
																																	    .withColumnIndex(1).withDataType(DataType.STRING));
																																	addColumn(new ColumnMetadata().withColumnName("MONTH")
																																	    .withColumnIndex(2)
																																	    .withDataType(DataType.INTEGER));
																																	addColumn(new ColumnMetadata()
																																	    .withColumnName("TICKETS").withColumnIndex(3)
																																	    .withDataType(DataType.DOUBLE));
																																}
																															};

	private final MatrixDataset       trailingNulls            = new MatrixDataset(meta) {

																																{
																																	final Number n = null;
																																	addRow(toList("TX", "DALLAS", new Integer(1),
																																	    new Double(42)));
																																	addRow(toList("TX", "DALLAS", new Integer(2), null));
																																	addRow(toList("TX", "HOUSTON", n, null));
																																	addRow(toList("TX", n, null, null));
																																}
																															};

	private final MatrixDataset       tickets                  = new MatrixDataset(meta) {
																																{
																																	addRow(toList("TX", "DALLAS", new Integer(1),
																																	    new Double(42)));
																																	addRow(toList("TX", "DALLAS", new Integer(2),
																																	    new Double(27)));
																																	addRow(toList("TX", "HOUSTON", new Integer(1),
																																	    new Double(32)));
																																	addRow(toList("TX", "Quoted\"Text", new Integer(3),
																																	    new Double(17)));
																																}
																															};

	@SuppressWarnings("boxing")
	private final DatasetMetadataImpl deferredAdjudicationMeta = new DatasetMetadataImpl() {
																																{
																																	int colIndx = 0;
																																	addColumn(new ColumnMetadata().withColumnName("STATE")
																																	    .withColumnIndex(colIndx++)
																																	    .withDataType(DataType.STRING));
																																	addColumn(new ColumnMetadata().withColumnName("CITY")
																																	    .withColumnIndex(colIndx++)
																																	    .withDataType(DataType.STRING));
																																	addColumn(new ColumnMetadata().withColumnName("MONTH")
																																	    .withColumnIndex(colIndx++)
																																	    .withDataType(DataType.INTEGER));
																																	addColumn(new ColumnMetadata().withColumnName("Fine")
																																	    .withColumnIndex(colIndx++)
																																	    .withDataType(DataType.DOUBLE));
																																	addColumn(
																																	    new ColumnMetadata().withColumnName("Legal Fee")
																																	        .withColumnIndex(colIndx++)
																																	        .withDataType(DataType.DOUBLE));

																																}
																															};

	private final MatrixDataset       fees                     = new MatrixDataset(deferredAdjudicationMeta) {
																																{
																																	addRow(toList("TX", "DALLAS", new Integer(1),
																																	    new Double(311), new Double(380)));
																																	addRow(toList("TX", "DALLAS", new Integer(2),
																																	    new Double(321), null));
																																	addRow(toList("TX", "HOUSTON", new Integer(1),
																																	    new Double(312), new Double(0)));
																																	addRow(toList("TX", "Southlake", new Integer(3),
																																	    new Double(333), null));

																																}
																															};

	@SuppressWarnings("boxing")
	public Dataset getConvictions() {
		final DatasetMetadataImpl convictionMeta = new DatasetMetadataImpl() {
			{
				int ndx = 0;
				addColumn(new ColumnMetadata().withColumnName("STATE").withColumnIndex(ndx++).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("CITY").withColumnIndex(ndx++).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("Date").withColumnIndex(ndx++).withDataType(DataType.DATE));
				addColumn(
				    new ColumnMetadata().withColumnName("Days in Jail").withColumnIndex(ndx++).withDataType(DataType.DOUBLE));
			}
		};

		final MatrixDataset mds = new MatrixDataset(convictionMeta);
		mds.addRow(toList("TX", "Southlake", new Day(2007, 12, 1), 3));
		mds.addRow(toList("TX", "Coppell", Day.today(), 3));
		return mds;

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

	@Test
	public void test1() throws IOException {
		logger.debug("test1");

		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		crr.setStreamResult(new StreamResult(baos));
		crr.setDataset(trailingNulls);

		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);
		cr.process();
		final StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
		processResults(ste, baos.toByteArray());
	}

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
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		crr.setStreamResult(new StreamResult(baos));
		crr.setDataset(ds);
		// TODO write test
		// final CsvRenderer cr = new CsvRenderer();
		// cr.setRequest(crr);
		// cr.process();
		// logger.debug(baos.toByteArray());
		// final StackTraceElement ste =
		// Thread.currentThread().getStackTrace()[1];
		// // resultValidator.validateResult(ste, baos.toByteArray());
		// processResults(ste, baos.toByteArray());
	}

	@Ignore
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
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		crr.setColumnSeparator("*******");
		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);

		crr.setStreamResult(new StreamResult(System.out));
		crr.setDataset(ds);
		cr.process();

	}

	@Test
	public void test4() throws IOException {
		logger.debug("test4");
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(fees);

		coke.getDataSet();

	}

	@Ignore
	@SuppressWarnings("serial")
	@Test
	public void test5() throws IOException {
		logger.debug("test5");
		final Map<String, String> idKeyMap = new TreeMap<String, String>() {
			{
				put("STATE", "STATE");
				put("CITY", "CITY");
				put("MONTH", "MONTH");
			}
		};
		final DatasetAppender dsa = new DatasetAppender();
		dsa.appendRight(tickets, fees, idKeyMap);
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);
		//
		crr.setStreamResult(new StreamResult(System.out));
		crr.setDataset(tickets);
		cr.process();
	}

	@SuppressWarnings("serial")
	@Test(expected = DatasetOperationException.class)
	public void test6() {
		logger.debug("test6");
		final Map<String, String> idKeyMap = new TreeMap<String, String>() {
			{
				put("STATE", "STATE");
				put("CITY", "CITY");
				put("MONTH", "MONTH");
			}
		};
		final DatasetAppender dsa = new DatasetAppender();
		dsa.setRequireSourceRowForEachTargetRow(true);
		dsa.appendRight(tickets, fees, idKeyMap);

		// CsvRendererRequest crr = new CsvRendererRequestImpl();
		// CsvRenderer cr = new CsvRenderer();
		// cr.setRequest(crr);
		// crr.setStreamResult(new StreamResult(System.out));
		// crr.setDataSet(tickets);
		// cr.process();
	}

	/*
	 * Take a dataset, crosstab it and then right append to the crosstabbed result
	 */
	@SuppressWarnings("serial")
	// TODO fix
	@Ignore
	@Test
	public void test7() {
		logger.debug("test7");
		final Map<String, String> idKeyMap = new TreeMap<String, String>() {
			{
				put("STATE", "STATE");
				put("CITY", "CITY");
				put("MONTH", "MONTH");
			}
		};
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		final List<String> rowId = toList("STATE", "CITY");
		final List<String> cellId = toList("Fine", "Legal Fee");

		final CrosstabColumns ctc = new CrosstabColumns(rowId, "MONTH", cellId);
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(fees);

		final MutableDataset crosstabbed = (MutableDataset) coke.getDataSet();
		final DatasetAppender dsa = new DatasetAppender();
		dsa.appendRight(crosstabbed, fees, idKeyMap);

	}

	// TODO fix this test
	@Ignore
	@Test
	public void test8() throws IOException {
		logger.debug("test8");
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		crr.setDataset(getConvictions());
		crr.setStreamResult(new StreamResult(System.out));
		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);
		cr.process();

	}

	@Ignore
	@Test
	public void test9() throws IOException {
		logger.debug("test9");
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		crr.setDataset(getConvictions());
		crr.setStreamResult(new StreamResult(System.out));
		// final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		crr.setDateFormat("yyyy-MM-dd");
		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);
		cr.process();

	}

	@Ignore
	@Test
	public void test10() throws IOException {
		logger.debug("test10");
		final CsvRendererRequest crr = new CsvRendererRequestImpl();
		crr.setDataset(getConvictions());
		crr.setStreamResult(new StreamResult(System.out));
		// final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		crr.setDateFormat("yyyy/MM/dd");
		final CsvRenderer cr = new CsvRenderer();
		cr.setRequest(crr);
		cr.process();
		// TODO what are we testing here?
	}

	@Test
	public void test11() {
		logger.debug("test11");
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Date d = new Date();
		logger.debug("it is now " + sdf.format(d));

	}

}
