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

	

	@Test
	public void test3() throws IOException {
		String base = "dev18b_ora_813";
		String inputFileName = "src/test/resources/oratrace/" + base + ".trc";

		FileOutputStream fos = new FileOutputStream("target/oratrace/" + base + ".juf");
		Tracer tracer = new Tracer("target/oratrace/" + base + ".trace");
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
