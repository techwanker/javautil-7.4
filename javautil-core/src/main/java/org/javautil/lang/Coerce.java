package org.javautil.lang;

public class Coerce {
	public static boolean asBoolean(String value) {
		// TODO put in lang.coerce along with true etc.
		Boolean retval = null;
		if ("Y".equals(value)) {
			retval = true;
		} else if ("N".equals(value)) {
			retval = false;
		} else {
			throw new IllegalArgumentException("value is not Y|N " + value);
		}
		return retval;
	}
}
