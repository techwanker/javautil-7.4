package org.javautil.core.sql;

import java.util.regex.Pattern;

public enum SqlSplitterLineType {
	BLANK,
	INDETERMINATE, 
	SQL, 
	STATEMENT_NAME, STATEMENT_END, 
	// END_NAME, 
	SEMICOLON,
	COMMENT, COMMENT_BLOCK_BEGIN, COMMENT_BLOCK_END,
	MARKDOWN_BLOCK_BEGIN, MARKDOWN_BLOCK_END, SQL_WITH_SEMICOLON,
	PROCEDURE_BLOCK_END, PROCEDURE_BLOCK_START; 
	private static Pattern semiPattern = Pattern.compile(".*;\\s*--.*");

	public static SqlSplitterLineType getSqlSplitterLineType(String text) {
		final String trimmed = text.trim();

		if (trimmed.startsWith("--#<")) {
			return COMMENT_BLOCK_BEGIN;
		}
		if (trimmed.startsWith("--#>")) {
			return COMMENT_BLOCK_END;
		}
		if (trimmed.startsWith("--#")) {
			return COMMENT;
		}
		if (trimmed.startsWith("--::<")) {
			return MARKDOWN_BLOCK_BEGIN;
		}
		if (trimmed.startsWith("--::>")) {
			return MARKDOWN_BLOCK_END;
		}
		if (trimmed.startsWith("--/<")) {
			return PROCEDURE_BLOCK_START;
		}
		if (trimmed.startsWith("--/>")) {
			return PROCEDURE_BLOCK_END;
		}
		if (trimmed.equals("/")) {
			return PROCEDURE_BLOCK_END;
		}
		if (trimmed.startsWith(";--")) {
			return STATEMENT_END;
		}
		if (trimmed.toUpperCase().startsWith("--@NAME")) {
			return STATEMENT_NAME;
		}
		if (trimmed.length() == 0) {
			return BLANK;
		}
		/*
		if (trimmed.toUpperCase().startsWith("--@END_NAME")) {
			return END_NAME;
		}
		 */
		if (trimmed.endsWith(";")) {
			if (trimmed.length() == 1) {
				return SEMICOLON;
			} else {
				return SQL_WITH_SEMICOLON;
			}
		}
		if (semiPattern.matcher(trimmed).matches()) {
			return SQL_WITH_SEMICOLON;
		}

		return INDETERMINATE;
	}

	SqlSplitterBlockType getBlockType() {

		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
			return SqlSplitterBlockType.MARKDOWN;
		case COMMENT_BLOCK_BEGIN:
			return SqlSplitterBlockType.COMMENT;
		case PROCEDURE_BLOCK_START:
		case STATEMENT_NAME:
		case INDETERMINATE:
			return SqlSplitterBlockType.STATEMENT;
		default:
			throw new IllegalArgumentException("Unsupported block type "  + this);
		}
	}

	boolean isBlockEnd(SqlSplitterLineType sentinel) {
		switch (this) {
		case INDETERMINATE:
			switch (sentinel) {
			case SQL_WITH_SEMICOLON:
			case STATEMENT_END:
			case PROCEDURE_BLOCK_START:
			case STATEMENT_NAME:
				return true;
			case COMMENT:
			case COMMENT_BLOCK_BEGIN:
			case COMMENT_BLOCK_END:
			case INDETERMINATE:
			case MARKDOWN_BLOCK_BEGIN:
			case MARKDOWN_BLOCK_END:
			case PROCEDURE_BLOCK_END:
			case SEMICOLON:
			case SQL:
				return false;
			}
			return false;
		case COMMENT_BLOCK_BEGIN:
			return sentinel.equals(COMMENT_BLOCK_END);
		case MARKDOWN_BLOCK_BEGIN:
			return sentinel.equals(MARKDOWN_BLOCK_END);
		case PROCEDURE_BLOCK_START:
			return sentinel.equals(PROCEDURE_BLOCK_END);
		default:
			throw new IllegalStateException();
		}

	} SqlSplitterLineType getEndType() {
		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
			return MARKDOWN_BLOCK_END;
		case PROCEDURE_BLOCK_START:
			return PROCEDURE_BLOCK_END;
		case COMMENT_BLOCK_BEGIN:
			return COMMENT_BLOCK_END;
		case STATEMENT_NAME:
			return STATEMENT_END;
		default:
			throw new IllegalArgumentException(this.name());
		}
	}

	boolean isBegin() {
		boolean retval = false;
		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
		case PROCEDURE_BLOCK_START:
		case COMMENT_BLOCK_BEGIN:
			retval = true;
			break;
		default:
			break;
		}
		return retval;		

	}
	boolean isEnd() {
		boolean retval = false;
		switch (this) {
		case COMMENT_BLOCK_END:
		case MARKDOWN_BLOCK_END:
		case PROCEDURE_BLOCK_END:
			retval = true;
			break;
		default:
			break;
		}
		return retval;		

	}


}