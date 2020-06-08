package org.javautil.joblog.installer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TOD collapse these installers
public class PostgresInstall extends AbstractDbloggerDatabaseObjectsInstaller implements CreateDbloggerDatabaseObjects {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Connection connection;

	public PostgresInstall(Connection conn) throws SQLException {

		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		Dialect dialect = Dialect.getDialect(conn);
		logger.info("dialect is {}",dialect);
		if (Dialect.getDialect(conn).equals(Dialect.POSTGRES)) {
			connection = conn;
		} else {
			throw new IllegalArgumentException("not a postgresql connection " + conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dblogging.CreateDbloggerDatabaseObjects#process()
	 */
	@Override
	public void process() throws Exception {
		if (isDrop()) {
			dropObjects();
		}
//		SqlStatement ss = new SqlStatement(connection,"create schema joblob");
//		ss.execute(new Binds());
		SqlStatement ss;
		ss = new SqlStatement(connection,"set search_path to joblog");
		ss.execute(new Binds());
				
		logger.info("creating logger tables for postgr");
		final String createTablesResource = "ddl/postgresql/job_tables.sr.sql";
		logger.info("about to call SqlRunner showSql {}", showSql);

		new SqlRunner(this, createTablesResource).setConnection(connection).setShowSql(showSql)
				.setContinueOnError(noFail).process();
		logger.info("SqlRunner complete");
		connection.commit();
		System.out.println("Postgres :process logger tables installed");
	}

	@Override
	public void dropObjects() throws SQLException {
         String dropSchema = "drop schema joblog cascade";
         SqlStatement ss = new SqlStatement(connection,dropSchema);
         ss.execute(new Binds());
         String createSchema = "create schema joblog";
         ss = new SqlStatement(connection,createSchema);
         ss.execute(new Binds());
		//final Statement dropAll = connection.createStatement();
		//System.out.println("drop everything");
		//logger.info("drop everything");
		//dropAll.execute("DROP ALL OBJECTS");
		//System.out.println("All objects  dropped not datafile!!!!");

	}

	@Override
	public PostgresInstall setDrop(boolean drop) {
		this.drop = drop;
		return this;
	}

	@Override
	public PostgresInstall setNoFail(boolean noFail) {
		this.noFail = noFail;
		return this;
	}

	@Override
	public PostgresInstall setShowSql(boolean showSql) {
		this.showSql = showSql;
		return this;
	}

	@Override
	public boolean isDrop() {
		return drop;
	}

	@Override
	public PostgresInstall setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
		return this;
	}

}
