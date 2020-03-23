package org.javautil.core.sql;

import java.sql.SQLException;

public class IllegalOperationException extends SQLException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1907983126890603869L;

	public IllegalOperationException(String string) {
		super(string);
	}

}
