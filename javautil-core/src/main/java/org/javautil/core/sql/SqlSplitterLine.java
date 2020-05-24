package org.javautil.core.sql;

public class SqlSplitterLine {
	private final int                 lineNumber;

	private int                       blockNumber = -1;
	
	private int statementNumber = -1;

	private int                       statementLineNumber;

	private final SqlSplitterLineType type;

	private SqlSplitterBlockType      blockType = SqlSplitterBlockType.INDETERMINATE;

	private final String              text;

	public SqlSplitterLine(int lineNumber, String text) {
		super();
		this.lineNumber = lineNumber;
		this.text = text;
		this.type = SqlSplitterLineType.getSqlSplitterLineType(text);

	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}

	public int getStatementLineNumber() {
		return statementLineNumber;
	}

	public void setStatementLineNumber(int lineNbr) {
		statementLineNumber = lineNbr;

	}

	public String getText() {
		return text;
	}

	public SqlSplitterLineType getType() {
		return type;
	}

	public String toString(String preamble) {
		return String.format("%-20s %s", preamble, toString());
	}

	@Override
	public String toString() {
		//return String.format("ssl: %5d %4d %4d %-22s %-22s %s", 
	return String.format("%5d %4d %4d %4d %-22s %-22s %s", 
				lineNumber, blockNumber, statementNumber, statementLineNumber, type,
		    blockType, text);
	}

	public SqlSplitterBlockType getBlockType() {
		return blockType;
	}

	public void setBlockType(SqlSplitterBlockType blockType) {
		this.blockType = blockType;
		// logger.info("setBlockType: " + this.toString());
	}

	public int getStatementNumber() {
		return statementNumber;
	}

	public void setStatementNumber(int statementNumber) {
		this.statementNumber = statementNumber;
	}
}
