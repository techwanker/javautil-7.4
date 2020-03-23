/**
 * @(#) RecordFactory.java
 */
package org.javautil.oracle.trace;

import java.io.IOException;

import org.javautil.oracle.trace.record.CursorRecord;
import org.javautil.oracle.trace.record.Record;
import org.javautil.oracle.trace.record.Xctend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO put fileName in trace File
 */
public class TraceFileProcessor {
	private final TraceFileReader reader;
	private Record record = null;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private int recordCount;
	private final Cursors cursors = new Cursors();
	private final CursorAggregates cursorAggregates = new CursorAggregates();
	private long startTime;
	private long endTime;
	private double elapsedSeconds;
	private final boolean aggregateOnly = true;

	public TraceFileProcessor(final String fileName) throws IOException {
		reader = new TraceFileReader(fileName);
		logger.info("processing file " + fileName);
	}

	/**
	 * Takes each statement and associates it with a cursor.
	 * 
	 * Loops through the input file add adds each statment to the corresponding
	 * cursor, which is created if necessary.
	 * 
	 * @throws IOException
	 */
	public void associateStatementsToCursors() throws IOException {
		startTime = System.nanoTime();
		long endTime;
		while ((record = reader.getNext()) != null) {
			if (!aggregateOnly) {
				processRecord(record);
			}
			if (isCursorRecord(record)) {
				aggregate((CursorRecord) record);
			}
			recordCount++;
		}
		if (logger.isInfoEnabled()) {
			endTime = System.nanoTime();
			double elapsedSeconds = (endTime - startTime) / 1e9;
			logger.info("elapsed seconds allocateStatementsToCursors " + elapsedSeconds);
		}

	}

	boolean isCursorRecord(Record record) {
		return record instanceof CursorRecord;
	}

	void aggregate(CursorRecord record) {
		cursorAggregates.aggregate(record);
		// switch (record.getRecordType()) {
		// case PARSE:
		// case EXEC:
		// case FETCH:
		// cursorAggregates.aggregate(record);
		// break;
		// case PARSING:
		// cursorAggregates.aggregate(record);
		// }
	}

	void processRecord(Record record) {
		if (record instanceof CursorRecord) {
			if (logger.isDebugEnabled()) {
				logger.debug("record is " + record.toString());
			}
			Cursor cursor = cursors.processCursorRecord((CursorRecord) record);
			((CursorRecord) record).setSqlid(cursor.getSqlId());
		} else {
			if (!(record instanceof Xctend)) {
				logger.info("not a cursor event " + record.getLineNumber() + record.getClass().getName());
			}
		}
	}

	public void process() throws IOException {
		associateStatementsToCursors();
		// populateAggregatesBySqlId();
		logger.info("records read " + recordCount);
		endTime = System.nanoTime();
		elapsedSeconds = (endTime - startTime) / 1e9;
		logger.info("elapsed seconds " + elapsedSeconds);
	}

	public Cursors getCursors() {
		return cursors;
	}

	public CursorAggregates getCursorAggregates() {
		return cursorAggregates;
	}

	// public HashMap<String, CursorAggregate> getAggregatesBySqlid() {
	// return aggregatesBySqlid;
	// }

}
