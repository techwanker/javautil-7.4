package org.javautil.core.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleResultSetIterator implements Iterator<ArrayList<Object>> {

	private ResultSet rset;
	private int       columnCount;

	public SimpleResultSetIterator(ResultSet rset) throws SQLException {
		this.rset = rset;
		this.columnCount = rset.getMetaData().getColumnCount();
	}

	@Override
	public boolean hasNext() {
		try {
			return rset.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<Object> next() {
		ArrayList<Object> retval = new ArrayList<Object>();
		for (int i = 1; i <= columnCount; i++) {
			try {
				retval.add(rset.getObject(i));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return retval;
	}

}
