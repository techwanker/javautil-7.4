package org.javautil.core.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import org.javautil.core.collections.ListHelper;
import org.javautil.core.text.AsString;
import org.javautil.core.text.CommonDateFormat;
import org.javautil.core.text.SimpleDateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs TODO replaceall \r \c \r\c \c\r with specified newline
 */
public class CsvWriter {
	private Writer              out                    = null;
	private static final String newline                = System.getProperty("line.separator");
	private boolean             flushEveryLine         = true;
	private SimpleDateFormatter dateFormatter          = new SimpleDateFormatter(CommonDateFormat.ISO_SECOND);
	private final Logger        logger                 = LoggerFactory.getLogger(getClass());
	private String              fieldSeparator         = ",";
	/**
	 * Some utilities get pretty pissed if the correct number of fields aren't found
	 * set this to true for them.
	 */
	private boolean             suppressTrailingNulls  = true;
	/**
	 * If a String is zero length or null emit null.
	 */
	private boolean             emitEmptyStringsAsNull = true;

	private final AsString      objectFormatter        = new AsString();

	public CsvWriter() {
	}

	public CsvWriter(final Writer w) {
		if (w == null) {
			throw new IllegalArgumentException("w is null");
		}
		out = w;
	}

	/**
	 * @return the suppressTrailingNulls
	 */
	public boolean isSuppressTrailingNulls() {
		return suppressTrailingNulls;
	}

	/**
	 * @param suppressTrailingNulls the suppressTrailingNulls to set
	 */
	public void setSuppressTrailingNulls(boolean suppressTrailingNulls) {
		this.suppressTrailingNulls = suppressTrailingNulls;
	}

	/**
	 * @return the fieldSeparator
	 */
	public String getFieldSeparator() {
		return fieldSeparator;
	}

	/**
	 * @param fieldSeparator the fieldSeparator to set
	 */
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String escape(final String in) {
		String retval = in;
		if (in != null) {
			if (in.indexOf("\"") > -1) {
				retval = in.replace("\"", "\"\"");
			}
			if (in.indexOf("\n") > -1) {
				retval = in.replace("\n", "\\n");
			}
		}
		return retval;
	}

	/**
	 * Converts an array of objects to a comma separated values String.
	 * 
	 * @param args array of objects
	 * @return as CSV
	 */
	public String asString(final ArrayList<Object> args) {
		if (args == null) {
			throw new IllegalArgumentException("objects is null");
		}

		final StringBuilder sb = new StringBuilder();
		final int n;
		if (suppressTrailingNulls) {
			n = ListHelper.lastNonNullValueIndex(args);
		} else {
			n = args.size() - 1;
		}

		for (int i = 0; i <= n; i++) {
			final Object o = args.get(i);


			if (o != null) {
				if (o instanceof String) {
					String str = (String) o;
					if ((str.length() == 0 && !emitEmptyStringsAsNull) || str.length() > 0) {
						sb.append("\"");
						sb.append(escape((String) o));
						sb.append("\"");
					}
				} else if (o instanceof Number) {
					sb.append(((Number) o).toString());
				} else if (o instanceof Date) {
					sb.append((dateFormatter.format((Date) o)));
				} else {
					sb.append("\"");
					sb.append(escape(o.toString()));
					sb.append("\"");
				}
			}
			if (i < n) {
				sb.append(fieldSeparator);
			}

		}
		final String retval = sb.toString();
//		if (logger.isDebugEnabled()) {
//			StringBuilder dsb = new StringBuilder();
//			dsb.append("Objects: " + objectFormatter.toString(objects) + "\n");
//			dsb.append("output string: " + retval);
//			logger.debug(dsb.toString());
//		}
		return retval;

	}

	// todo jjs what happend to the number formatters
//	public void writeArray(final Object... objects) throws IOException {
//		if (objects == null) {
//			throw new IllegalArgumentException("objects is null");
//		}
//		out.write(asString(objects));
//		out.write(newline);
//		if (flushEveryLine) {
//			out.flush();
//		}
//	}
	public void writeList(ArrayList<?> objects) throws IOException {
		if (objects == null) {
			throw new IllegalArgumentException("objects is null");
		}
		ArrayList<Object> al = new ArrayList<>();
		al.addAll(objects);
		out.write(asString(al));
		out.write(newline);
		if (flushEveryLine) {
			out.flush();
		}
	}

//	public void writeList(ArrayList<Object> objects) throws IOException {
//		if (objects == null) {
//			throw new IllegalArgumentException("objects is null");
//		}
//		out.write(asString(objects));
//		out.write(newline);
//		if (flushEveryLine) {
//			out.flush();
//		}
//	}

	public void close() throws IOException {
		out.close();
	}

	/**
	 * @return the flushEveryLine
	 */
	public boolean isFlushEveryLine() {
		return flushEveryLine;
	}

	/**
	 * @param flushEveryLine the flushEveryLine to set
	 */
	public void setFlushEveryLine(final boolean flushEveryLine) {
		this.flushEveryLine = flushEveryLine;
	}

	public SimpleDateFormatter getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(final SimpleDateFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public boolean isEmitEmptyStringsAsNull() {
		return emitEmptyStringsAsNull;
	}

	public void setEmitEmptyStringsAsNull(boolean value) {
		this.emitEmptyStringsAsNull = value;
	}

	public void setSimpleDateFormatter(SimpleDateFormatter exportDateTimeFormatter) {
		this.dateFormatter = exportDateTimeFormatter;

	}
}
