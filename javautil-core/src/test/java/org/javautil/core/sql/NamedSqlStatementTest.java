package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedSqlStatementTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testAnnotatedName() throws IOException, SqlSplitterException {
		final SqlSplitter sr = new SqlSplitter(this, "testsr/etl_persistence.named.sr.sql");
		sr.setTraceState(1);
		sr.process();
		logger.info("statementIndex {}", sr.getStatementIndex());
		assertEquals(0,sr.getStatementIndex().get(0).intValue());
		assertEquals(69,sr.getStatementIndex().get(8).intValue());
		String sql1 = sr.getSqlText(0);
		logger.info("sql:\n{}",sql1);
		final SqlStatements ss = sr.getSqlStatements();
		assertNotNull(ss);
		assertEquals(9,ss.size());
//
//		final NamedSqlStatements named = new NamedSqlStatements(ss);
//		final SqlStatement custInsert = named.getSqlStatement("etl_customer_tot_insert");
//		assertNotNull(custInsert);
//		logger.info("custInsert: {}",custInsert);
//		final int index = custInsertSql.indexOf(":ETL_FILE_ID,  :LINE_NUMBER, :CUSTOMER_COUNT");
//		assertTrue(index > -1);

	}

	@Test
	public void testAnnotatedNameShort() throws IOException, SqlSplitterException {
		String resourceName = "testsr/etl_persistence.named.sr.sql";
		final SqlSplitter sr = new SqlSplitter(this, resourceName);
		sr.setTraceState(1);
		final SqlStatements splitterSS = sr.getSqlStatements();
		assertNotNull(splitterSS);
		int i  = 0;
		for (SqlStatement stmt : splitterSS) {
			logger.info("ndx: {}\n{}",i,stmt);
		}
		
		
		final NamedSqlStatements named = NamedSqlStatements.getNameSqlStatementsFromSqlSplitterResource(this, resourceName);
		logger.info("named size {}", named.size());
		for (SqlStatement ss : named) {
			logger.info("name {}", ss.getName());
		}
		final SqlStatement custTotInsert = named.getSqlStatement("etl_customer_tot_insert");
		assertNotNull(custTotInsert);
		final String custTotInsertSql = custTotInsert.getSql();
		final int index = custTotInsertSql.indexOf(":ETL_FILE_ID,  :LINE_NUMBER, :CUSTOMER_COUNT");
		assertTrue(index > -1);
		assertEquals(9, named.size());

	}
}
