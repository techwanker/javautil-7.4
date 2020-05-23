package org.javautil.core.sql;

public enum SqlSplitterBlockType {
	COMMENT, PROCEDURE, SQL, 
	MARKDOWN, MARKDOWN_DIRECTIVE, COMMENT_DIRECTIVE, INDETERMINATE, 
	IGNORED, DIRECTIVE, STATEMENT;
	
	public boolean isSQL() {
		return (this == SQL || this == PROCEDURE || this == STATEMENT);
	}
}
