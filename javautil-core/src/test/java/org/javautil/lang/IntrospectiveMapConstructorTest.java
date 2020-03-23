package org.javautil.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

import org.javautil.core.workbook.BindDefinitions;
import org.javautil.core.workbook.WorkbookDefinition;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntrospectiveMapConstructorTest {
	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	@Ignore
	@Test
	public void t1() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		String name = "DataLoadWorkbook";
		map.put("name", name);
		map.put("description", "Workbook for Dataload");

		LinkedHashMap<String, Object> etl_file_id = new LinkedHashMap<>();
		etl_file_id.put("bind_type_name", "long");
		map.put("bindDefinitions", etl_file_id);

		logger.info("map is:\n{}");

		WorkbookDefinition workbook = new WorkbookDefinition();
		IntrospectiveMapConstructor erector = new IntrospectiveMapConstructor(workbook, map);
		erector.populate();
		assertEquals(name, workbook.getName());
		BindDefinitions bindDefinitions = workbook.getBindDefinitions();
		assertNotNull(bindDefinitions);
		logger.info("bindDefinitions:\n{}", bindDefinitions);
	}
}
