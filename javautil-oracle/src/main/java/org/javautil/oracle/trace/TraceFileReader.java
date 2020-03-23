/**
 * @(#) RecordFactory.java
 */
package org.javautil.oracle.trace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.javautil.oracle.trace.record.Exec;
import org.javautil.oracle.trace.record.Fetch;
import org.javautil.oracle.trace.record.Parse;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.trace.record.Record;
import org.javautil.oracle.trace.record.RecordType;
import org.javautil.oracle.trace.record.Stat;
import org.javautil.oracle.trace.record.Wait;
import org.javautil.oracle.trace.record.Xctend;
import org.javautil.util.EventHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TraceFileReader {
	private static EventHelper events = new EventHelper();
	private static final Integer LOG_PARSED_RECORD = Integer.valueOf(1);
	private static final Integer LOG_RAW_RECORD = Integer.valueOf(2);
	private final String fileName;
	//
	private final ArrayList<String> filePreamble = new ArrayList<String>();
	//
	private LineNumberReader br;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private boolean endOfFile = false;
	private String line;
	// private boolean needsRead;
	private final HashMap<String, String> waitTypes = new HashMap<String, String>();
	private boolean logSkippedRecords = true;
	private final boolean logSkippedTimestamp = false;
	private final boolean logSkippedSeparator = false;
	private Record record = null;
	// log events
	// private boolean logRead = false;
	private final boolean logParsingFound = false;
	private final boolean logSkipsInBind = false;
	private final boolean logInitialSkips = false;
	private int getNextCount = 0;
	private int nonSkipCount = 0;
	private int bindCount = 0;
	private int xctendCount = 0;
	private int parsingCount = 0;
	private int parseCount;
	private int execCount;
	private int waitCount;
	private int fetchCount;
	private int statCount;
	private int endOfStatementCount;
	private int ignoredCount;
	static {
		events.addEvent(LOG_PARSED_RECORD);
		events.addEvent(LOG_RAW_RECORD);
	}

	public TraceFileReader(final String fileName) throws IOException {
		this.fileName = fileName;
		initResources();
	}

	public void dispose() throws IOException {
		br.close();
	}

	public Record getNext() throws IOException {
		getNextCount++;
		record = null;
		while (!endOfFile && record == null) {
			final RecordType recordType = RecordType.getRecordType(line);
			if (recordType != null) {
				switch (recordType) {
				case BIND:
					processBinds();
					bindCount++;
					break;
				case XCTEND:
					record = new Xctend(line, br.getLineNumber());
					xctendCount++;
					break;
				case PARSING:
					record = new Parsing(line, br.getLineNumber());
					final Parsing stmt = (Parsing) record;
					while ((line = readLine()) != null) {
						if (line.equals("END OF STMT")) {
							break;
						}
						stmt.addLine(line);
					}
					parsingCount++;
					break;
				case PARSE:
					record = new Parse(line, br.getLineNumber());
					parseCount++;
					break;
				case EXEC:
					record = new Exec(line, br.getLineNumber());
					execCount++;
					break;
				case WAIT:
					record = new Wait(line, br.getLineNumber());
					final Wait wait = (Wait) record;
					wait.setWaitType(getWaitType(wait.getWaitType()));
					wait.clean();
					waitCount++;
					break;
				case FETCH:
					record = new Fetch(line, br.getLineNumber());
					fetchCount++;
					break;
				case STAT:
					record = new Stat(line, br.getLineNumber());
					statCount++;
					break;
				case END_OF_STATEMENT:
					endOfStatementCount++;
					break;
				case IGNORED:
					ignoredCount++;
					break;
				}
			}
			if (record == null && !endOfFile) {
				logSkip();
			} else {
				nonSkipCount++;
				if (events.exists(LOG_PARSED_RECORD)) {
					logger.debug(record.toString());
				}
			}

			readLine();
		}
		if (record != null) {
			logRecordType(record);
		}
		if (record == null) {
			logger.info("returning null record");
		} else if (events.exists(LOG_PARSED_RECORD)) {
			logger.debug(record.toString());
		}
		return record;
	}

	private String getWaitType(final String type) {
		String returnValue = waitTypes.get(type);
		if (returnValue == null) {
			returnValue = type;
			waitTypes.put(type, type);
		}
		return returnValue;
	}

	private void initResources() throws IOException {
		try {
			br = new LineNumberReader(new BufferedReader(new FileReader(fileName)));
			readPreamble();
		} catch (final FileNotFoundException fnfe) {
			logger.error("Cannot locate input file! " + fnfe.getMessage());
			throw fnfe;
		}
	}

	private void logRecordType(final Record rec) {
		if (rec.getRecordType() != null) {
			switch (rec.getRecordType()) {
			case PARSING:
				if (logParsingFound) {
					logger.info("found parsing");
				}
				break;
			default:
				break;
			case BIND:

			}
		}
	}

	private void logSkip() {
		if (logSkippedRecords) {
			if (nonSkipCount == 0 && !logInitialSkips) {
				return;
			}
			if (line == null) {
				throw new IllegalStateException("line is null");
			}
			while (true) {
				if (line.startsWith("*** 20") && !logSkippedTimestamp) {
					break;
				}
				if (line.startsWith("=====") && !logSkippedSeparator) {
					break;
				}
				logger.warn("skipped " + line);
				break;
			}
		}
	}

	private String readLine() throws IOException {

		line = br.readLine();
		if (line == null) {
			endOfFile = true;
		} else {
			if (events.exists(LOG_PARSED_RECORD)) {
				logger.info("line # " + br.getLineNumber() + ": " + line);
			}
		}
		return line;
	}

	private void readPreamble() throws IOException {
		while (true) {
			line = br.readLine();
			if (line.startsWith("==")) {
				break;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("preamble " + line);
			}
			filePreamble.add(line);
		}
	}

	void processBinds() throws IOException {
		final boolean saveLogSkipsInBind = logSkipsInBind;
		logSkippedRecords = logSkipsInBind;
		while ((line = readLine()) != null) {
			if (line.charAt(0) != ' ') {
				break;
			}

		}
		logSkippedRecords = saveLogSkipsInBind;
		// logger.info("stopped skipping at " + br.getLineNumber() +
		// " with text " + line);
	}

	public int getGetNextCount() {
		return getNextCount;
	}

	public void setGetNextCount(int getNextCount) {
		this.getNextCount = getNextCount;
	}

	public int getNonSkipCount() {
		return nonSkipCount;
	}

	public void setNonSkipCount(int nonSkipCount) {
		this.nonSkipCount = nonSkipCount;
	}

	public int getBindCount() {
		return bindCount;
	}

	public void setBindCount(int bindCount) {
		this.bindCount = bindCount;
	}

	public int getXctendCount() {
		return xctendCount;
	}

	public void setXctendCount(int xctendCount) {
		this.xctendCount = xctendCount;
	}

	public int getParsingCount() {
		return parsingCount;
	}

	public void setParsingCount(int parsingCount) {
		this.parsingCount = parsingCount;
	}

	public int getParseCount() {
		return parseCount;
	}

	public void setParseCount(int parseCount) {
		this.parseCount = parseCount;
	}

	public int getExecCount() {
		return execCount;
	}

	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}

	public int getWaitCount() {
		return waitCount;
	}

	public void setWaitCount(int waitCount) {
		this.waitCount = waitCount;
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int fetchCount) {
		this.fetchCount = fetchCount;
	}

	public int getStatCount() {
		return statCount;
	}

	public void setStatCount(int statCount) {
		this.statCount = statCount;
	}

	public int getEndOfStatementCount() {
		return endOfStatementCount;
	}

	public void setEndOfStatementCount(int endOfStatementCount) {
		this.endOfStatementCount = endOfStatementCount;
	}

	public int getIgnoredCount() {
		return ignoredCount;
	}

	public void setIgnoredCount(int ignoredCount) {
		this.ignoredCount = ignoredCount;
	}
}
