package org.javautil.conditionidentification;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.javautil.core.sql.SqlStatements;
import org.junit.Test;

public class ConditionIdentificationPersistenceTest {

	@Test
	public void loadDmlTest() throws IOException {
		ConditionIdentificationPersistence cip = new ConditionIdentificationPersistence();
		SqlStatements statements = cip.loadDml();
		assertNotNull(statements);
	}
}
