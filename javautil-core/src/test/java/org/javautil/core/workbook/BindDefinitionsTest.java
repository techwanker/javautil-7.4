package org.javautil.core.workbook;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class BindDefinitionsTest {
	private transient final Logger logger               = LoggerFactory.getLogger(getClass());
	private String                 definitionsDirectory = WorkbookTestConstants.definitionsDirectory;

	@Test
	public void loadBindDefinitionsTest() throws FileNotFoundException {
		String name = "BindDefinitions.yaml";
		FileInputStream fis = new FileInputStream(definitionsDirectory + name);
		Yaml yaml = new Yaml();
		BindDefinitions bd = null;
		bd = yaml.loadAs(fis, BindDefinitions.class);
		logger.info("BindDefinitions: {}", bd);
		assertNotNull(bd);
	}
}
