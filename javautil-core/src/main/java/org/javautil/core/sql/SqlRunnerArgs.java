package org.javautil.core.sql;

import java.io.File;

import org.javautil.commandline.annotations.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlRunnerArgs {
	private final Logger logger          = LoggerFactory.getLogger(this.getClass());
	private String JDBCURL;
	
	private String       datasourceName;

	private boolean      continueOnError = false;

	@Required
	private File         statementsFile;
	private boolean      commit          = false;

	private int          verbosity       = 0;

	private boolean      showError       = true;

	private boolean      showSql;

	private boolean      commitOrRollbackEveryStatement;

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}

	public void setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
	}

	public File getStatementsFile() {
		return statementsFile;
	}

	public void setStatementsFile(File statementsFile) {
		this.statementsFile = statementsFile;
	}

	public boolean isCommit() {
		return commit;
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

	public int getVerbosity() {
		return verbosity;
	}

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	public boolean isShowError() {
		return showError;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public boolean isCommitOrRollbackEveryStatement() {
		return commitOrRollbackEveryStatement;
	}

	public void setCommitOrRollbackEveryStatement(boolean commitOrRollbackEveryStatement) {
		this.commitOrRollbackEveryStatement = commitOrRollbackEveryStatement;
	}

	public String getJDBCURL() {
		return JDBCURL;
	}

	public void setJDBCURL(String jDBCURL) {
		JDBCURL = jDBCURL;
	}

}
