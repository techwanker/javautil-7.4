package org.javautil.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackTraceUtils {

	public static final String fileSeparator = System.getProperty("file.separator");

	private static Logger      logger        = LoggerFactory.getLogger(StackTraceUtils.class);

	/**
	 * todo doc
	 * 
	 * @param ste stackTraceElement
	 * @return path for element
	 */
	public static String getResourceMethodPath(final StackTraceElement ste) {

		final String className = ste.getClassName();
		logger.debug(className);
		final String methodName = ste.getMethodName();
		final String retval = className.replace(".", fileSeparator) + "." + methodName;
		return retval;
	}

}
