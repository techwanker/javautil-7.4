package org.javautil.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PrepareConnection {

	private static final String prepareSql = "begin prepare_connection end;";

	/**
	 * Prepares a connection obtained from a connection pool for use.
	 * 
	 * This clears all context variables and resets packages so a connection should
	 * be in the same state coming from a connection pool as new connection.
	 * 
	 * @param connection the connection to be prepared
	 * @throws SQLException in the event of failure
	 */
	public static void prepare(Connection connection) throws SQLException {
		CallableStatement prepareStatement = connection.prepareCall(prepareSql);
		prepareStatement.execute();
	}
}
