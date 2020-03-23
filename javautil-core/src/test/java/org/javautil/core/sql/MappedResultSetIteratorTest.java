package org.javautil.core.sql;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.javautil.util.NameValue;

public class MappedResultSetIteratorTest {

	// TODO this does nothing and tests nothing
	public void testOne() throws PropertyVetoException, SQLException {
		HashMap<String, Object> parms = new HashMap<>();
		parms.put("driver_class", "org.sqlite.JDBC");
		parms.put("url", "sqlite::memory:");
		DataSource ds = DataSourceFactory.getDatasource(parms);
		Connection conn = ds.getConnection();
		String sql = "select 'x' x union select 'y' ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		MappedResultSetIterator it = new MappedResultSetIterator(rs);
		for (Iterator<NameValue> iter = it.iterator(); it.hasNext();) {
		}
	}

}
