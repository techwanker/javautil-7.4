package org.javautil.core.sql;

public class SqlSplitterLine {
	private final int                 lineNumber;

	private int                       blockNumber = -1;
	
	private int statementNumber = -1;

	private int                       blockLineNumber;

	private final LineType type;

	private BlockType      blockType = BlockType.UNKNOWN;

	private final String              text;

	public SqlSplitterLine(int lineNumber, String text) {
		super();
		this.lineNumber = lineNumber;
		this.text = text;
		this.type = LineType.getSqlSplitterLineType(text);

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

	public int getBlockLineNumber() {
		return blockLineNumber;
	}

	public void setBlockLineNumber(int lineNbr) {
		blockLineNumber = lineNbr;

	}

	public String getText() {
		return text;
	}

	public LineType getType() {
		return type;
	}

	public String toString(String preamble) {
		return String.format("%-20s %s", preamble, toString());
	}

	@Override
	public String toString() {
		//return String.format("ssl: %5d %4d %4d %-22s %-22s %s", 
	return String.format("%5d %4d %4d %4d %-22s %-22s %s", 
				lineNumber, blockNumber, statementNumber, blockLineNumber, type,
		    blockType, text);
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public void setBlockType(BlockType blockType) {
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
