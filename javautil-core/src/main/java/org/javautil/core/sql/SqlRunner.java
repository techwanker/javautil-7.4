package org.javautil.core.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.javautil.commandline.CommandLineHandler;
import org.javautil.io.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlRunner {
	private final Logger  logger         = LoggerFactory.getLogger(this.getClass());

	private Connection    connection;
	private boolean       continueOnError;
	private boolean       commit         = false;

	private int           verbosity      = 0;
	private String        inputName;
	private String        inputType;
	private boolean       proceduresOnly = false;                                   // blocks only end in
	// "/"
	private SqlSplitter   splitter;
	private SqlStatements statements;

	private boolean       trace          = false;
	private InputStream   splitterInputStream;
	private boolean       showError      = true;

	private boolean       showSql;

	private boolean       commitOrRollbackEveryStatement;
	private int           sqlSplitterTrace;

	public boolean isShowSql() {
		return showSql;
	}

	public SqlRunner setShowSql(boolean showSql) {
		this.showSql = showSql;
		return this;
	}

	public SqlRunner() {

	}

	public SqlRunner(Object instantiator, String resourceName) throws IOException, SqlSplitterException {
		splitterInputStream = ResourceHelper.getResourceAsInputStream(instantiator, resourceName);
		splitter = new SqlSplitter(splitterInputStream);
		splitter.setTraceState(sqlSplitterTrace);
	}

	public SqlRunner(InputStream inputStream) {
		splitterInputStream = inputStream;
		splitter = new SqlSplitter(splitterInputStream);
		splitter.setTraceState(sqlSplitterTrace);
	}

	public SqlRunner(File inputFile) throws IOException, SqlSplitterException {
		splitterInputStream = new FileInputStream(inputFile);
		splitter = new SqlSplitter(splitterInputStream);
		splitter.setTraceState(sqlSplitterTrace);
	}

	public SqlRunner(Connection connection, SqlStatements sqlStatements) {
		this.connection = connection;
		this.statements = sqlStatements;
		logger.debug("statements size: {}", statements.size());
	}

	public SqlRunner(Connection connection, NamedSqlStatements sqlStatements, String... statementNames) {
		this.connection = connection;
		if (sqlStatements == null) {
			throw new IllegalArgumentException("sqlStatements is null");
		}
		ArrayList<SqlStatement> statementsToRun = sqlStatements.getStatements(statementNames);
		statements = new NamedSqlStatements(statementsToRun);
		logger.debug("statements size: {}", statements.size());
	}

	public void processStatements(Connection connection, Binds binds, List<SqlStatement> statements) throws SQLException {

		logger.debug("processStatements: showSql?: {}, continueOnError? {}", showSql, continueOnError);

		int statementNumber = 0;
		for (final SqlStatement ss : statements) {
			statementNumber++;
			ss.setConnection(connection);
			ss.setShowError(showError);
			if (ss.getSql() == null || ss.getSql().trim().length() == 0) {
				throw new IllegalStateException(String.format("sql is '%s'", ss.getSql()));
			}
			String name = ss.getName();
			if (showSql) {
				if (name == null) {
					System.out.println(String.format("-- SqlRunner - #%d --\n%s", statementNumber, ss.getSql()));
				} else {
					System.out.println(String.format("-- SqlRunner - #%d name: '%s'\n%s", statementNumber, name, ss.getSql()));
				}
			}

			try {
				int rc;
				if (proceduresOnly) {
					rc = ss.executeUpdate(connection, null); // nasty hack for quoted : 'yy:mm:dd'
				} else {
					logger.debug("commitEvery:{} about to execute: \n{}", commitOrRollbackEveryStatement, ss.getSql());
					try {
						rc = ss.executeUpdate(binds);
						if (commitOrRollbackEveryStatement) {
							connection.commit();
							logger.debug("commitOrRollbackEveryStatement true, committed");
						}

					} catch (SQLException sqe) {
						if (commitOrRollbackEveryStatement) {
							connection.rollback();
							logger.debug("commitOrRollbackEveryStatement true rolled back");

						}
						logger.error(sqe.getMessage());
						throw sqe;
					}

					if (commitOrRollbackEveryStatement) {
						connection.commit();
					}
				}
				ss.close();
			} catch (final SQLException s) {
//				logger.warn("found an error: processStatements: showError {}", showError);
				final String message = String.format("While processing: \nSQL:'\n%s\n'\n%s", ss.getSql(), s.getMessage());
				if (showError) {
					logger.error(message);
				}
				if (!continueOnError) {
					throw new SQLException(message, s);
				}
			} catch (Exception e) {
				logger.error("How did we get here?");
				logger.warn("found an error: processStatements: showError {}", showError);
				final String message = String.format("While processing: \nSQL:'\n%s\n'\n%s", ss.getSql(), e.getMessage());
				if (showError) {
					logger.error(message);
				}
				if (!continueOnError) {
					throw new SQLException(message, e.getMessage());
				}
			}
		}
		if (commit) {
			connection.commit();
		}
	}

	public void process() throws SQLException {
		process(new Binds());
	}

	public void process(Binds binds) throws SQLException {
		if (statements == null && splitter != null) {
			statements = splitter.getSqlStatements();
		}
		List<SqlStatement> listOfStatements = statements.getStatements();
		processStatements(connection, binds, listOfStatements);
	}

	public SqlRunner setConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}

	public SqlRunner setCommitOrRollbackEveryStatement(boolean commitOrRollbackEveryStatement) {
		this.commitOrRollbackEveryStatement = commitOrRollbackEveryStatement;
		return this;
	}

	public SqlRunner setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
		return this;
	}

	public boolean isCommit() {
		return commit;
	}

	public SqlRunner setCommit(boolean commit) {
		this.commit = commit;
		return this;
	}

	public boolean isProceduresOnly() {
		return proceduresOnly;
	}

	public SqlRunner setProceduresOnly(boolean proceduresOnly) {
		this.proceduresOnly = proceduresOnly;
		splitter.setProceduresOnly(proceduresOnly);
		return this;
	}

	public boolean isShowError() {
		return showError;
	}

	public int getVerbosity() {
		return verbosity;
	}

	public SqlRunner setVerbosity(int verbosity) {
		this.verbosity = verbosity;
		return this;
	}

	public String getInputName() {
		return inputType + ":" + inputName;
	}

	// TODO set LogLevel
	public SqlRunner setShowError(boolean showError) {
		this.showError = showError;
		return this;
	}

	public SqlSplitter getSplitter() {
		return splitter;
	}

	public boolean isCommitOrRollbackEveryStatement() {
		return commitOrRollbackEveryStatement;
	}

	public void process(SqlRunnerArgs args) {
		logger.info("got this far");
	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {
		SqlRunner invocation = new SqlRunner();
		SqlRunnerArgs arguments = new SqlRunnerArgs();
		new CommandLineHandler(arguments).evaluateArguments(args);
		invocation.process(arguments);
	}

	public SqlRunner setSqlSplitterTrace(int sqlSplitterTrace) {
		this.sqlSplitterTrace = sqlSplitterTrace; 
		return this;
	}

}
