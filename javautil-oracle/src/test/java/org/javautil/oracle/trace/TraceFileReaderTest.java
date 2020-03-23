package org.javautil.oracle.trace;

import java.io.IOException;

import org.javautil.oracle.trace.record.Record;
import org.junit.Test;

public class TraceFileReaderTest {

	private TraceFileReader reader;
	private Record record;

	//@Test TODO need a trace file this should test something
	public void test1() throws IOException {
		reader = new TraceFileReader("src/test/resources/DEV11F_j011_24148.trc");
		while ((record = reader.getNext()) != null) {

		}
	}

}
