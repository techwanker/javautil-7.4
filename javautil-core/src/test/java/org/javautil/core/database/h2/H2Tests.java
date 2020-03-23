package org.javautil.core.database.h2;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.junit.Test;

public class H2Tests {

	public H2Tests() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void dropAllObjectsTest() throws Exception {
		DataSource dataSource = DataSourceFactory.getH2Permanent("../target/H2Tests.dbf", "sa", "tutorial");
		;
		Connection connection = dataSource.getConnection();
		assertNotNull(connection);
//		File f = new File("../target/tmp/test.dbf.mv.db");
//		assertTrue (f.exists());
//		Statement s = connection.createStatement();
//		s.execute("drop all objects delete files");
//		assertTrue (f.exists());
//		s.execute("create table a (b number(9))");
//		connection.commit();
//		s.close();
//		connection.close();
//		assertTrue (f.exists());
//		ClosesableDataSource.close(dataSource);
//		assertFalse (f.exists());
	}
}
