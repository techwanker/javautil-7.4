package org.javautil.core.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javautil.core.text.StringUtils;
import org.javautil.io.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO remove System commments
/*
 * --#< begin comment block This line and all lines to end comment block are not
 * returned in sql 
 * 
 * --#> end comment block This may be used to skip sqlplus
 * directives, for example 
 * <code> 
 * --#< 
 *    set echo on 
 *    set serveroutput on 
 * --#> 
 * The * above stat
 * <p>
 * <p>
 * --/< begin statement block --/> end statement block
 * <p>
 * Alternativel --/ / end statement block sqlplus compatability ends plsql code
 * block
 * <p>
 * Can be a pair --::< begin of markdown block may include sql or procedural
 * code --::> end of markdown block --:: markdown or Representational Text
 */

public class SqlSplitter {
	private final static Logger               logger            = LoggerFactory.getLogger(SqlSplitter.class);

	private LineNumberReader                   reader;
	private boolean                            traceAnalyze      = false;

	public static final int traceBlock = 0x01;
	public static final int traceAnalysis = 0x02;
	private int traceFlags = traceBlock | traceAnalysis;

	private int                        verbosity         = 0;
	private InputStream                is;
	private String                     inputName;
	private String                     inputType;
	private String                     inLine;
	private ArrayList<SqlSplitterLine> lines;
	private boolean                    analyzed          = false;
	private int                        traceState        = 1;
	private final static Pattern       annotationPattern = Pattern.compile("^ *--@.*");
	private boolean                    trace             = false;
	private int                        blockNumber = -1;
	private boolean                    proceduresOnly;
	private ArrayList<String>          statements = new ArrayList<String>();
	private TreeMap<Integer,Integer>   blockIndex = new TreeMap<>();
	private TreeMap<Integer,BlockType>   blockTypeMap = new TreeMap<>();
	private int statementNumber = -1;
	/**
	 * k - statement number (relative 0), v lines index (relative 0) 
	 * for blocks of statement
	 */
	private TreeMap<Integer,Integer>   statementIndex = new TreeMap<>();

	public TreeMap<Integer, Integer> getStatementIndex() {
		return statementIndex;
	}

	public SqlSplitter() {

	}

	// TODO accept a String Constructor
	public SqlSplitter(Object instantiator, String resourceName) throws FileNotFoundException {
		this.inputName = resourceName;
		this.inputType = "resource";
		// TODO dedupe
		this.is = ResourceHelper.getResourceAsInputStream(instantiator, resourceName);

		final InputStreamReader isr = new InputStreamReader(is);
		reader = new LineNumberReader(isr);
	}

	public SqlSplitter(File inputFile) throws FileNotFoundException {
		this.inputName = inputFile.getAbsolutePath();
		this.inputType = "file";
		this.is = new FileInputStream(inputFile);
		if (this.is == null) {
			final String message = "unable to open file: '" + inputFile.getAbsolutePath() + "'";
			logger.error(message);
			throw new IllegalStateException(message);
		}
		final InputStreamReader isr = new InputStreamReader(is);
		reader = new LineNumberReader(isr);
	}

	public SqlSplitter(InputStream splitterInputStream) {
		if (splitterInputStream ==  null) {
			throw new IllegalArgumentException("input stream is null");
		}
		reader = new LineNumberReader(new InputStreamReader(splitterInputStream));
	}

	public void setTraceFlags(int traceFlags) {
		this.traceFlags = traceFlags;
	}

	public void process() {
		processLines();
		analyze();
		if (traceState > 0) {
			logger.info("\n{}",formatLines());
		}
		// TODO need to close the files
	}

	public static String trimSql(String text, boolean isBlock) {
		//logger.info("trimming '{}'",text);
		Character[] whiteCruft = {'\t','\n','\r',' '};

		String leading = StringUtils.stripTrailing(text,whiteCruft);
		//logger.info("leading: '{}'", leading);

		Character[] semi = {';'};
		String noSemi = isBlock ? leading : StringUtils.stripTrailing(leading, semi);
		//String noSemi = StringUtils.stripTrailing(leading, semi);
		//logger.info("noSemi '{}'",noSemi);

		String trimmed = StringUtils.stripLeading(noSemi,whiteCruft);

		//logger.info("trimmed '{}'",trimmed);
		return trimmed;

	}

