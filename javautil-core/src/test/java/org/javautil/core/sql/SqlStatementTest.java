package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.javautil.core.collections.CollectionsUtil;
import org.javautil.dataset.ListOfNameValueDataset;
import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlStatementTest {

	static final Logger logger = LoggerFactory.getLogger(SqlStatementTest.class);
	static boolean      setup  = false;
	private Connection  connection;

	@Before
	public void before() throws IOException, SQLException, SqlSplitterException {
		connection = DataSourceFactory.getInMemoryDataSourceSingletonConnection();
		assertNotNull(connection);
		createSeededDatabase();
	}

	@After
	public void after() throws SQLException, IOException {
		connection.close();
	}

	public void createSeededDatabase() throws IOException, SQLException, SqlSplitterException {
		logger.debug("****creating database");
		runner("h2/ut_condition_tables.sr.sql");
		runner("h2/sales_reporting_ddl.sr.sql");
		seedDatabase(connection);
	}

	//
	// Fetch Tests

	@Test
	public void listOfNameValueTest() throws SQLException, IOException, SqlSplitterException {
		final SqlStatement orgStatement = new SqlStatement(connection, "select * from org order by org_cd");
		final ListOfNameValue rows = orgStatement.getListOfNameValue(new Binds());
		final NameValue row = rows.get(0);
		// tests
		assertEquals(18, rows.size());
		assertEquals("CADBURY", row.get("org_cd"));
		logger.debug("row: " + row);
	}


	@Test(expected = DataNotFoundException.class)
	public void nameValueNoRowsTest() throws SQLException {
		final SqlStatement orgStatement = new SqlStatement(connection, "select * from org where org_cd = :org_cd");
		final Binds binds = new Binds();
		binds.put("org_cd", "NOT FOUND");
		orgStatement.getNameValue(binds, true);
		orgStatement.close();
	}

	@Test(expected=TooManyRowsException.class)
	public void nameValueTooManyRowsTest() throws SQLException {
		final SqlStatement orgStatement = new SqlStatement(connection, "select * from org");
		orgStatement.getNameValue();
		orgStatement.close();
	}

	@Test public void SqlStatementAccessorsTest() throws SQLException {
		final String orgsSql =  "select * from org order by org_cd";
		final SqlStatement ss = new SqlStatement(connection, orgsSql);
		assertTrue(connection == ss.getConnection());
		assertTrue(orgsSql == ss.getSql());
	}

	@Test
	public void nameValueColonTest() throws SQLException, IOException, SqlSplitterException {
		final SqlStatement orgsStatement = new SqlStatement(connection, "select * from org order by org_cd");
		ListOfNameValue orgsList = orgsStatement.getListOfNameValue();
		logger.debug("orgsList {}",CollectionsUtil.asString(orgsList));
		assertTrue(orgsList.size() > 0);

		final SqlStatement orgStatement = new SqlStatement(connection, "select * from org where org_cd = :org_cd");
		final Binds binds = new Binds();
		binds.put("org_cd", "HOSTESS");
		final NameValue row = orgStatement.getNameValue(binds, true);
		orgStatement.close();
		// tests
		assertEquals("HOSTESS", row.get("org_cd"));
		logger.debug("row: " + row);
	}


	@Test
	public void listOfListsTest() throws SQLException, IOException, SqlSplitterException {

		final SqlStatement orgStatement = new SqlStatement(connection, "select * from org where org_cd = :org_cd");
		final Binds binds = new Binds();
		binds.put("org_cd", "CADBURY");
		final ListOfLists rows = orgStatement.getListOfLists(binds);
		orgStatement.close();
		// tests
		assertEquals(1, rows.size());
		ArrayList<? extends Object> row = rows.get(0);
		long expected = 12;
		assertEquals(expected, row.get(0));
		assertEquals("CADBURY", row.get(1));
		logger.debug("row: " + row);
	}


	@Test
	public void batchingTest() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("create table psycho (psychoId integer auto_increment, name varchar(32))");

		SqlStatement ss = new SqlStatement(connection, "insert into psycho (name) values (:name)");
		Binds binds = new Binds();
		binds.put("name", "Dahlmer");
		ss.addBatch(binds);
		binds.put("name", "Manson");
		ss.addBatch(binds);
		ss.executeBatch();

		logger.debug("about to select");
		SqlStatement sel = new SqlStatement(connection, "select * from psycho order by name desc");
		ListOfNameValue lonv = sel.getListOfNameValue();
		assertEquals(2, lonv.size());
		assertEquals("Manson", lonv.get(0).get("name"));
		logger.debug("lonv {}", lonv);
	}

	// @Test // TODO this does not work with H2
	public void executeUpdateGetGeneratedKeyTest() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("create table psycho (psychoId integer auto_increment, name varchar(32))");

		SqlStatement ss = new SqlStatement(connection, "insert into psycho (name) values (:name)");
		Binds binds = new Binds();
		binds.put("name", "Dahlmer");
		ListOfNameValue keys = ss.executeUpdateGetGeneratedKeysTemp(binds);

		logger.debug("about to select");
		SqlStatement sel = new SqlStatement(connection, "select * from psycho");
		ListOfNameValue lonv = sel.getListOfNameValue();
		logger.debug("lonv {}", lonv);

		keys = ss.executeUpdateGetGeneratedKeys(binds);
		logger.debug("keys {}", keys);
		assertNotNull(keys);
		assertEquals(1, keys.size());
	}


	@Test
	public void toQuestionTest() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("create table vend (org_cd varchar(8), org_nm varchar(32))");
		final String insertSql = "insert into vend (org_cd, org_nm)  values (%(ORG_CD)s, %(ORG_NM)s)";
		final String expected = "insert into vend (org_cd, org_nm)  values (?, ?)";
		final List<String> binds = SqlStatement.findPythonBinds(insertSql);
		assertNotNull(binds);
		assertEquals(2, binds.size());
		assertTrue(binds.contains("ORG_CD"));
		assertTrue(binds.contains("ORG_NM"));
		SqlStatement ss = new SqlStatement(insertSql);
		final String result = ss.toQuestionBinds(insertSql);
		assertEquals(expected, result);

		// insert
		SqlStatement ss2 = new SqlStatement(connection, insertSql);
		Binds b = new Binds();
		b.put("ORG_CD", "AA");
		b.put("ORG_NM", "Name");
		ss2.execute(b);
		ss2.close();

		// fetch it
		SqlStatement ssq = new SqlStatement(connection, "select * from vend");
		ListOfNameValue lonv = ssq.getListOfNameValue(new Binds());
		assertEquals(1, lonv.size());
		NameValue nv = lonv.get(0);
		logger.debug("nv is {}", nv);
		System.out.println(String.format("nv is %s", nv.toString()));
		assertEquals("AA", nv.get("org_cd"));
		assertEquals("Name", nv.getInsensitive("ORG_NM"));
		ssq.close();
		// TODO

		logger.debug("result is: " + result);
	}

	@Test
	public void testFormat() {
		String sql = "select 'x' from dual";
		SqlStatement ss = new SqlStatement(sql);
		String expected = String.format("sql:\n{}", sql);
		String actual = ss.format();
		logger.debug("formatted:\n{}", actual);
		//assertEquals(expected,actual);
	}

	void runner(String ddlResourceName) throws SQLException, IOException, SqlSplitterException {
		logger.debug("about to create sales tables " + ddlResourceName);
		if (connection == null) {
			throw new IllegalStateException("connection is null");
		}
		final SqlRunner runner = new SqlRunner(this, ddlResourceName).setCommit(false).
				setConnection(connection).setShowSql(true).setSqlSplitterTrace(1);
		runner.process();
	}

	public void seedDatabase(Connection connection) throws SQLException, IOException, SqlSplitterException {
		final SeedSalesReportingDatabase seeder = new SeedSalesReportingDatabase(connection);
		seeder.process(true);
	}


	@Test
	public void toQuestionBindsTest() {
		String input = "--@name wtf\ncreate sequence woop";
		SqlStatement ss = new SqlStatement(input);
		ss.setTrace(true);
		String output = ss.toQuestionBinds(input);
		assertEquals(input, output);
	}

	@Test
	public void testAccessors() {
		SqlStatement s = new SqlStatement();
		s.setShowSql(true);
		assertEquals(true, s.isShowSql());

		s.setShowSql(false);
		assertEquals(false, s.isShowSql());
	}

	@Test
	public void testConstructor() {
		SqlStatement ss = new  SqlStatement("name", "sql", "description", "narrative"); 
		assertEquals("name",ss.getName());
		assertEquals("sql",ss.getSql());
		assertEquals("description",ss.getDescription());
		assertEquals("narrative",ss.getNarrative());

		ss = new  SqlStatement("name2", "sql2"); 
		assertEquals("name2",ss.getName());
		assertEquals("sql2",ss.getSql());
	}

	@Test
	public void getListOfNameValueDataset() throws SQLException {
		Connection conn = DataSourceFactory.getInMemoryDataSourceSingletonConnection();
		TestTables testTables = new TestTables();
		testTables.createTable(conn);

		SqlStatement ss = new SqlStatement(conn, "select * from x ");
		ListOfNameValueDataset ds = ss.getListOfNameValueDataSet(new Binds());
		assertEquals(2,ds.size());

	}

}
