package org.javautil.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javautil.dataset.csv.DatasetCsvMarshaller;
import org.javautil.document.crosstab.CrosstabColumns;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs @ dbexperts
 * 
 */
public class CrosstabDataset2Test extends BaseTest {
	public Logger               logger  = LoggerFactory.getLogger(getClass());

	ArrayList<Object> asList(final Object... o) {
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
	public void testOeCustMfr() {

		final DatasetMetadataImpl meta = new DatasetMetadataImpl() {
			{
				addColumn(new ColumnMetadata().withColumnName("itemCd").withColumnIndex(0).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("custCd").withColumnIndex(1).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("mfrCd").withColumnIndex(2).withDataType(DataType.STRING));
				addColumn(new ColumnMetadata().withColumnName("restrict").withColumnIndex(3).withDataType(DataType.STRING));
//				addColumn(new ColumnMetadata("itemCd", 0, DataType.STRING, null, null, null));
//				addColumn(new ColumnMetadata("custCd", 1, DataType.STRING, null, null, null));
//				addColumn(new ColumnMetadata("mfrCd", 2, DataType.STRING, null, null, null));
//				addColumn(new ColumnMetadata("restrict", 3, DataType.STRING, null, null, null));
			}
		};

		final MatrixDataset ds = new MatrixDataset(meta) {
			private static final long serialVersionUID = 1061400049859785367L;
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
		logger.debug("dsString:\n{}", dsString);
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
		logger.debug("resultString:\n{}", resultString);

		int rowCount = result.getRowCount();
		int colCount = result.getMetadata().getColumnCount();

		assertEquals("custCd", result.getMetadata().getColumnMetaData(0).getColumnName());
		assertEquals("06725", result.getMetadata().getColumnMetaData(1).getColumnName());
		assertEquals(3, rowCount);
		assertEquals(12, colCount);
		Collection<String> columnNames = result.getColumnNames();
		assertNotNull(columnNames);

		DatasetIterator<Object> di = result.getDatasetIterator();
		di.next();
		List<Object> row = di.getRowAsList();
		assertEquals(12, row.size());
		assertEquals("135701", row.get(0));
		assertNull(row.get(11));
		MutableDatasetMetadata md = result.getMetadata();
		for (ColumnMetadata cm : md.getColumnMetadata()) {
			logger.debug(cm.toStringBrief());
		}
	}

}
