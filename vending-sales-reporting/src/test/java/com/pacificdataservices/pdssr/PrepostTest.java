package com.pacificdataservices.pdssr;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.javautil.core.sql.SqlStatements;
import org.javautil.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrepostTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void loadSqlStatementsTest() throws IOException {

		InputStream is = getClass().getResourceAsStream("prepost_dml.yaml");
		String dml = IOUtils.inputStreamToString(is);
		logger.debug("dml:\n{}", dml);
		Prepost prepost = new Prepost();
		SqlStatements sqlStatements = prepost.loadDml();
		assertNotNull(sqlStatements);

	}

}
