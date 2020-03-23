package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestTables {
	
	private static final String createTableSql     = "create table x (y number(9))";
	@SuppressWarnings("resource")
	public void createTable(Connection conn) throws SQLException {
		Statement s = conn.createStatement();
		s.execute(createTableSql);

		SqlStatement insertSs = new SqlStatement(conn,"insert into x (y) values(:y)");
		Binds binds = new Binds();
		binds.put("y",1);
		insertSs.executeUpdate(binds);
		binds.put("y",2);
		insertSs.executeUpdate(binds);
		conn.commit();
	}

}
