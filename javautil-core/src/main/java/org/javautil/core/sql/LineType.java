package org.javautil.core.sql;

import java.util.regex.Pattern;

public enum LineType {
	BLANK,
	INDETERMINATE, 
	STATEMENT_NAME, STATEMENT_END, 
	// END_NAME, 
	SEMICOLON,
	COMMENT, COMMENT_BLOCK_BEGIN, COMMENT_BLOCK_END,
	MARKDOWN_BLOCK_BEGIN, MARKDOWN_BLOCK_END, SQL_WITH_SEMICOLON,
	PROCEDURE_BLOCK_END, PROCEDURE_BLOCK_START, SLASH; 
	private static Pattern semiPattern = Pattern.compile(".*;\\s*--.*");

	public static LineType getSqlSplitterLineType(String text) {
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
			return SLASH;
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

	boolean isBlockStart() {
		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
		case COMMENT_BLOCK_BEGIN:
		case PROCEDURE_BLOCK_START:
			return true; 
		default:
			return false;
		}

	}
	BlockType getBlockType() {

		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
			return BlockType.MARKDOWN;
		case COMMENT_BLOCK_BEGIN:
			return BlockType.COMMENT;
		case PROCEDURE_BLOCK_START:
			return BlockType.STATEMENT_BLOCK;
		case STATEMENT_NAME:
		case INDETERMINATE:
		case SQL_WITH_SEMICOLON:
			return BlockType.STATEMENT;
		default:
			throw new IllegalArgumentException("Unsupported block type "  + this);
		}
	}

	boolean isStatementEnd() {
		switch (this) {
		case STATEMENT_END:
		case SQL_WITH_SEMICOLON:
		case SEMICOLON:
			return true;
		default:
			return false;
		}

	}

	boolean isBlockEnd() {
		switch (this) {
		case COMMENT_BLOCK_END:
		case MARKDOWN_BLOCK_BEGIN:
		case MARKDOWN_BLOCK_END:
		case PROCEDURE_BLOCK_END:
			return true;
		default:
			return false;
		}
	}

	boolean isBlockEnd(LineType sentinel) {
		switch (this) {

		case INDETERMINATE:
			switch (sentinel) {
			case SQL_WITH_SEMICOLON:
			case STATEMENT_END:
			case PROCEDURE_BLOCK_START:
			case STATEMENT_NAME:
			case SEMICOLON:
				return true;
			case COMMENT:
			case COMMENT_BLOCK_BEGIN:
			case COMMENT_BLOCK_END:
			case INDETERMINATE:
			case MARKDOWN_BLOCK_BEGIN:
			case MARKDOWN_BLOCK_END:
			case PROCEDURE_BLOCK_END:
				return false;
			}
			return false;
		case COMMENT_BLOCK_BEGIN:
			return sentinel.equals(COMMENT_BLOCK_END);
		case MARKDOWN_BLOCK_BEGIN:
			return sentinel.equals(MARKDOWN_BLOCK_END);
		case PROCEDURE_BLOCK_START:
			return sentinel.equals(PROCEDURE_BLOCK_END);
		case SQL_WITH_SEMICOLON:
			return sentinel.equals(SQL_WITH_SEMICOLON);
		case STATEMENT_NAME: 
			switch (sentinel) {
			case STATEMENT_END:
			case SQL_WITH_SEMICOLON:
			case SEMICOLON:
				return true;
			default:
				return false;
			}
		default:
			throw new IllegalStateException(this.name() + "called with " + sentinel);
		}

	} 

	boolean isStatementEndType(LineType sentinel) {
		switch (this) {
		case STATEMENT_NAME:
		case INDETERMINATE:
			switch (sentinel) {
			case COMMENT:
			case BLANK:
				return false;
			case COMMENT_BLOCK_BEGIN:
			case COMMENT_BLOCK_END:
			case MARKDOWN_BLOCK_END:
			case MARKDOWN_BLOCK_BEGIN:
			case PROCEDURE_BLOCK_START:
			case PROCEDURE_BLOCK_END:
				// TODO
				return false;
			case INDETERMINATE:
			case SEMICOLON:
			case SQL_WITH_SEMICOLON:
			case STATEMENT_END:
				return true;
			case STATEMENT_NAME:
				throw new IllegalStateException("Two @NAME");
			}
		default:
			throw new IllegalArgumentException(this.name());
		}

	}
	boolean isEndType(LineType sentinel) {
		switch (this) {
		case MARKDOWN_BLOCK_BEGIN:
			return sentinel.equals(LineType.MARKDOWN_BLOCK_END);
		case PROCEDURE_BLOCK_START:
			return sentinel.equals(LineType.PROCEDURE_BLOCK_END);
		case COMMENT_BLOCK_BEGIN:
			return sentinel.equals(LineType.COMMENT_BLOCK_END);
		case STATEMENT_NAME:
		case INDETERMINATE:
			switch (sentinel) {
			case COMMENT:
			case BLANK:
				return false;
			case COMMENT_BLOCK_BEGIN:
			case COMMENT_BLOCK_END:
			case MARKDOWN_BLOCK_END:
			case MARKDOWN_BLOCK_BEGIN:
			case PROCEDURE_BLOCK_START:
			case PROCEDURE_BLOCK_END:
				return false;
			case INDETERMINATE:
			case SEMICOLON:
			case SQL_WITH_SEMICOLON:
			case STATEMENT_END:
				return true;
			case STATEMENT_NAME:
				throw new IllegalStateException("Two @NAME");
			}
		default:
			throw new IllegalArgumentException(this.name());
		}

	}
	//	SqlSplitterLineType getEndType() {
	//		switch (this) {
	//		case MARKDOWN_BLOCK_BEGIN:
	//			return MARKDOWN_BLOCK_END;
	//		case PROCEDURE_BLOCK_START:
	//			return PROCEDURE_BLOCK_END;
	//		case COMMENT_BLOCK_BEGIN:
	//			return COMMENT_BLOCK_END;
	//		case STATEMENT_NAME:
	//			return STATEMENT_END;
	//		default:
	//			throw new IllegalArgumentException(this.name());
	//		}
	//	}

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

	public boolean isMultistatementBlockEnd(LineType type) {
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