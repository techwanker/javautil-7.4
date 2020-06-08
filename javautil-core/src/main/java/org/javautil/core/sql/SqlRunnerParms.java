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

public class SqlRunnerParms {
	private final transient Logger  logger         = LoggerFactory.getLogger(this.getClass());

	private Connection    connection;
	private InputStream   splitterInputStream;
	private String        inputStreamDescription;
	private int           verbosity      = 0;
	private boolean       continueOnError;
	private boolean       showError      = true;
	private boolean       showSql;
	private boolean       commitOrRollbackEveryStatement;
	private boolean       showSqlSplitterOnError = true;
	private boolean commit;
	
	public SqlRunnerParms(Connection connection, InputStream splitterInputStream, String inputStreamDescription,
			int verbosity) {
		super();
		this.connection = connection;
		this.splitterInputStream = splitterInputStream;
		this.inputStreamDescription = inputStreamDescription;
		this.verbosity = verbosity;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getInputStreamDescription() {
		return inputStreamDescription;
	}

	public InputStream getSplitterInputStream() {
		return splitterInputStream;
	}

	public int getVerbosity() {
		return verbosity;
	}

	public boolean isCommitOrRollbackEveryStatement() {
		return commitOrRollbackEveryStatement;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}

	public boolean isShowError() {
		return showError;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public boolean isShowSqlSplitterOnError() {
		return showSqlSplitterOnError;
	}

	public SqlRunnerParms  setCommitOrRollbackEveryStatement(boolean commitOrRollbackEveryStatement) {
		this.commitOrRollbackEveryStatement = commitOrRollbackEveryStatement;
		return this;
	}

	public SqlRunnerParms setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
		return this;
	}

	public SqlRunnerParms  setShowError(boolean showError) {
		this.showError = showError;
		return this;
	}

	public SqlRunnerParms setShowSql(boolean showSql) {
		this.showSql = showSql;
		return this;
	}

	public SqlRunnerParms setShowSqlSplitterOnError(boolean showSqlSplitterOnError) {
		this.showSqlSplitterOnError = showSqlSplitterOnError;
		return this;
	}

	public boolean isCommit() {
		return commit;
	}
}
	
	