/**
 *  https://dzone.com/articles/java-string-format-examples
 */
package org.javautil.dataset;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.javautil.document.style.HorizontalAlignment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**

 *
 */
public class ColumnMetadataTest {

	public static ColumnMetadata dollarsColumn;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		instantiateAndPopulateDollarsColumn();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	static {
		instantiateAndPopulateDollarsColumn();
	}

//	public static void testDollarsColumn(ColumnMetadata dollarsColumn) {
//		assertEquals("Dollars", dollarsColumn.getColumnName());
//		assertEquals(DataType.NUMERIC, dollarsColumn.getDataType());
//		assertEquals(new Integer(12), dollarsColumn.getPrecision());
//		assertEquals(new Integer(2), dollarsColumn.getScale());
//		assertEquals(new Integer(13), dollarsColumn.getColumnDisplaySize());
//		assertEquals(HorizontalAlignment.RIGHT, dollarsColumn.getHorizontalAlignment());
//		assertEquals("###,##9.99", dollarsColumn.getExcelFormat());
//		assertEquals("###,###,##9.99", dollarsColumn.getJavaFormat());
//	}

	public static void instantiateAndPopulateDollarsColumn() {
//		dollarsColumn = new ColumnMetadata("Dollars", 1, DataType.NUMERIC, 12, 2, 13, HorizontalAlignment.RIGHT,
//				"###,##9.99", "###,###,##9.99");
		dollarsColumn = new ColumnMetadata().withColumnName("Dollars").withColumnIndex(1).withPrecision(12).withScale(2)
		    .withColumnDisplaySize(13).withHorizontalAlignment(HorizontalAlignment.RIGHT).withExcelFormat("###,##9.99")
		    .withJavaFormat("###,###,##9.99");
		// k testDollarsColumn(dollarsColumn);
	}

	public static void beforeClass() {

		// final ColumnMetadata dollarsColumn = new ColumnMetadata("Dollars", 1,
		// DataType.NUMERIC, 12, 2, 13, HorizontalAlignment.RIGHT,
		// "###,##9.99", "###,###,##9.99");
		assertEquals("Dollars", dollarsColumn.getColumnName());
//		assertEquals(DataType.NUMERIC, dollarsColumn.getDataType());
//		assertEquals(new Integer(12), dollarsColumn.getPrecision());
//		assertEquals(new Integer(2), dollarsColumn.getScale());
//		assertEquals(new Integer(13), dollarsColumn.getColumnDisplaySize());
//		assertEquals(HorizontalAlignment.RIGHT, dollarsColumn.getHorizontalAlignment());
//		assertEquals("###,##9.99", dollarsColumn.getExcelFormat());
//		assertEquals("###,###,##9.99", dollarsColumn.getJavaFormat());
	}

	@Test
	public void toListAndBack() {
		ArrayList<Object> strings = dollarsColumn.toObjectList();
		ColumnMetadata constructed = ColumnMetadata.fromObjectList(strings);
		assert (dollarsColumn.equals(constructed));
	}

