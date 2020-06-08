package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.javautil.core.text.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO ensure works with no databases up
public class SqlSplitterTest2 {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	

	//@Ignore
	
	@Test
	public void test_01() {
		String input = "select 'x' from dual; ";
		String expected = "select 'x' from dual";
		assertEquals(expected,SqlSplitter.trimSql(input,false));
	}
	//@Ignore
	@Test
	public void test_02() {
		String input =    "select 'y' from dual  \n;  \n";
		String expected = "select 'y' from dual  \n";
		String actual = SqlSplitter.trimSql(input,false);
		logger.info("expected '{}'",expected);
		logger.info("actual   '{}'",actual);
		assertEquals(expected,actual);
	}
	
	@Test
	public void testStatementBlock() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/statement_block.sr.sql").setProceduresOnly(true);
		sr.process();
		List<String> texts = sr.getSqlTexts();
		assertNotNull(texts);
		logger.debug("texts.get(0) {}", texts.get(0));
		assertEquals(1,texts.size());
	}

//	//@Ignore
//	@Test
//	public void testBig() throws IOException, SqlSplitterException {
//		SqlSplitter sr = new SqlSplitter(this, "testsr/dblogger_install.pks.sr.sql").setProceduresOnly(true);
//		sr.process();
//		List<String> texts = sr.getSqlTexts();
//		assertNotNull(texts);
//		assertEquals(1,texts.size());
//	}

	//@Ignore
	@Test
	public void testShort() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(new File("src/test/resources/testsr/logger_short.sql")).setProceduresOnly(true);
		sr.setTraceState(1);
		sr.process();
		
		int stmt0firstIndex = sr.getStatementIndex().get(0);
		assertEquals(5,stmt0firstIndex);
		ArrayList<SqlSplitterLine> stmtLines = sr.getStatementLines(0);
		//logger.debug("stmtLines:\n{}",stmtLines);
		logger.info("\n{}",sr.formatLines());
		logger.info("stmtLines\n{}",sr.formatLines(stmtLines));
		assertEquals(10,stmtLines.size());
		assertEquals("--%```",stmtLines.get(0).getText());
		assertEquals("END logger;",stmtLines.get(9).getText());
		List<String> texts  = sr.getSqlTexts();
		String stmt0 = texts.get(0);
		logger.debug("stmt0\n{}",stmt0);
		assertEquals(1,texts.size());
		ArrayList<String> text = sr.getBlockText(1);
		logger.info("text ==-\n{}'",text);
		assertNotNull(text);
	}

