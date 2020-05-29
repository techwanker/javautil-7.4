package org.javautil.core.sql;

public enum BlockType {
	SQL, 
	MARKDOWN,   MARKDOWN_DIRECTIVE, 
	COMMENT, 	COMMENT_DIRECTIVE, 
	STATEMENT_BLOCK, STATEMENT_DIRECTIVE, 
	UNKNOWN,
	
	IGNORED, DIRECTIVE, STATEMENT  ;
	
	public boolean isSQL() {
		return (this == SQL || this == STATEMENT_BLOCK|| this == STATEMENT);
	}
}
