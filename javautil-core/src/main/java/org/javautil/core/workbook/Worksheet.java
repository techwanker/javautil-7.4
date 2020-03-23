
package org.javautil.core.workbook;

import java.util.List;

import org.javautil.core.text.StringBuilderHelper;
import org.javautil.document.crosstab.CrosstabColumns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Worksheet {

	private transient final Logger logger          = LoggerFactory.getLogger(getClass());

	@SerializedName("name")
	@Expose
	private String                 name;
	@SerializedName("sql")
	@Expose
	private String                 sql;
	@SerializedName("connectionName")
	@Expose
	private String                 connectionName;
	@SerializedName("columns")
	@Expose
	private List<WorkbookColumn>           columns         = null;
	private CrosstabColumns        crosstabColumns = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Worksheet() {
	}

	/**
	 * @param sql            The query
	 * @param name           worksheet name
	 * @param connectionName name of the DataSource
	 * @param crosstabColumns the column names to be used for crosstabbing
	 * @param columns        column definitions
	 */
	public Worksheet(String name, String sql, String connectionName, List<WorkbookColumn> columns,
	    CrosstabColumns crosstabColumns) {
		super();
		this.name = name;
		this.sql = sql;
		this.connectionName = connectionName;
		this.columns = columns;
		this.crosstabColumns = crosstabColumns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Worksheet withName(String name) {
		this.name = name;
		return this;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Worksheet withSql(String sql) {
		this.sql = sql;
		return this;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public Worksheet withConnectionName(String connectionName) {
		this.connectionName = connectionName;
		return this;
	}

	public List<WorkbookColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<WorkbookColumn> columns) {
		this.columns = columns;
	}

	public Worksheet withColumns(List<WorkbookColumn> columns) {
		this.columns = columns;
		return this;
	}

	public CrosstabColumns getCrosstabColumns() {
		return crosstabColumns;
	}

	public void setCrosstabColumns(CrosstabColumns crosstabColumns) {
		this.crosstabColumns = crosstabColumns;
	}

	@Override
	public String toString() {
		StringBuilderHelper sb = new StringBuilderHelper();
		sb.addNameValue("name", name).addNameValue("sql", sql).addNameValue("connectionName", connectionName)
		    .addNameValue("columns", columns);
		String retval = sb.toString();
		logger.info("toString:\n{}", retval);
		return sb.toString();
	}
}
