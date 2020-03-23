package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO finish writing tests
public class SqlStatementsTest {

	private SqlStatements statements;
	private Logger        logger = LoggerFactory.getLogger(getClass());

	public SqlStatementsTest() {
	}

	@Before
	public void before() throws FileNotFoundException {
		File etlPersistenceFile = new File("src/test/resources/etl_persistence_colon.yaml");
		statements = new SqlStatements(etlPersistenceFile);
	}

	@Test
	public void testSequence() throws SQLException {
		SqlStatement seqStatement = statements.getSqlStatement("etl_file_id_seq");
		assertNotNull(seqStatement);
		logger.info("seqStatement {}", seqStatement);
		System.out.println("seqStatement " + seqStatement);
		assertEquals("etl_file_id_seq", seqStatement.getSql());
		assertTrue(seqStatement.isSequence());
	}

	@Test
	public void testH2Sequence() throws SQLException {
		Connection conn = DataSourceFactory.getInMemoryDataSourceSingletonConnection();
		Statement s = conn.createStatement();
		s.execute("create sequence etl_file_id_seq");

		SqlStatement ss = new SqlStatement();
		ss.setSql("ETL_FILE_ID_SEQ");
		ss.setSequence(true);
		ss.setConnection(conn);
		long value = ss.nextval();
		assertEquals(1, value);

		ss.setSql("etl_file_id_seq");
		ss.setSequence(true);
		ss.setConnection(conn);
		value = ss.nextval();
		assertEquals(2, value);

		conn.close();

	}
}
