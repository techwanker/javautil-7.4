/**
 * @(#) RecordFactory.java
 */
package org.javautil.oracle.tracehandlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.javautil.oracle.OracleConnectionHelper;
import org.javautil.oracle.trace.record.Action;
import org.javautil.oracle.trace.record.Close;
import org.javautil.oracle.trace.record.Error;
import org.javautil.oracle.trace.record.Exec;
import org.javautil.oracle.trace.record.Fetch;
import org.javautil.oracle.trace.record.Module;
import org.javautil.oracle.trace.record.Parse;
import org.javautil.oracle.trace.record.ParseError;
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
 *  TODO coalesce with the FileSystemReader
 */
public class TraceFileUtlFileReader implements Closeable, TraceFileReader {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static EventHelper events = new EventHelper();
	private static final Integer LOG_PARSED_RECORD = Integer.valueOf(1);
	//
	private final ArrayList<String> filePreamble = new ArrayList<String>();
	//
	private LineNumberReader br;

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
	private List<Integer> unhandledRecords = new LinkedList<>();
	private List<ParseError> parseErrors = new LinkedList<>();
	private final String fileName;

	private boolean showError;
	private boolean showParseError;
	private HashMap<RecordType, Integer> unhandledCountByType = new HashMap<>();

	public TraceFileUtlFileReader(final Connection connection, String fileName) throws SQLException, IOException {
		this.fileName = fileName;
		String fileText = OracleConnectionHelper.getTextFromRdbmsTraceFile(connection,fileName);
		logger.info("attempting to read trace file {} ", fileName);
		ByteArrayInputStream bais = new ByteArrayInputStream(fileText.getBytes());
		Reader r = new InputStreamReader(bais);
		BufferedReader buffy = new BufferedReader(r);
		br = new LineNumberReader(buffy);
		readLine();
	}

	
	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#close()
	 */
	@Override
	public void close() throws IOException {
		if (unhandledCountByType.size() > 0) {
			logger.warn("Unhandled counts: {} " + unhandledCountByType.toString());
		}
		br.close();
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getNext()
	 */
	@Override
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
					record = new Parsing(line, br.getLineNumber()); // TODO why create a new one?
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
					wait.clean(); // TODO ??
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
				case ACTION:
					record = new Action(line, br.getLineNumber());
					break;
				case MODULE:
					record = new Module(line, br.getLineNumber());
					break;
				case APP_NAME:
					break;
				case ERROR:
					record = new Error(br.getLineNumber(), line);
					if (showError) {
						System.out.println(String.format("error found: %d %s", br.getLineNumber(), line));
					}
					break;
				case PARSE_ERROR:

					ParseError pe = new ParseError(br.getLineNumber(), line);
					record = pe;
					logger.debug("parse error found: {} {}", br.getLineNumber(), line);
					while ((line = readLine()) != null) {
						if (line.startsWith("***")) { // TOOD create regex or do a lookahead to see what next is
							break;
						}
						if (showParseError) {
							System.out.println(String.format("parse error found: %d %s", br.getLineNumber(), line));
						}
						pe.addLine(line);
					}
					parseErrors.add(pe);
					break;
				case SEPARATOR:
					break;
				case STARS:
					break;
				case TIMESTAMP:
					break;
				case UNMAP:
					break;
				case CLOSE:
					record = new Close(line, br.getLineNumber());
					break;
				case LOBREAD:
				default:
					logUnhandled(br.getLineNumber(), line, recordType);
					logSkip();
				}
			}
			if (record != null) {
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
			logger.debug("returning null record");
		} else if (events.exists(LOG_PARSED_RECORD)) {
			logger.debug(record.toString());
		}
		return record;
	}

	private void logUnhandled(int lineNumber, String line, RecordType recordType) {
		unhandledRecords.add(lineNumber);
		Integer count = unhandledCountByType.get(recordType);
		int newCount;
		if (count == null) {
			newCount = 1;
		} else {
			newCount = ++count;
		}
		unhandledCountByType.put(recordType, newCount);
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
			readPreamble();
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
				if (line.trim().length() > 0) {
					logger.debug("skipped {} '{}'", br.getLineNumber(), line);
				}
				break;
			}
		}
	}

	private String readLine() throws IOException {

		line = br.readLine();
		logger.debug("readLine: {}",line);
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
		while ((line = br.readLine()) != null) {
			if (line.startsWith("==")) {
				break;
			}
			logger.debug("preamble {}" + line);
			filePreamble.add(line);
		}
	}

	void processBinds() throws IOException {
		final boolean saveLogSkipsInBind = logSkipsInBind;
		logSkippedRecords = logSkipsInBind;
		while ((line = readLine()) != null && line.length() > 0) {
			if (line.charAt(0) != ' ') {
				break;
			}
		}
		logSkippedRecords = saveLogSkipsInBind;

	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getGetNextCount()
	 */
	@Override
	public int getGetNextCount() {
		return getNextCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getNonSkipCount()
	 */
	@Override
	public int getNonSkipCount() {
		return nonSkipCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getBindCount()
	 */
	@Override
	public int getBindCount() {
		return bindCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getXctendCount()
	 */
	@Override
	public int getXctendCount() {
		return xctendCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getParsingCount()
	 */
	@Override
	public int getParsingCount() {
		return parsingCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getParseCount()
	 */
	@Override
	public int getParseCount() {
		return parseCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getExecCount()
	 */
	@Override
	public int getExecCount() {
		return execCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getWaitCount()
	 */
	@Override
	public int getWaitCount() {
		return waitCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getFetchCount()
	 */
	@Override
	public int getFetchCount() {
		return fetchCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getStatCount()
	 */
	@Override
	public int getStatCount() {
		return statCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getEndOfStatementCount()
	 */
	@Override
	public int getEndOfStatementCount() {
		return endOfStatementCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getIgnoredCount()
	 */
	@Override
	public int getIgnoredCount() {
		return ignoredCount;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getFileName()
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/* (non-Javadoc)
	 * @see org.javautil.oracle.tracehandlers.TraceFileReader#getLineNumber()
	 */
	@Override
	public int getLineNumber() {

		return br.getLineNumber();
	}
}
