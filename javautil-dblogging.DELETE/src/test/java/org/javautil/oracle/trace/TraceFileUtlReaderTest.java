package org.javautil.oracle.trace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.io.IOException;

import org.javautil.io.Tracer;
import org.javautil.oracle.trace.formatter.OracleTraceReport;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.tracehandlers.OracleTraceProcessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceFileUtlReaderTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// @Test
	public void test1() throws IOException {
		String inputFileName = "src/test/resources/oratrace/job1.trc";
		OracleTraceProcessor tfr = new OracleTraceProcessor(inputFileName);
		tfr.process();
		CursorsStats cursorStats = tfr.getCursors();
		CursorInfo cursorStat = cursorStats.getCursorInfoByCursorId("2xcqaktjqwp1h");
		assertNotNull(cursorStat);
		Parsing parsing = cursorStat.getParsing();
		assertNotNull(parsing);
		// logger.debug(parsing.toString());
		String expected = "insert into etl_file ( etl_file_id, RPT_ORG_ID, DATAFEED_ORG_ID) values ( :etl_file_id, :rpt_org_id, :org_id)";
		assertEquals(expected, parsing.getSqlText().trim());
		OracleTraceReport formatter = new OracleTraceReport(System.out);
		formatter.process(false);
	}

	// @Test
	public void test2() throws IOException {

		String inputFileName = "src/test/resources/oratrace/dev12c_ora_10581_job_6.trc";
		FileOutputStream fos = new FileOutputStream(
				"target/test-classes/oratrace/dev12c_ora_10581_job_6.dblogging.prf");
		Tracer tracer = new Tracer("target/test-classes/oratrace/dev12c_ora_10581_job_6.dblogging.trace");
		OracleTraceProcessor tfr = new OracleTraceProcessor(inputFileName);
		tfr.setTracer(tracer);
		tfr.process();
		OracleTraceReport formatter = new OracleTraceReport(fos);
		formatter.format(tfr.getCursors(), false);
		fos.close();
		tracer.close();
	}

	@Test
	public void test3() throws IOException {
		String base = "dev18b_ora_813";
		String inputFileName = "src/test/resources/oratrace/" + base + ".trc";

		FileOutputStream fos = new FileOutputStream("target/test-classes/oratrace/" + base + ".juf");
		Tracer tracer = new Tracer("target/test-classes/oratrace/" + base + ".trace");
		OracleTraceProcessor tfr = new OracleTraceProcessor(inputFileName);
		tfr.setTracer(tracer);
		tfr.process();

		OracleTraceReport formatter = new OracleTraceReport(fos);
		logger.debug(tfr.getCursors().toString());
		formatter.format(tfr.getCursors(), false);
		fos.close();
		tracer.close();
	}
}