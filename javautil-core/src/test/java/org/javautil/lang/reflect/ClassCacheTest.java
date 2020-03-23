package org.javautil.lang.reflect;

import static org.junit.Assert.assertNotNull;

import org.javautil.core.workbook.WorkbookDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassCacheTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void t1() {
		WorkbookDefinition wd = new WorkbookDefinition();
		ClassCache cache = new ClassCache(wd);
		FieldCache fieldCache = cache.getFields();
		assertNotNull(fieldCache);
		logger.info("fields:\n{}", fieldCache.toString());
	}
}
