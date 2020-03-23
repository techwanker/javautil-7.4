/**
 * @(#) Cursor.java
 */
package org.javautil.oracle.trace;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Formatter;

import org.javautil.oracle.trace.aggregation.CursorOperationAggregation;
import org.javautil.oracle.trace.record.StatAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * There is nothing to aggregate on a Parsing.
 * 
 * @todo fix adding 1 to fetch count even if there is no fetch, maybe just on
 *       aggregation.
 */
public class FormatCursorsTkprofStyle {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String newline = System.getProperty("line.separator");

	private final FormatCursor formatCursor = new FormatCursor();

	private final Formatter formatter;

	private final OutputStream os;

	private final String secondsHeadingFormat = "%16s";
	private final String secondsDashes = "----------------";
	private final String secondsFormat = "%16.6f";

	private String columnHeaderFormat = null;

	// private final Writer writer;

	private final String[] header = new String[] {
			"********************************************************************************",
			"count    = number of times OCI procedure was executed", "cpu      = cpu time in seconds executing",
			"elapsed  = elapsed time in seconds executing", "disk     = number of physical reads of buffers from disk",
			"query    = number of buffers gotten for consistent read",
			"current  = number of buffers gotten in current mode (usually for update)",
			"rows     = number of rows processed by the fetch or execute call",
			"********************************************************************************" };

	private final float million = 1000000;

	private String dashesFormat;

	private String numericFormat;

	private static final String tenDashes = "----------";

	public FormatCursorsTkprofStyle(OutputStream os) {
		if (os == null) {
			throw new IllegalArgumentException("os is null");
		}
		this.os = os;
		// writer = new OutputStreamWriter(os);
		formatter = new Formatter(os);
	}

	void emitExplanation() throws IOException {
		for (String string : header) {
			writeLn(string);
		}
	}

	void writeLn(String string) throws IOException {
		if (string != null) {
			formatter.format("%s%s", string, newline);
		} else {
			formatter.format("%s", newline);
		}

	}

	public void emitColumnHeaders() throws IOException {
		if (columnHeaderFormat == null) {
			columnHeaderFormat = "%7s %10s " + secondsHeadingFormat + " " + secondsHeadingFormat
					+ " %10s %10s %10s %10s" + newline;
			if (logger.isDebugEnabled()) {
				logger.debug("columnHeaderFormat: '" + columnHeaderFormat + "'");
			}
		}

		formatter.format(columnHeaderFormat, "call", "count", "cpu", "elapsed", "disk", "query", "current", "rows");
		emitDashes();
		formatter.flush();

	}

	public void emitDashes() throws IOException {

		if (dashesFormat == null) {
			dashesFormat = "%7s %10s " + secondsHeadingFormat + " " + secondsHeadingFormat + " %10s %10s %10s %10s"
					+ newline;
		}
		formatter.format(dashesFormat, "-------", tenDashes, secondsDashes, secondsDashes, tenDashes, tenDashes,
				tenDashes, tenDashes);
		formatter.flush();

	}

	public void format(CursorAggregates cursors) throws IOException {
		for (CursorAggregate cursor : cursors.getAggregatesById().values()) {
			logger.debug("processing cursor " + cursor);
			if (cursor.getParsing() != null) {
				writeLn("SQL ID : " + cursor.getParsing().getSqlid());
				writeLn(cursor.getParsing().getSqlText());
			}
			emitColumnHeaders();
			emitAggregate("PARSE", cursor.getParseAggregation());
			emitAggregate("EXEC", cursor.getExecAggregation());
			emitAggregate("FETCH", cursor.getFetchAggregation());
			emitDashes();
			emitAggregate("TOTAL", cursor.getTotalAggregation());
			writeLn(null);
			emitStats(cursor);
		}
	}

	void emitAggregate(String operation, CursorOperationAggregation aggregation) {
		if (numericFormat == null) {
			numericFormat = "%7s %10d " + secondsFormat + " " + secondsFormat + " %10d %10d %10d %10d" + newline;
		}
		if (aggregation != null) {
			formatter.format(numericFormat, operation, aggregation.getEventCount(), aggregation.getCpu() / million,
					aggregation.getElapsedMicroSeconds() / million, aggregation.getPhysicalBlocksRead(),
					aggregation.getConsistentReadBlocks(), aggregation.getCurrentModeBlocks(),
					aggregation.getRowCount());
		}

	}

	// (cr=0 pr=0 pw=0 time=0 us cost=0 size=13 card=1)

	void emitStats(CursorAggregate cursor) {
		cursor.getStatsById().setDepths();
		formatter.format("%s" + newline, "Rows     Row Source Operation");
		formatter.format("%s" + newline, "-------  ---------------------------------------------------");

		for (StatAggregation agg : cursor.getStatsById().values()) {
			int indent = agg.getDepth() + 2;
			String formatString = "%7d" + "%" + indent + "s" + "%s" + " (cr=%d pr=%d pw=%d cost=%d size=%d card=%d)"
					+ newline;
			if (logger.isDebugEnabled()) {
				logger.debug("formatString: " + formatString);
			}
			formatter.format(formatString, agg.getCnt(), "", agg.getOperation(), agg.getCr(), agg.getPhysicalReads(),
					agg.getPhysicalWrites(), 0, 0, 0);
		}
	}
}
