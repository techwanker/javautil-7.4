/**
 * @(#) Cursor.java
 */
package org.javautil.oracle.trace;

/**
 * 
 * There is nothing to aggregate on a Parsing.
 * 
 * @todo fix adding 1 to fetch count even if there is no fetch, maybe just on
 *       aggregation.
 */
public class FormatCursors {

	private static final String newline = System.getProperty("line.separator");

	private final FormatCursor formatCursor = new FormatCursor();

	public String format(Cursors cursors) {

		StringBuilder sb = new StringBuilder();
		for (Cursor cursor : cursors.getCursors()) {
			sb.append(formatCursor.format(cursor));
			sb.append(newline);
		}
		return sb.toString();
	}

}
