package org.javautil.conditionidentification;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionIdentificationTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test1() {
		String path = "org/javautil/conditionidentification/UtConditionPersistenceDml.yaml";
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		logger.debug(file.getAbsolutePath());
		assertTrue(file.exists());
	}

}