//	//@Ignore
//	@Test
//	public void testLogger() throws IOException, SqlSplitterException {
//		SqlSplitter sr = new SqlSplitter(new File("src/test/resources/testsr/logger.pks.sr.sql")).setProceduresOnly(true);
//		sr.setTraceState(1);
//		List<String> texts  = sr.getSqlTexts();
//		int stmtNbr = 0;
//		ArrayList<SqlSplitterLine> lines = sr.getStatementLines(0);
//		logger.debug("lines {}",lines);
//		assertEquals(167,lines.size());
//		logger.debug("lines.get(0): {}",lines.get(0));
//		logger.debug("lines.get(0): {}",lines.get(166));
//	}
	
	// @Test
	public void testUtCondition() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/ut_condition_tables.sr.sql").setVerbosity(9);
		sr.setTraceState(1);
		sr.process();
		SqlStatements sqls = sr.getSqlStatements();
		// sr.formatLines();
		assertEquals(6, sqls.size());
	}
    
	//@Ignore
    @Test
	public void testSkipBlockIncr() throws IOException, SqlSplitterException {

		SqlSplitter runner = new SqlSplitter(this, "testsr/skip_block.sr.sql").setVerbosity(9);
		runner.setTraceState(1);
		runner.process();
		
		ArrayList<SqlSplitterLine> stmt2lines = runner.getStatementLines(2);
		assertNotNull(stmt2lines);
		logger.debug("stmt2lines {}",stmt2lines);
		
		SqlStatements sqls = runner.getSqlStatements();
		assertEquals(3,sqls.size());
		assertEquals("select 'x' from dual", sqls.get(0).getSql().trim());
		assertEquals("select 'y' from dual", sqls.get(1).getSql().trim());
		assertEquals("select 'z' from dual", sqls.get(2).getSql().trim());
	}
	
	////@Ignore
    @Test
	public void testSqlWithSemicolon() throws IOException, SqlSplitterException {

		SqlSplitter runner = new SqlSplitter(this, "testsr/dblogger_install_tables.sr.sql").setVerbosity(9);
		runner.setTraceState(1);
		runner.process();
		
		ArrayList<SqlSplitterLine> stmt2lines = runner.getStatementLines(2);
		assertNotNull(stmt2lines);
		logger.debug("stmt2lines {}",stmt2lines);
		
		SqlStatements sqls = runner.getSqlStatements();
		assertEquals(25,sqls.size());
		int ssndx = 0;
		for (SqlStatement ss : sqls) {
			logger.info("ssndx {} {}",ssndx++,ss);
		}
	}
	
	
	
	
	
	//@Ignore
	 @Test
	public void testSkipBlock() throws IOException, SqlSplitterException {

		SqlStatements sqls = new SqlSplitter(this, "testsr/skip_block.sr.sql").getSqlStatements();

		assertEquals("select 'x' from dual", sqls.get(0).getSql().trim());
		assertEquals("select 'y' from dual", sqls.get(1).getSql().trim());
		assertEquals("select 'z' from dual", sqls.get(2).getSql().trim());
		assertEquals(3, sqls.size());
	}

	 @Test
	 public void utCondition() throws FileNotFoundException {
		File    f = new File("src/main/resources/org/javautil/conditionidentification/ut_condition_tables.oracle.sr.sql");
		
		SqlSplitter splitter =  new SqlSplitter(f);
		List<SqlStatement> stmts = splitter.getSqlStatementList();
		logger.info("ut_condition statements\n{}",stmts);
	 }
	 

	 //@Ignore
	@Test
	public void test3() throws IOException, SqlSplitterException {

		SqlStatements sqls = new SqlSplitter(this, "testsr/dblogger_install_tables.sr.sql").getSqlStatements();
		logger.debug("sqls.size() {}", sqls.size());
		String sqls_0 = sqls.get(0).getSql();
		logger.debug("sqls.get(0) {}", sqls_0);
		String sqls_0_0 = sqls_0.split("\n")[0].trim();
		assertEquals("/* Explain Plan */",sqls_0_0);
		
//		assertEquals(sqls.get(0).getSql().trim(), "create sequence job_log_id_seq cache 1000");
//		// logger.debug("sql 1\n{}", sqls.get(1).getSql().trim());
//		assertTrue(sqls.get(1).getSql().trim().startsWith("CREATE TABLE job_log"));
//		assertTrue(sqls.get(1).getSql().trim().endsWith(")"));
//		// logger.debug("sql 2\n{}", sqls.get(2).getSql().trim());
//		String sql = sqls.get(2).getSql();
//		logger.debug("sql {}",sql);
//		String sqlFirst = StringUtils.getFirstLine(sql);
//		String sqlLast = StringUtils.getLastLine(sql);
//		// logger.debug("sql 2 first : " + sqlFirst);
//		// logger.debug("sql 2 last :" + sqlLast);
//		assertEquals("CREATE TABLE job_msg", sqlFirst.trim());
//		assertEquals("references job_log(job_log_id)", sqlLast.trim());

		assertEquals(25, sqls.size());
	}
//	 //@Ignore
//	@Test
//	public void testLoggerPks() throws IOException, SqlSplitterException {
//
//		SqlStatements sqls = new SqlSplitter(this, "testsr/logger.pks.sr.sql").getSqlStatements();
//		logger.debug("sqls.size() {}", sqls.size());
//		String sqls_0 = sqls.get(0).getSql();
//		logger.debug("sqls.get(0) {}", sqls_0);
//		String sqls_0_0 = sqls_0.split("\n")[0].trim();
//		assertEquals("/* Explain Plan */",sqls_0_0);
//
//		assertEquals(25, sqls.size());
//	}

	@Test
	public void testPackageSpec() throws IOException, SqlSplitterException {

		SqlStatements sqls = new SqlSplitter(this, "testsr/package_spec.sr.sql").getSqlStatements();
		logger.debug("sqls.size() {}", sqls.size());
		String sqls_0 = sqls.get(0).getSql();
		logger.debug("sqls.get(0) {}", sqls_0);
		String[] sqllines = sqls_0.split("\n");
		
		String sqls_0_0 = sqllines[0].trim();
		assertEquals("--%```",sqls_0_0);
		String sqls_0_last = sqllines[sqllines.length - 1];
		
		assertEquals("END logger;",sqls_0_last);

		assertEquals(1, sqls.size());
	}
	
}
