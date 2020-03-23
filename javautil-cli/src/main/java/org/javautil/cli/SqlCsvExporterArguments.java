package org.javautil.cli;

import java.io.File;

import org.javautil.commandline.annotations.Required;
import org.javautil.commandline.CommandLineHandler;
import org.javautil.commandline.annotations.Optional;

public class SqlCsvExporterArguments {

	@Optional
	private File outFile;

	@Required
	private String selectStatement;

	@Required
	private String JDBCURL;

	@Required
	private String userNamePassword;




	public File getOutFile() {
		return outFile;
	}

	public void setOutFile(File outFile) {
		this.outFile = outFile;
	}

	public String getSelectStatement() {
		return selectStatement;
	}

	public void setSelectStatement(String selectStatement) {
		this.selectStatement = selectStatement;
	}

	public String getJDBCURL() {
		return JDBCURL;
	}

	public void setJDBCURL(String jDBCURL) {
		JDBCURL = jDBCURL;
	}

	public String getUserNamePassword() {
		return userNamePassword;
	}

	public void setUserNamePassword(String userNamePassword) {
		this.userNamePassword = userNamePassword;
	}

	public void parseArguments(String[] args) {
		new CommandLineHandler(this).evaluateArguments(args);
	}

}