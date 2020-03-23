package org.javautil.core.sql;

public class SqlSplitterException extends RuntimeException {
	private SqlSplitterLine line;

	private String          message;

	SqlSplitterException(SqlSplitterLine line, String message) {
		this.line = line;
		this.message = message;
	}

	public SqlSplitterLine getLine() {
		return line;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
