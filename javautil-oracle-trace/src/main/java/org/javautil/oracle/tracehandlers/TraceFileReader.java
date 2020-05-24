package org.javautil.oracle.tracehandlers;

import java.io.IOException;

import org.javautil.oracle.trace.record.Record;

public interface TraceFileReader {

	void close() throws IOException;

	Record getNext() throws IOException;

	int getGetNextCount();

	int getNonSkipCount();

	int getBindCount();

	int getXctendCount();

	int getParsingCount();

	int getParseCount();

	int getExecCount();

	int getWaitCount();

	int getFetchCount();

	int getStatCount();

	int getEndOfStatementCount();

	int getIgnoredCount();

	String getFileName();

	int getLineNumber();

}