package org.javautil.core.sql;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.text.SimpleDateFormatFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2FileDatabaseTest {
	private static final Logger logger = LoggerFactory.getLogger(H2FileDatabaseTest.class);

	@Test
	public void test() throws ClassNotFoundException, SQLException {
		// Date d = new Date();
		// SimpleDateFormat df = SimpleDateFormatFactory.getDateTimeForFileName();
		// String fname= df.format(d);
		// String fpath = "target/tmp/" + fname;
		String timestamp = SimpleDateFormatFactory.getTimestamp();
		File f = new File("target/tmp/" + timestamp);
		logger.debug("h2 file " + f.getAbsolutePath());

		Connection conn = H2FileDatabase.getConnection(f, "", "");
		assertNotNull(conn);
		conn.close();
		File f2 = new File(f.getAbsolutePath() + ".mv.db");
		logger.debug("checking for file: " + f2.getAbsolutePath());
		logger.debug("file size " + f2.length());
		assertTrue(f2.exists());
	}

}
