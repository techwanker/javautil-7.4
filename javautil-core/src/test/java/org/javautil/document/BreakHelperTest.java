package org.javautil.document;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetMetadataImpl;
import org.javautil.dataset.MatrixDataset;
import org.junit.Before;
import org.junit.Test;

public class BreakHelperTest {

	private BreakHelper breaks;

	@Before
	public void withup() throws Exception {
		breaks = new BreakHelper();
		final Map<String, Integer> mapping = new HashMap<String, Integer>();
		mapping.put("yr", 0);
		mapping.put("per", 1);
		mapping.put("mth", 2);
		mapping.put("invoice_no", 3);
		mapping.put("customer_id", 4);
		mapping.put("customer_descr", 4);
		mapping.put("product_id", 5);
		mapping.put("product_descr", 6);
		mapping.put("cases", 7);
		mapping.put("dollars", 8);
		breaks.setColumnNameIndex(mapping);
		breaks.setBreaks(new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("yr");
				add("per");
				add("mth");
			}
		});
		breaks.afterPropertiesSet();
	}

	@Test
	public void testGetBreakLevelDatawith() throws Exception {
		breaks = new BreakHelper();
		breaks.setLowerCase(true);
		final DatasetMetadataImpl metadata = new DatasetMetadataImpl();
		metadata.addColumn(new ColumnMetadata().withColumnName("yr").withColumnIndex(1).withDataType(DataType.INTEGER)
		    .withPrecision(8).withScale(0));

		metadata.addColumn(new ColumnMetadata().withColumnName("per").withColumnIndex(2).withDataType(DataType.INTEGER)
		    .withPrecision(1).withScale(0));
		metadata.addColumn(new ColumnMetadata().withColumnName("mth").withColumnIndex(3).withDataType(DataType.INTEGER)
		    .withPrecision(2).withScale(0));
		metadata.addColumn(new ColumnMetadata().withColumnName("dollar").withColumnIndex(4).withDataType(DataType.DOUBLE)
		    .withPrecision(14).withScale(2));
		final Dataset dataset = new MatrixDataset(metadata);
		breaks.setDataset(dataset);
		breaks.setBreaks(new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("yr");
				add("per");
				add("mth");
			}
		});
		breaks.afterPropertiesSet();
		final Object[] currentRow = new Object[] { 2008, 4, 12, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(-1, breakLevel);
	}

	@Test
	public void testGetBreakLevelNoBreak() {
		final Object[] currentRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(-1, breakLevel);
	}

	@Test
	public void testGetBreakLevelOnSameToStringButDifferentDataType() {
		// 2008 is a String and an Integer
		final Object[] currentRow = new Object[] { "2008", 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(1, breakLevel);
	}

	@Test
	public void testGetBreakLevelBreakOnFirstColumn() {
		final Object[] currentRow = new Object[] { 2007, 4, 11, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(1, breakLevel);
	}

	@Test
	public void testGetBreakLevelBreakOnFirstSecondAndThirdColumns() {
		final Object[] currentRow = new Object[] { 2007, 3, 8, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(1, breakLevel);
	}

	@Test
	public void testGetBreakLevelBreakOnSecondAndThirdColumn() {
		final Object[] currentRow = new Object[] { 2008, 3, 8, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(2, breakLevel);
	}

	@Test
	public void testGetBreakLevelBreakOnThirdColumn() {
		final Object[] currentRow = new Object[] { 2008, 4, 11, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Mini Donuts, 6pk", 1, 35.50 };
		final Object[] previousRow = new Object[] { 2008, 4, 12, "INV_90210", "12345", "Seven Eleven", "98765",
		    "Slim Jim, 10\"", 1, 42.97 };
		final int breakLevel = breaks.getBreakLevel(previousRow, currentRow);
		assertEquals(3, breakLevel);
	}
}
