package org.javautil.dataset;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.javautil.core.misc.Timer;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetToRDBMSTableTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	DataSourceFactory dataSourceFactory = new DataSourceFactory();
	DataSource dataSource = null;
	DataSource destDataSource = DataSourceFactory.getInMemoryDataSource();
	Connection conn;

	@Before 
	public void before() throws SQLException, PropertyVetoException {
		if (dataSource == null) {
			dataSource = dataSourceFactory.getDatasource("integration_postgres");
		}
		conn = dataSource.getConnection();
	}

	@After
	public void after() throws SQLException {
		conn.close();
	}

	@Ignore
	@Test
	public void testCreatestatement() throws SQLException {
		Dataset ds = getDataset();
		String tableCreate = DatasetToRdbmsTable.toPaddedTable("test_table", ds);
		logger.debug("tableCreate\n{}", tableCreate);
	}				
	@Ignore
	@Test
	public void testInsertStatement() throws SQLException {
		Dataset ds = getDataset();
		// This hack is necessary for postgres who wants to know the type of the null
		String tableInsert = DatasetMetadataImpl.getInsertStatement("test_table",ds.getMetadata(),true);
		logger.debug("testInsertStatement\n{}", tableInsert);
	}				

	@Test
	public void testToPostgress() throws SQLException {
		ListOfNameValueDataset ds = getDataset();
		Connection conn = destDataSource.getConnection();
		//	DatasetToRdbmsTable tableCreator = new DatasetToRdbmsTable();

		String drop_table = "drop table test_table";


		String tableCreate = DatasetToRdbmsTable.toPaddedTable("test_table", ds);
		Statement stmt = conn.createStatement();
		try {
			stmt.execute(drop_table);
		}
		catch (SQLException sqe) {
			logger.warn(sqe.getMessage());
			conn.rollback();
		}
		stmt.execute(tableCreate);

		String tableInsert = DatasetMetadataImpl.getInsertStatement("test_table",ds.getMetadata(),true);
		SqlStatement insert = new SqlStatement(conn,tableInsert);
		int rowCount = 0;
		DatasetIterator<?> di = ds.getDatasetIterator();
		logger.debug("columnMetaData:\n{}",ColumnMetadata.toString(ds.getMetadata().getColumnMetadata()));
		Timer timer = new Timer();
		Timer runningTimer = new Timer();
		while (di.hasNext()) {

			di.next();
			Map<String,Object> map= di.getRowAsMap();
			Map <String,Integer> sqlTypeMap = ds.getMetadata().getSqlTypeMap();
			Binds binds = new Binds(map);
			binds.putAllTypes(sqlTypeMap);

			logger.debug("about to execute with binds {}", binds.toString());
			//		insert.execute(binds);
			runningTimer.resume();
			insert.addBatch(binds);
			runningTimer.pause();

			rowCount++;
		}
		runningTimer.resume();
		insert.executeBatch();
		runningTimer.pause();
		logger.debug("time {} millis", timer.getElapsedMillis());
		logger.debug("running time {} micros", runningTimer.getRunningMicros());
		conn.commit();
		conn.close();
		assertEquals(ds.size(),rowCount);
		logger.debug("Row Count {} ms {} rows/ms ",
				runningTimer.getRunningMicros() / 1000);
	}

	public ListOfNameValueDataset getDataset() throws SQLException {
		SqlStatement ss = new SqlStatement(conn, "select * from etl_sale where :etl_file_id = :etl_file_id ");
		Binds binds = new Binds();
		binds.put("etl_file_id",1);
		ListOfNameValueDataset ds = ss.getListOfNameValueDataSet(binds);
		return ds;
	}

}