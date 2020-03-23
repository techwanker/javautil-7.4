package org.javautil.conditionidentification;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.Dialect;
import org.junit.Test;

public class CreateDatabaseObjectsTest {

	@Test
	public void createObjectsTest() throws SQLException, IOException {
		Connection conn = DataSourceFactory.getInMemoryDataSourceSingletonConnection();
		CreateUtConditionDatabaseObjects creator = new CreateUtConditionDatabaseObjects(conn, Dialect.H2, true);
		creator.process();
		// TODO check the schema
	}
}
