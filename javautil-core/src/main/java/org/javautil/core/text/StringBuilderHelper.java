package org.javautil.core.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs@dbexperts.com December 2018
 */
public class StringBuilderHelper {

	private transient final Logger logger         = LoggerFactory.getLogger(getClass());

	private String                 separator      = ": ";
	private String                 pairSeparator  = "\n";
	private StringBuilder          sb             = new StringBuilder();
	private boolean                skipNullValues = true;

	public StringBuilderHelper() {
		super();
	}

	public StringBuilderHelper(final String val) {
		super();
		sb.append(val.toString());
		sb.append("\n");
	}

	public StringBuilderHelper append(final String name, final Object value) {
		return addNameValue(name, value);
	}

	public StringBuilderHelper addNameValue(final String name, final Object value) {
		if (value != null || !skipNullValues) {
			sb.append(name);
			sb.append(separator);
			if (value == null) {
				sb.append("null");
			} else {
				sb.append("'");
				sb.append(value); // TODO should escape single quotes
				sb.append("'");
			}
			sb.append(pairSeparator);
		}
		return this;
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	public void setPairSeparator(final String val) {
		pairSeparator = val;
	}

	public void setValueSeparator(final String val) {
		separator = val;
	}

	public StringBuilderHelper append(String string) {
		sb.append(string);
		return this;
	}

	@Override
	public String toString() {
		String retval = sb.toString();
		logger.debug("toString retval:\n{}", retval);
		return retval;
	}
}
