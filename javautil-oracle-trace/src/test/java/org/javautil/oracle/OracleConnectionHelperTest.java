package org.javautil.oracle;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.oracle.trace.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleConnectionHelperTest extends BaseTest{

	private transient Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void traceFileSystemDirTest() throws SQLException {
		Connection conn = getAppConnection();
		String dirName = OracleConnectionHelper.getFileSystemDirectoryForTraceFiles(conn);
		assertNotNull(dirName);
		logger.warn("trace dir {}",dirName);
		File f = new File(dirName);
		assertTrue(f.exists());
	}
	
	public void dbmsTraceDir() throws SQLException {
		Connection conn = getAppConnection();
		 String dirName = OracleConnectionHelper.getDBMSDirectoryForTraceFiles(conn);
		 assertNotNull(dirName);
	}

}