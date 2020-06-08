package org.javautil.core.sql;

import static org.junit.Assert.assertNotNull;

import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;

public class TestDataSourceTest {

	@Test
	public void testOracle() throws SQLException, PropertyVetoException, IOException {
		DataSource ds = TestDataSource.getDataSource(Dialect.ORACLE);
		assertNotNull(ds);
		((Closeable) ds).close();
	}
	@Test
	public void testPostgres() throws SQLException, PropertyVetoException, IOException {
		DataSource ds = TestDataSource.getDataSource(Dialect.POSTGRES);
		assertNotNull(ds);
		((Closeable) ds).close();
	}
	@Test
	public void testH2() throws SQLException, PropertyVetoException, IOException {
		DataSource ds = TestDataSource.getDataSource(Dialect.H2);
		assertNotNull(ds);
		((Closeable) ds).close();
	}
	
}
