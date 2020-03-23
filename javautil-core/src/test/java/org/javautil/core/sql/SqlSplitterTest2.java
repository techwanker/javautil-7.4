package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.javautil.core.text.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO ensure works with no databases up
public class SqlSplitterTest2 {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	// @Test
	public void testBig() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/dblogger_install.pks.sr.sql").setProceduresOnly(true);
		String sql = sr.getSqlStatements().get(0).getSql();
		String trimmed = sql.trim();
		assertTrue(trimmed.startsWith("CREATE OR REPLACE PACKAGE logger AS"));
		assertTrue(trimmed.endsWith("END logger ;"));

	}

	// @Test
	public void testUtCondition() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/ut_condition_tables.sr.sql").setVerbosity(9);
		SqlStatements sqls = sr.getSqlStatements();
		// sr.formatLines();
		assertEquals(6, sqls.size());

	}

	// @Test
	public void testSkipBlockIncr() throws IOException, SqlSplitterException {

		SqlSplitter runner = new SqlSplitter(this, "testsr/skip_block.sr.sql").setVerbosity(9);
		SqlStatements sqls = runner.getSqlStatements();
		assertEquals("select 'x' from dual", sqls.get(0).getSql().trim());
		assertEquals("select 'y' from dual", sqls.get(1).getSql().trim());
		assertEquals("select 'z' from dual", sqls.get(2).getSql().trim());
	}

	// @Test
	public void testSkipBlock() throws IOException, SqlSplitterException {

		SqlStatements sqls = new SqlSplitter(this, "testsr/skip_block.sr.sql").getSqlStatements();

		assertEquals("select 'x' from dual", sqls.get(0).getSql().trim());
		assertEquals("select 'y' from dual", sqls.get(1).getSql().trim());
		assertEquals("select 'z' from dual", sqls.get(2).getSql().trim());
		assertEquals(3, sqls.size());
	}

	// @Test
	public void test2() throws IOException, SqlSplitterException {

		SqlSplitter splitter = new SqlSplitter(this, "testsr/install_sales_reporting_tables.sr.sql");
		splitter.processLines();
		splitter.analyze();
		// logger.debug(splitter.formatLines());
		ArrayList<SqlSplitterLine> splitterLines = splitter.getStatement(1);
		assertEquals(1, splitterLines.size());
		logger.debug("splitterLines");

	}

//	@Test
//	public void testProcedures() throws IOException, SqlSplitterException {
//		SqlSplitter sr = new SqlSplitter(this, "testsr/logger_message_formatter.plsql.sr.sql").setProceduresOnly(true).setTraceState(0);
//		sr.analyze();
////		logger.debug(sr.snapshot());
//		List<String> sqls = sr.getSqlTexts();
//		for (String sql : sqls) {
//			logger.debug(sql);
//		}
//	}

	// @Test
	public void test3() throws IOException, SqlSplitterException {

		SqlStatements sqls = new SqlSplitter(this, "testsr/dblogger_install_tables.sr.sql").getSqlStatements();
		assertEquals(sqls.get(0).getSql().trim(), "create sequence job_log_id_seq cache 1000");
		// logger.debug("sql 1\n{}", sqls.get(1).getSql().trim());
		assertTrue(sqls.get(1).getSql().trim().startsWith("CREATE TABLE job_log"));
		assertTrue(sqls.get(1).getSql().trim().endsWith(")"));
		// logger.debug("sql 2\n{}", sqls.get(2).getSql().trim());
		String sql = sqls.get(2).getSql();
		String sqlFirst = StringUtils.getFirstLine(sql);
		String sqlLast = StringUtils.getLastLine(sql);
		// logger.debug("sql 2 first : " + sqlFirst);
		// logger.debug("sql 2 last :" + sqlLast);
		assertEquals("CREATE TABLE job_msg", sqlFirst.trim());
		assertEquals("references job_log(job_log_id)", sqlLast.trim());

		assertEquals(5, sqls.size());
	}

}
