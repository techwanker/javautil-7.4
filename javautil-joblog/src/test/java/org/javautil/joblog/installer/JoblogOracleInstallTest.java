package org.javautil.joblog.installer;

import java.io.Closeable;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.DataSourceFactory;
import org.junit.Test;

public class JoblogOracleInstallTest {

	// TODO WTF

	@Test
	public void testDrop() throws Exception  {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDatasource("integration_oracle");
		Connection loggerConnection = ds.getConnection();
		JoblogOracleInstall installer = new JoblogOracleInstall(loggerConnection, true, true);
		installer.drop();
		installer.process();
		loggerConnection.close();
		((Closeable) ds).close();
	}

//	public static void ensureLoggerPackage(Connection connection) throws SQLException {
//		String sql = "select object_type, status from user_objects\n" + 
//				"where object_name = 'LOGGER'";
//
//		SqlStatement ss = new SqlStatement(connection,sql);
//		NameValue nv = ss.getNameValue();
//		assertEquals("VALID",nv.get("status"));
//		ss.close();	
//	}
}