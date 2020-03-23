package org.javautil.core.sql;

public class SqlStatementFormatterImpl {
	public String format(SqlStatement statement) {
		return String.format("SqlStatement [name='%s', sql=\n'%s'\n, returning=%s\n, \ndescription=%s]",
		    statement.getName(), statement.getSql(), statement.getDescription());
	}
}
