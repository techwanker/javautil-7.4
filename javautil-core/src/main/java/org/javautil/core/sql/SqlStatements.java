package org.javautil.core.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class SqlStatements implements Iterable<SqlStatement> {

	private final Logger              logger               = LoggerFactory.getLogger(this.getClass());

	// TODO this should go away only by name should exist
	private List<SqlStatement>        statements           = new ArrayList<>();

	private Map<String, SqlStatement> sqlStatementByName   = null;

	private boolean                   showStatementsOnLoad = false;

	private boolean                   showSqlAndBindsOnExecute;

	private Connection                connection;
	private String                    yamlFileName;

	//
	// Constructors
	//
	public SqlStatements() {

	}

	//
	public SqlStatements(Connection conn) {
		this.connection = conn;
	}

	public SqlStatements(File file, Connection conn) throws FileNotFoundException {
		this.yamlFileName = file.getAbsolutePath();
		FileInputStream fis = new FileInputStream(yamlFileName);
		loadYamlMap(fis);
		this.connection = conn;

	}

	public SqlStatements(File file) throws FileNotFoundException {
		this.yamlFileName = file.getAbsolutePath();
		FileInputStream fis = new FileInputStream(yamlFileName);
		loadYamlMap(fis);
	}

	public SqlStatements(InputStream is, Connection conn) {
		this.connection = conn;
		loadYamlMap(is);
	}

	public SqlStatements(String yamlFileName, Connection conn) throws IOException {
		this.connection = conn;
		this.yamlFileName = yamlFileName;

		final File f = new File(yamlFileName);
		if (!f.exists()) {
			throw new FileNotFoundException("no such file:\n" + yamlFileName + "\n" + f.getAbsolutePath());
		}
		final InputStream ios = new FileInputStream(new File(yamlFileName));
		loadYamlMap(ios);
		ios.close();
	}

	public SqlStatements(Connection conn, List<SqlStatement> statements) {
		setStatements(statements);
		setConnection(conn);
	}

	public SqlStatements(List<SqlStatement> statements) {
		setStatements(statements);
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;

	}

	protected void setStatements(List<SqlStatement> statements) {
		this.statements = statements;
	}

	public List<Exception> close() {
		final List<Exception> exceptions = new ArrayList<>();
		for (final SqlStatement sh : statements) {
			try {
				sh.close();
			} catch (final SQLException se) {
				exceptions.add(se);
			}
		}
		return exceptions;
	}

	public String getYamlFileName() {
		return yamlFileName;
	}

	@Override
	public Iterator<SqlStatement> iterator() {
		return statements.iterator();
	}

	public void add(SqlStatement stmt) {
		statements.add(stmt);
	}

	public void processStatements(Connection conn, Binds binds) throws SQLException {
		for (final SqlStatement stmt : statements) {
			stmt.executeUpdate(conn, binds);
		}
	}

	SqlStatements withColonBinds() {
		final SqlStatements retval = new SqlStatements(this.connection);
		final ArrayList<SqlStatement> colonStatements = new ArrayList<>();
		for (final SqlStatement statement : statements) {
			SqlStatement newStatement = statement;
			final String oldSql = statement.getSql();
			final String colonSql = SqlStatement.toColonBinds(statement.getSql());
			if (!oldSql.equals(colonSql)) {
				newStatement = new SqlStatement(statement);
				newStatement.setSql(colonSql);
				final String message = String.format("converted:\n%s\nto\n %s\", " + statement.getSql(), newStatement.getSql());
				logger.debug(message);
			}

			colonStatements.add(newStatement);
		}
		colonStatements.trimToSize();
		retval.setStatements(this.statements);
		return retval;
	}

	void closeAll() throws SQLException {
		for (final SqlStatement statement : statements) {
			if (statement.isPrepared()) {
				statement.close();
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final SqlStatement ss : statements) {
			sb.append(ss.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public SqlStatement getSqlStatement(String statementName) throws SQLException {
		SqlStatement retval = sqlStatementByName.get(statementName);

		if (retval == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("no such statement ");
			sb.append(statementName);
			sb.append(" in ");
			sb.append(sqlStatementByName.keySet());
			String message = sb.toString();
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		if (retval.getConnection() == null && connection != null) {
			retval.setConnection(connection);
		}
		return retval;

	}

	// TODO needs test
	void loadYamlMap(InputStream is) {
		Yaml yaml = new Yaml();
		LinkedHashMap<String, Map<String, Object>> sqlMap = yaml.load(is);
		sqlStatementByName = new LinkedHashMap<String, SqlStatement>();
		for (Entry<String, Map<String, Object>> e : sqlMap.entrySet()) {
			SqlStatement ess = new SqlStatement(e.getValue());
			String name = e.getKey();
			ess.setName(name);
			if (showStatementsOnLoad) {
				logger.debug("loadYamlMap adding name {} {}", name, ess);
			}
			sqlStatementByName.put(name, ess);
		}
		logger.debug("sqlStatementByName has {} entries {}", sqlStatementByName.size(), sqlStatementByName.keySet());
	}

//	// TODO this should go away just use the map format
//	@Deprecated
//	void loadYamlList(InputStream is) {
//		Yaml yaml = new Yaml();
//		List<Map<String, Object>> sqlList = (List<Map<String, Object>>) yaml.load(is);
//		sqlStatementByName = new LinkedHashMap<String, SqlStatement>();
//		for (Map<String, Object> e : sqlList) {
//			SqlStatement ess = new SqlStatement(e);
//			sqlStatementByName.put(ess.getName(), ess);
//		}
//	}

	public SqlStatement get(int i) {
		return statements.get(i);
	}

	public int size() {

		return statements.size();
	}

	public List<SqlStatement> getStatements() {
		return statements;
	}

	/**
	 * @return the sqlStatementByName
	 */
	public Map<String, SqlStatement> getSqlStatementByName() {
		return sqlStatementByName;
	}

	public void setShowStatementsOnLoad(boolean showStatementsOnLoad) {
		this.showStatementsOnLoad = showStatementsOnLoad;
	}

	/**
	 * @return the showStatementsOnLoad
	 */
	public boolean isShowStatementsOnLoad() {
		return showStatementsOnLoad;
	}

	/**
	 * @return the showSqlAndBindsOnExecute
	 */
	public boolean isShowSqlAndBindsOnExecute() {
		return showSqlAndBindsOnExecute;
	}

	/**
	 * calls setShowSql for each SqlStatement.
	 * 
	 * Does not effect subsequently added statements.
	 * 
	 * @param showSqlAndBindsOnExecute the showSqlAndBindsOnExecute to set
	 */
	public void setShowSqlAndBindsOnExecute(boolean showSqlAndBindsOnExecute) {
		this.showSqlAndBindsOnExecute = showSqlAndBindsOnExecute;
		if (sqlStatementByName != null) {
			for (SqlStatement s : sqlStatementByName.values()) {
				s.setShowSql(showSqlAndBindsOnExecute);
			}
		}
	}
}
