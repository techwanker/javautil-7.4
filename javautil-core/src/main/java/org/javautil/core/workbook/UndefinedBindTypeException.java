package org.javautil.core.workbook;

public class UndefinedBindTypeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3052080037716421367L;
	private String            bindName;

	public UndefinedBindTypeException(String bindName) {
		this.bindName = bindName;
	}

	@Override
	public String getMessage() {
		return "bindName: '" + bindName + "' is in a SQL statement but it's type is not defined";
	}
}
