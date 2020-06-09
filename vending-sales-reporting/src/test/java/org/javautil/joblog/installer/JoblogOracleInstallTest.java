
package org.javautil.joblog.installer;
import static org.junit.Assert.assertNotNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.TestDataSource;
import org.javautil.io.ResourceHelper;
import org.junit.Test;

public class JoblogOracleInstallTest {

	public static final	String resourceName =  "ddl/oracle/logger_tables.sr.sql";
//	@Test
//	public void getResource() throws IOException {
//		
//		File f  = ResourceHelper.getResourceAsFile(this, resourceName);
//		assertNotNull(f);
//		System.out.println("f " + f);
//		FileInputStream fis = new FileInputStream(f);
//		assertNotNull(fis);
//		fis.close();
//	}
	@Test
	public void getResourceAsStream() throws IOException {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
		assertNotNull(is);
		is.close();
	}
	
	//@Test
	public void testInstall() throws Exception  {
		DataSource ds = TestDataSource.getDataSource(Dialect.ORACLE);
		Connection loggerConnection = ds.getConnection();
		JoblogOracleInstall installer = new JoblogOracleInstall(loggerConnection, true, true);
		installer.process();
		loggerConnection.close();
		((Closeable) ds).close();
	}
}