	public String lineInfo() {
		return String.format("line# %d, '%s'", reader.getLineNumber(), inLine);
	}

	public ArrayList<SqlSplitterLine> processLines() {
		try {
			if (lines == null) {
				lines = new ArrayList<>();
				String line;
				while ((line = reader.readLine()) != null) {
					lines.add(new SqlSplitterLine(reader.getLineNumber(), line));
				}
			}
			return lines;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * By the time this arrives as loopPhase
	 * What if start block or end block
	 * @param linesIndex
	 * @return
	 */
	int processDelimitedBlock(int linesIndex ) {
		SqlSplitterLine blockBegin = lines.get(linesIndex);
		BlockType blockType = blockBegin.getType().getBlockType();
		SqlSplitterLine line = lines.get(linesIndex);

		// block begin
		int blockLineNbr = 1;
		blockNumber++;

		logtext("delim top blockBegin" ,linesIndex,blockBegin,"");
		if (blockBegin.getType().isBlockStart()) {
			blockBegin.setBlockType(BlockType.DIRECTIVE);
			blockBegin.setBlockLineNumber(1);
			if (linesIndex + 1 < lines.size()) {
				linesIndex++;
				line = lines.get(linesIndex);
				if (blockBegin.getType().equals(LineType.PROCEDURE_BLOCK_START)) {
					blockBegin.setBlockType(BlockType.STATEMENT_BLOCK);
					statementIndex.put(++statementNumber, linesIndex  );
				}
			}
		}
		else {
			blockBegin.setBlockType(BlockType.STATEMENT);
			statementIndex.put(++statementNumber, linesIndex );
		}

		logtext("\n\ndelimitedBlock preloop ==<",linesIndex,blockBegin,"");
		loop:
			while (linesIndex < lines.size() ) {
				if (blockBegin.getType().equals(LineType.PROCEDURE_BLOCK_START)) {
					if (line.getType().equals(LineType.PROCEDURE_BLOCK_END)) {
						break loop;
					}
				} else {
					if (line.getType().isBlockStart()) {
						break loop;
					}
					if (line.getType().isBlockEnd()) {
						line.setBlockLineNumber(1);
						line.setBlockNumber(++blockNumber);
						line.setBlockType(BlockType.DIRECTIVE);
						break loop;
					}
				}
				logtext(" loop top ",linesIndex,line,"");
				//	logger.debug("blockBegin type {} {}",blockBegin.getType(),blockBegin);
				switch (blockBegin.getBlockType()) {
				case STATEMENT:
					line.setBlockNumber(blockNumber);
					line.setBlockType(BlockType.STATEMENT);
					line.setBlockLineNumber(blockLineNbr++);
					if(	line.getType().isStatementEnd()) {
						logger.debug("isStatementEnd {}",line.getType());
						break loop;
					}
					break;
				case STATEMENT_BLOCK:
					line.setBlockNumber(blockNumber);
					line.setBlockType(BlockType.STATEMENT_BLOCK);
					line.setBlockLineNumber(blockLineNbr++);
					break;
				default:
					if (	blockBegin.getType().isEndType(line.getType())) {
						line.setBlockType(BlockType.DIRECTIVE );
						line.setBlockLineNumber(1);
						break loop;
					} else {
						line.setBlockType(blockType);
						line.setBlockNumber(blockNumber);
						line.setBlockLineNumber(blockLineNbr++);
					}
				}
				logtext(" loop bottom",linesIndex,line,"");
				if (linesIndex + 1 < lines.size()) {
					line = lines.get(++linesIndex);
				} else {
					break loop;
				}
			}
		logtext("delimitedBlock end ==>",linesIndex,lines.get(linesIndex),"\n");
		logger.debug("\n");
		return linesIndex;
	}

	/**
	 * A very simple finite state machine.
	 * 
	 */
	public void analyze() throws SqlSplitterException {
		if (analyzed) {
			return;
		}
		final int NO_BLOCK = 0;
		logger.debug("SqlSplitter proceduresOnly{}", proceduresOnly);

		processLines();

		int state = NO_BLOCK;
		int blockNumber = -1;
		int statementLineNumber = 1;
		// TODO test name no sql
		// TODO exception with line
		logger.debug("SqlSplitter:analyze traceState:{} ", traceState);
		int linesIndex = 0;
		//	for (final SqlSplitterLine srl : lines) {
		while (linesIndex < lines.size())  {
			SqlSplitterLine srl = lines.get(linesIndex);

			logtext("analyze loop",linesIndex,lines.get(linesIndex),"");
			switch (srl.getType()) {
			case COMMENT_BLOCK_BEGIN:   // if (trimmed.startsWith("--#<"))
			case PROCEDURE_BLOCK_START:
				linesIndex = processDelimitedBlock(linesIndex);
				break;

			case MARKDOWN_BLOCK_BEGIN:
			case STATEMENT_NAME:
			case INDETERMINATE:
			case SQL_WITH_SEMICOLON:
				linesIndex = processDelimitedBlock(linesIndex);
				break;
			case BLANK:
				srl.setBlockType(BlockType.IGNORED);
				break;	
			case COMMENT:               // (trimmed.startsWith("--#"))
				srl.setBlockType(BlockType.COMMENT);
				break;
			case SEMICOLON:
				throw new IllegalStateException();
			default:
				break;
			}
			logger.info("linesIndex {}",linesIndex);
			logtext("analyzed",linesIndex,lines.get(linesIndex),"");
			linesIndex++;	
		}
		analyzed = true;
	}

	public ArrayList<String>  getBlockText(int blockIndex) {
		ArrayList<String> retval = new ArrayList<>();
		for (SqlSplitterLine line : lines) {
			if (line.getBlockNumber() == blockIndex)  {
				retval.add(line.getText() );
			}
		}
		return retval;
	}

	public static String stripAnnotations(String in) {
		String[] lines = StringUtils.getLines(in);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (annotationPattern.matcher(in).matches()) {
				continue;
			}
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();

	}

//	private String lineState() {
//		final StringBuilder sb = new StringBuilder();
//		for (final SqlSplitterLine srl : lines) {
//			sb.append(srl.toString());
//			sb.append("\n");
//		}
//		return sb.toString();
//	}
//
//	public String snapshot() throws SqlSplitterException {
//
//		if (lines == null) {
//			throw new IllegalStateException("analyze has not been called");
//		}
//		final StringBuilder sb = new StringBuilder();
//		sb.append(lineState());
//		int i = 1;
//		logger.debug("SqlSplitter snapshot: sqltexts");
//		for (String sqlText : getSqlTexts()) {
//			logger.debug(String.format("#%d\n%s", i++, sqlText));
//		}
//		i = 1;
//		logger.debug("Sqlsplitter sqlStatements");
//		for (SqlStatement ss : getSqlStatements()) {
//			logger.debug(String.format("#%d\n%s", i++, ss.toString()));
//		}
//		return sb.toString();
//	}

	public ArrayList<SqlSplitterLine> getStatementLines(int statementNumber) throws SqlSplitterException {
		analyze();
		final ArrayList<SqlSplitterLine> stmtLines = new ArrayList<>();
		Integer index = statementIndex.get(statementNumber);
		if (index == null) {
			throw new IllegalArgumentException(String.format("no such statementNumber %d in %s",statementNumber,statementIndex));
		}
		SqlSplitterLine ssl = lines.get(index);
		int blockNumber = ssl.getBlockNumber();
		while (ssl.getBlockNumber() == blockNumber) {
			stmtLines.add(ssl);
			if (index == (lines.size() - 1)) {
				break;
			}
			ssl = lines.get(++index);
		}
		return stmtLines;
	}


	String asSqlString(List<SqlSplitterLine> lines) {
		final StringBuilder sb = new StringBuilder();
		SqlSplitterLine firstLine = lines.get(0);
		boolean stmtBlock =  (firstLine.getBlockType().equals(BlockType.STATEMENT_BLOCK));
		int lastIndex = lines.size() - 1;
		for (int i = 0; i <= lastIndex; i++) {
			sb.append(lines.get(i).getText());
			if (i < lastIndex) {
				sb.append("\n");
			}
		}
		String retval = trimSql(sb.toString(),stmtBlock);
		//logger.debug("lines:\n{}\nreturn:{}",lines,retval);
		return retval;
	}



	int getStatementCount() {
		if (statementIndex.size() == 0) {
			logger.debug("statementIndex.size(): 0");
		} else {
			logger.debug("statementIndex.lastKey(): {}",statementIndex.lastKey());
		}
		return statementIndex.size() == 0 ? 0 : statementIndex.lastKey() + 1;
	}

	public ArrayList<String> getSqlTexts() throws SqlSplitterException {
		process();
		final ArrayList<String> sqlTexts = new ArrayList<>();

		final int statementCount = getStatementCount();
		logger.debug("statement count: {}", statementCount);
		for (int i = 0; i < getStatementCount(); i++) {
			final String sql = getSqlText(i);
			sqlTexts.add(sql);
			//	logger.debug("sqlText # {}\n{}",i,sql);
		}
		return sqlTexts;
	}
	//
	//	private void log(String caller, int lineIndex, SqlSplitterLine line) {
	//			logtext(caller,lineIndex,line,"\n");
	//	}
	//
	//	private void lognewline(String caller, int lineIndex, SqlSplitterLine line) {
	//			logtext(caller,lineIndex,line,"");
	//	}

	private void logtext(String caller, int lineIndex, SqlSplitterLine line, String newline) {
		if (traceFlags > 0) {
			String msg = String.format("%-20s %s %s",caller,line, newline);
			logger.info(msg);
		}
	}


	public String formatLines(ArrayList<SqlSplitterLine> lines) {
		StringBuilder sb = new StringBuilder();
		for (SqlSplitterLine line : lines) {
			sb.append(line.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	public String formatLines() {
		return(formatLines(lines));
	}

	String getSqlText(int stmtNumber) throws SqlSplitterException {
		final ArrayList<SqlSplitterLine> lines = getStatementLines(stmtNumber);
		String retval = asSqlString(lines);
		logger.debug("returning {}\n{}", stmtNumber, retval);
		return retval;

	}

	@SuppressWarnings("incomplete-switch")
	public SqlStatement getSqlStatement(List<SqlSplitterLine> lines) {
		String sql = asSqlString(lines);
		SqlStatement ss = new SqlStatement(sql);
		logger.debug("sql:\n{}",sql);
		String name = null;
		for (SqlSplitterLine line : lines) {
			switch (line.getType()) {
			case STATEMENT_NAME:

				String upperText = line.getText().toUpperCase();
				final int index = upperText.indexOf("@NAME ");
				if (name != null) {
					String message = String.format("Duplicate @NAME found on %d and %d,",
							lines.get(0).getLineNumber(),
							line.getLineNumber());
					throw new IllegalArgumentException(message);
				}
				//name = upperText.substring(index + "@NAME ".length()).trim();
				name = line.getText().substring(index + "@NAME ".length()).trim();
				ss.setName(name);
			}
		}
		return ss;
	}

	public SqlStatement getSqlStatement(int statementNumber) {
		return getSqlStatement(getStatementLines(statementNumber));

	}

	public ArrayList<SqlStatement> getSqlStatementList() throws SqlSplitterException {
		process();
		final ArrayList<SqlStatement> statements = new ArrayList<>();
		for (int stmtNbr : statementIndex.keySet()) {
			statements.add(getSqlStatement(stmtNbr));
		}
		return statements;

	}

	public SqlStatements getSqlStatements() throws SqlSplitterException {
		final SqlStatements sqlStatements = new SqlStatements(getSqlStatementList());
		return sqlStatements;
	}

	public String getInputName() {
		return inputType + ":" + inputName;
	}

	public SqlSplitter setProceduresOnly(boolean proceduresOnly) {
		logger.debug("proceduresOnly {} ", proceduresOnly);
		this.proceduresOnly = proceduresOnly;
		return this;
	}

	public SqlSplitter setTraceState(int traceState) {
		if ((traceState < 0) || (traceState > 9)) {
			throw new IllegalArgumentException("must be 0-9");
		}
		this.traceState = traceState;
		return this;

	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public TreeMap<Integer, Integer> getBlockIndex() {
		return blockIndex;
	}

	public TreeMap<Integer, BlockType> getBlockTypeMap() {
		return blockTypeMap;
	}
	public int getVerbosity() {
		return verbosity;
	}

	public SqlSplitter setVerbosity(int verbosity) {
		this.verbosity = verbosity;
		return this;
	}
}
