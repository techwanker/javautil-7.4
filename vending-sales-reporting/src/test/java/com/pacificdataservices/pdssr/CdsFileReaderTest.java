package com.pacificdataservices.pdssr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.javautil.util.ListOfNameValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdsFileReaderTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// @Test TODO why does this not work
	public void loadDefinitionsTest() throws IOException {
		CdsFileReader cfr = new CdsFileReader();
		HashMap<String, ListOfNameValue> recordDefs = cfr.getRecordDefinitionsByRecordType();
		assertNotNull(recordDefs);
		logger.info("recordDefs.size {}", recordDefs.size());
		assertEquals(6, recordDefs.size());
	}

	@Test
	public void testRun() throws ParseException, IOException {
		CdsFileReader cfr = new CdsFileReader("src/test/resources/dataloads/201502.cds");
		Map<String, Object> binds = null;
		while ((binds = cfr.readLine()) != null) {
			logger.debug(binds.toString());
		}
	}

}
