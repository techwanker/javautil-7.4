package org.javautil.joblog.installer;

import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlSplitterException;

public class JoblogInstaller {

	private Connection conn;

	public JoblogInstaller(Connection conn) {
		this.conn = conn;
	}
	
	public void process() throws SqlSplitterException, Exception {
		Dialect dialect = Dialect.getDialect(conn);
		switch (dialect) {
		case ORACLE:
			JoblogOracleInstall installOracle = new JoblogOracleInstall(conn, true, true);
			installOracle.process();
			break;
		case POSTGRES:
			PostgresInstall installPostgres = new PostgresInstall(conn);
			installPostgres.process();
			break;
		case H2:
			H2Install installH2 = new H2Install(conn);
			installH2.process();
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect");
		}
	}
	
	public void drop() {
		
	}
}
