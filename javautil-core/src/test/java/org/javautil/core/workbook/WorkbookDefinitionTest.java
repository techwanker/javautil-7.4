package org.javautil.core.workbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.javautil.core.text.YamlUtils;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class WorkbookDefinitionTest {
	private Logger logger               = LoggerFactory.getLogger(getClass());
	private String definitionsDirectory = WorkbookTestConstants.definitionsDirectory;

	@Test
	public void testDataloadWorkbookMeta() throws JsonParseException, JsonMappingException, IOException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String name = "DataLoadWorkbookMeta.yaml";
		InputStream is = new FileInputStream(definitionsDirectory + name);
		assertNotNull(is);

		WorkbookDefinition wd = WorkbookDefinition.getWorkbookDefinition(is);
		logger.info("wd is {}", wd);
		LinkedHashMap<String, ColumnMetadata> columns = wd.getColumns();
		assertNotNull(columns);

		ColumnMetadata extNet = columns.get("extended_net_amt");
		logger.info("extNet: {}", extNet);
		assertNotNull(extNet);
		assertNotNull(extNet.getDataType());
		assertEquals(DataType.NUMERIC, extNet.getDataType());
		assertEquals("#,###.00;[RED]-#,###.00", extNet.getExcelFormat());
		assertNotNull(extNet.getDataTypeName());

	}

	@Ignore // TODO why does this not work
	@Test
	public void testDataloadWorkbookFromResource() throws JsonParseException, JsonMappingException, IOException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String name = "DataLoadWorkbook.yaml";
		InputStream is = getClass().getClassLoader().getResourceAsStream(name);
		assertNotNull(is);
		WorkbookDefinition wd = WorkbookDefinition.getWorkbookDefinition(is);
		logger.info("wd is {}", wd);
	}

	@Test
	public void testDataloadWorkbookFromSrcSheets() throws JsonParseException, JsonMappingException, IOException,
	    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String name = "DataLoadWorkbook.yaml";
		InputStream is = new FileInputStream(definitionsDirectory + name);
		assertNotNull(is);

		WorkbookDefinition wd = WorkbookDefinition.getWorkbookDefinition(is);
		logger.info("wd is {}", wd);
		LinkedHashMap<String, ColumnMetadata> columns = wd.getColumns();
		assertNotNull(columns);
	}

	@Test
	public void metaTest() throws JsonParseException, JsonMappingException, IOException, PropertyVetoException,
	    SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		File file = new File("src/test/resources/workbook/DataLoadWorkbookMeta2.yaml");
		logger.info("processing file: '{}'", file.getAbsoluteFile());
		WorkbookDefinition wd = WorkbookDefinition.getWorkbookDefinition(file);
		logger.info("input file: {}\n{}", file.getAbsoluteFile(), wd.getFileAsString());
		logger.info("WorkbookDefinition asYaml:\n{}", YamlUtils.asYaml(wd));
	}
}
