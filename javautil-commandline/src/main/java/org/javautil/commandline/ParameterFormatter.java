package org.javautil.commandline;



import jcmdline.Parameter;

public class ParameterFormatter {
	public String toString(final Parameter parameter) {
		final AsString as = new AsString();
		return as.toString(parameter);

	}
}
