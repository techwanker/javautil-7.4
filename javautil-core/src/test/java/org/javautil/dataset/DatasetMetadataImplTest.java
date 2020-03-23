package org.javautil.dataset;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.ResultSetMetaDataCache;
import org.javautil.core.sql.SqlStatement;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetMetadataImplTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testOne() throws SQLException {
		Connection conn = DataSourceFactory.getInMemoryDataSourceSingletonConnection();
		String sql = "create table a (a_id bigint auto_increment, descr varchar(2))";
		SqlStatement ss = new SqlStatement(conn, sql);
		ss.execute(new Binds());

		SqlStatement q = new SqlStatement(conn, "select * from a");
		q.setNeedsResultSetMetaDataCache(true);
		q.getListOfNameValue();
		ResultSetMetaDataCache cache = q.getResultSetMetaDataCache();
		DatasetMetadataImpl dmi = new DatasetMetadataImpl(cache);
		logger.info("dmi {}", dmi);
		// TODO test

	}
}
