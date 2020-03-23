package org.javautil.oracle.trace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class TraceFileProcessorTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private TraceFileProcessor processor;
	private final FormatCursors formatter = new FormatCursors();
	private final FormatCursorAggregate aggFormatter = new FormatCursorAggregate();

	
	//@Test  todo restore with a trace file
	public void test1() throws IOException {
		FileOutputStream fos = new FileOutputStream("target/ouput.text");
		FormatCursorsTkprofStyle tkprof = new FormatCursorsTkprofStyle(fos);
		processor = new TraceFileProcessor(
				"src/test/resources/DEV11F_ora_13468.trc");
		processor.process();
		Cursors cursors = processor.getCursors();
		// HashMap<Integer, Cursor> cursorMap = cursors.getByCursorNumber();
		List<Cursor> allCursors = cursors.getCursors();
		logger.info("allCursors count " + allCursors.size());

		logger.info(formatter.format(cursors));
		logger.info("========= Summary =========");
		logger.info(aggFormatter.format(processor.getCursorAggregates()));
		formatter.format(cursors);
		tkprof.format(processor.getCursorAggregates());
		fos.close();
	}
}
