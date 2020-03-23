package org.javautil.dataset;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import org.javautil.document.style.HorizontalAlignment;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnMetadataCsvMarshallerTest extends BaseTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ColumnMetadata getColumnMetadata() {
		final ColumnMetadata cm = new ColumnMetadata();
		cm.setColumnName("COL_NM");
		cm.setColumnIndex(0);
		cm.setDataTypeName("INTEGER");
		cm.setHeading("col nm");
		cm.setLabel("Column Name");
		cm.setPrecision(9);
		cm.setScale(1);
		cm.setColumnDisplaySize(10);
		cm.setComments("the name name of a column");
		cm.setExternalFlag(false);
		cm.setAttributeName("colNm");
		cm.setWorkbookFormula("workbookFormula");
		cm.setExcelFormat("###,###.#");
		cm.setJavaFormat("###.##");
		cm.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		cm.setAggregateFunction("sum");
		return cm;
	}

	// todo fix

	@Ignore
	@Test
	public void test2() throws IOException {
		final ColumnMetadata cm = getColumnMetadata();
		ArrayList<ColumnMetadata> list = new ArrayList<>();
		list.add(cm);

		final File f = new File("target/tmp/cm.csv");
		final Writer w = new FileWriter(f);
		final ColumnMetadataSerializerCsv marshallOut = new ColumnMetadataSerializerCsv(list);
		marshallOut.write(w);
		w.close();
		final Reader r = new FileReader(f);
		final ColumnMetadataSerializerCsv marshallIn = new ColumnMetadataSerializerCsv(r);
		final ArrayList<ColumnMetadata> cm2list = marshallIn.readAll();
		final ColumnMetadata cm2 = cm2list.get(0);

		assertEquals(cm.getColumnName(), cm2.getColumnName());
		assertEquals(cm.getColumnIndex(), cm2.getColumnIndex());
		assertEquals(cm.getDataType(), cm2.getDataType());
		assertEquals(cm.getHeading(), cm2.getHeading());
		assertEquals(cm.getLabel(), cm2.getLabel());
		assertEquals(cm.getPrecision(), cm2.getPrecision());
		assertEquals(cm.getScale(), cm2.getScale());
		assertEquals(cm.getColumnDisplaySize(), cm2.getColumnDisplaySize());
		assertEquals(cm.getComments(), cm2.getComments());
		assertEquals(cm.isExternalFlag(), cm2.isExternalFlag());
		assertEquals(cm.getAttributeName(), cm2.getAttributeName());
		assertEquals(cm.getWorkbookFormula(), cm2.getWorkbookFormula());
		assertEquals(cm.getExcelFormat(), cm2.getExcelFormat());
		assertEquals(cm.getJavaFormat(), cm2.getJavaFormat());
		assertEquals(cm.getHorizontalAlignment(), cm2.getHorizontalAlignment());
		assertEquals(cm.getAggregateFunction(), cm2.getAggregateFunction());
		assertEquals(cm, cm2);
		logger.debug(cm.toString());
	}

}