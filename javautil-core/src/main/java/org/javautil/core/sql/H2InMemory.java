package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2InMemory {

	public static Connection getConnection() {
		try {
			Class.forName("org.h2.Driver");
			final Connection conn = DriverManager.getConnection("jdbc:h2:mem:", "", "");
			return conn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
