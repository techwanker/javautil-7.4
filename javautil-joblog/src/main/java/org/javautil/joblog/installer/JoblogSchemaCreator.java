package org.javautil.joblog.installer;

import java.sql.SQLException;

import org.javautil.core.sql.SqlSplitterException;

public interface JoblogSchemaCreator {

	void process() throws Exception, SqlSplitterException;

	JoblogSchemaCreator setDrop(boolean drop);

	JoblogSchemaCreator setNoFail(boolean noFail);

	JoblogSchemaCreator setShowSql(boolean showSql);

	JoblogSchemaCreator setDryRun(boolean showSql);

	boolean isDrop();

	void dropObjects() throws SQLException;

}