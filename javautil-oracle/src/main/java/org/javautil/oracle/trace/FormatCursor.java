/**
 * @(#) Cursor.java
 */
package org.javautil.oracle.trace;

import org.javautil.oracle.trace.record.Fetch;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.trace.record.Stat;

/**
 * 
 * There is nothing to aggregate on a Parsing.
 * 
 * @todo fix adding 1 to fetch count even if there is no fetch, maybe just on
 *       aggregation.
 */
public class FormatCursor {

	private static final String newline = System.getProperty("line.separator");

	public String format(Cursor cursor) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatParsing(cursor));
		sb.append(formatStats(cursor));
		sb.append(formatFetches(cursor));
		return sb.toString();
	}

	public StringBuilder formatParsing(Cursor cursor) {
		StringBuilder sb = new StringBuilder();
		Parsing parsing = cursor.getParsing();
		if (parsing != null) {
			sb.append(parsing.getSqlText());
			sb.append(newline);
		}
		return sb;
	}

	public StringBuilder formatStats(Cursor cursor) {
		StringBuilder sb = new StringBuilder();
		for (Stat stat : cursor.getStats()) {
			sb.append(stat.toString());
			sb.append(newline);
		}
		return sb;
	}

	public StringBuilder formatFetches(Cursor cursor) {
		StringBuilder sb = new StringBuilder();
		if (cursor.getFetches() != null) {
			for (Fetch fetch : cursor.getFetches()) {
				sb.append(fetch.toString());
				sb.append(newline);
			}
		}
		return sb;
	}
}