	@Test
	public void ColumnMetadataTest3() {
//		final ColumnMetadata cm1 = new ColumnMetadata("TEST_COL_1", 1, DataType.NUMERIC, 12, 2, 12,
//				HorizontalAlignment.RIGHT, "###,##9.99", "###,###,##9.99");

		final String columnName = "TEST_COL_1";
		final String attributeName = "Attribute";
		final int precision = 12;
		final String comments = "Test Column";
		final boolean externalFlag = false;
		final String groupName = "Group1";
		final String label = "Test Column 1";
		final String heading = "Test Column 1";
		final String formula = "SUM(X1:Y1)";
		final String aggregateFunction = "SUM";
		final String excelFormat = "###,##9.99";
		final String javaFormat = "%9f";
		final int scale = 2;
		final HorizontalAlignment ha = HorizontalAlignment.RIGHT;

		final ColumnMetadata cm1 = new ColumnMetadata().withColumnName(columnName).withColumnIndex(1)
		    .withAttributeName(attributeName).withColumnDisplaySize(precision).withComments(comments)
		    .withExternalFlag(externalFlag).withDataType(DataType.NUMERIC).withGroupName(groupName).withHeading(heading)
		    .withLabel("Test Column 1").withWorkbookFormula(formula).withHorizontalAlignment(ha)
		    .withAggregateFunction(aggregateFunction).withExcelFormat(excelFormat).withJavaFormat(javaFormat)
		    .withPrecision(precision).withScale(scale);

		assertEquals(columnName, cm1.getColumnName());
		assertEquals(1, cm1.getColumnIndex().intValue());
		assertEquals(cm1.getDataType().toString(), "NUMERIC");
		assertEquals(aggregateFunction, cm1.getAggregateFunction());
		assertEquals(attributeName, cm1.getAttributeName());
		assertEquals(comments, cm1.getComments());
		assertEquals(excelFormat, cm1.getExcelFormat());
		assertEquals(groupName, cm1.getGroupName());
		assertEquals(cm1.getHeading(), "Test Column 1");
		assertEquals(label, cm1.getLabel());
		assertEquals("RIGHT", cm1.getHorizontalAlignment().toString());
		assertEquals(javaFormat, cm1.getJavaFormat());
		assertEquals(formula, cm1.getWorkbookFormula());
		assertEquals(cm1.getColumnDisplaySize().intValue(), 12);
		assertEquals(precision, cm1.getPrecision().intValue());
		assertEquals(scale, cm1.getScale().intValue());
		assertEquals(false, cm1.isExternalFlag());

	}

	@Test
	public void ColumnMetadataTest4() {
		final List<String> args = new ArrayList<String>();
		int indx = 0;
		args.add(indx++, "TEST_COL_1"); // Column Name
		args.add(indx++, "1"); // Column Index
		args.add(indx++, "NUMERIC"); // Data Type
		args.add(indx++, "Heading"); // Column Heading
		args.add(indx++, "Label"); // Column Label
		args.add(indx++, "12"); // Column Precision
		args.add(indx++, "2"); // Column Scale
		args.add(indx++, "12"); // Display Length
		args.add(indx++, "Comments"); // Comments
		args.add(indx++, "N"); // External Flag
		args.add(indx++, "Attribute"); // Attribute Name
		args.add(indx++, "SUM(A1:A100)"); // Workbook Formula
		args.add(indx++, "###,##0.00"); // Excel Format
		args.add(indx++, "###,###,##9.99"); // Java Format
		args.add(indx++, "Group"); // Group Name
		args.add(indx++, "RIGHT"); // Horizontal Alignment
		args.add(indx++, "SUM"); // Aggregate Function

		final ColumnMetadata cm1 = ColumnMetadata.fromStringList(args);
		cm1.setInjectedGroupField(false);

		Assert.assertTrue(cm1 != null);
		Assert.assertEquals(cm1.getColumnName(), "TEST_COL_1");
		Assert.assertEquals(cm1.getColumnIndex().intValue(), 1);
		Assert.assertEquals(cm1.getDataType().toString(), "NUMERIC");
		Assert.assertEquals(cm1.getHeading(), "Heading");
		Assert.assertEquals(cm1.getLabel(), "Label");
		Assert.assertEquals(cm1.getPrecision().intValue(), 12);
		Assert.assertEquals(cm1.getScale().intValue(), 2);
		Assert.assertEquals(cm1.getColumnDisplaySize().intValue(), 12);
		Assert.assertEquals(cm1.getComments(), "Comments");
		Assert.assertEquals(cm1.isExternalFlag(), false);
		Assert.assertEquals(cm1.getAttributeName(), "Attribute");
		Assert.assertEquals(cm1.getWorkbookFormula(), "SUM(A1:A100)");
		Assert.assertEquals(cm1.getExcelFormat(), "###,##0.00");
		Assert.assertEquals(cm1.getJavaFormat(), "###,###,##9.99");
		Assert.assertEquals(cm1.getGroupName(), "Group");
		Assert.assertEquals(cm1.getHorizontalAlignment().toString(), "RIGHT");
		Assert.assertEquals(cm1.getAggregateFunction(), "SUM");
		Assert.assertEquals(cm1.isInjectedGroupField(), false);

	}
}
