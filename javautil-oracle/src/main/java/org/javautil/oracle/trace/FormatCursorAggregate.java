/**
 *
 */
package org.javautil.oracle.trace;

import org.javautil.oracle.trace.aggregation.CursorOperationAggregation;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.trace.record.Stat;

/**
 * 
 * There is nothing to aggregate on a Parsing.
 * 
 * @todo fix adding 1 to fetch count even if there is no fetch, maybe just on
 *       aggregation.
 */
public class FormatCursorAggregate {

	private static final String newline = System.getProperty("line.separator");

	public String format(CursorAggregates cursors) {
		StringBuilder sb = new StringBuilder();
		for (CursorAggregate cursor : cursors.getAggregatesById().values()) {
			sb.append(newline);
			if (cursor.getParsing() != null) {
				sb.append(cursor.getParsing());
				sb.append(newline);
			}
			if (cursor.getParseAggregation() != null) {
				sb.append(cursor.getParseAggregation());
				sb.append(newline);
			}
			if (cursor.getExecAggregation() != null) {
				sb.append(cursor.getExecAggregation());
				sb.append(newline);
			}
			if (cursor.getFetchAggregation() != null) {
				sb.append(cursor.getFetchAggregation());
				sb.append(newline);
			}
		}
		return sb.toString();
	}

	public String format(CursorAggregate agg) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatParsing(agg));
		sb.append(formatStats(agg));
		sb.append(formatFetches(agg));
		return sb.toString();
	}

	/**
	 * TODO should create a super class for cursors and aggregates
	 * 
	 * @param cursor
	 * @return
	 */
	public StringBuilder formatParsing(CursorAggregate cursor) {
		StringBuilder sb = new StringBuilder();
		Parsing parsing = cursor.getParsing();
		if (parsing != null) {
			sb.append(parsing.getSqlText());
			sb.append(newline);
		}
		return sb;
	}

	public StringBuilder formatStats(CursorAggregate cursor) {
		StringBuilder sb = new StringBuilder();
		for (Stat stat : cursor.getStats()) {
			sb.append(stat.toString());
			sb.append(newline);
		}
		return sb;
	}

	public String formatFetches(CursorAggregate cursor) {
		CursorOperationAggregation agg = cursor.getFetchAggregation();
		return agg.toString();
	}
}
