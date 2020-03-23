package com.pacificdataservices.pdssr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;

import org.javautil.util.ListOfNameValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdsReportingFileMetadataTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testDefinitions() throws IOException {
		CdsFileReader cfr = new CdsFileReader();
		HashMap<String, ListOfNameValue> recordDefs = cfr.getRecordDefinitionsByRecordType();
		assertNotNull(recordDefs);
		logger.info("recordDefs.size {}", recordDefs.size());
		assertEquals(6, recordDefs.size());
	}
}
