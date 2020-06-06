package org.javautil.oracle.trace;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.oracle.OracleConnectionHelper;
import org.javautil.oracle.tracehandlers.OracleTraceProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleTraceProcessorTest extends BaseTest { 

	// TODO !!! handle errors
		private transient Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test() throws IOException {
		OracleTraceProcessor otp = new OracleTraceProcessor("src/test/resources/oratrace/dev18b_ora_813.trc");
		otp.process();
	}

	@Test
	public void test2() throws IOException {
		OracleTraceProcessor otp = new OracleTraceProcessor("src/test/resources/oratrace/dev18b_ora_6884_job_1.trc");
		otp.process();
	}
	
	@Test
	public void testConnectionRead() throws SQLException, IOException {
		Connection conn = getAppConnection();
		OracleConnectionHelper.enableTrace(conn, true, false);
		String tracefilePath = OracleConnectionHelper.getMyTraceFileName(conn);
		logger.debug("tracefilePath {}",tracefilePath);
		File traceFileFile = new File(tracefilePath);
		assert(traceFileFile.canRead());
		String traceFileName = traceFileFile.getName();
		OracleTraceProcessor otp = new OracleTraceProcessor(conn,traceFileName);
		
		otp.process();
	}
}
