
package org.javautil.core.csv.gson;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// TODO does this do anything?  
public class SqlCsvExporterJsonGson {

	@SerializedName("exportDateFormatString")
	@Expose
	private String            exportDateFormatString;
	@SerializedName("dateTimeFormatString")
	@Expose
	private String            dateTimeFormatString;
	@SerializedName("sql")
	@Expose
	private String            sql;
	@SerializedName("databaseMetaData")
	@Expose
	private DatabaseMetadata  databaseMetaData;
	@SerializedName("columnMeta")
	@Expose
	private List<ColumnMetum> columnMeta = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public SqlCsvExporterJsonGson() {
	}

	public SqlCsvExporterJsonGson(String exportDateFormatString, String dateTimeFormatString, String sql,
	    DatabaseMetadata databaseMetaData, List<ColumnMetum> columnMeta) {
		super();
		this.exportDateFormatString = exportDateFormatString;
		this.dateTimeFormatString = dateTimeFormatString;
		this.sql = sql;
		this.databaseMetaData = databaseMetaData;
		this.columnMeta = columnMeta;
	}

	public String getExportDateFormatString() {
		return exportDateFormatString;
	}

	public void setExportDateFormatString(String exportDateFormatString) {
		this.exportDateFormatString = exportDateFormatString;
	}

	public String getDateTimeFormatString() {
		return dateTimeFormatString;
	}

	public void setDateTimeFormatString(String dateTimeFormatString) {
		this.dateTimeFormatString = dateTimeFormatString;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public DatabaseMetadata getDatabaseMetaData() {
		return databaseMetaData;
	}

	public void setDatabaseMetaData(DatabaseMetadata databaseMetaData) {
		this.databaseMetaData = databaseMetaData;
	}

	public List<ColumnMetum> getColumnMeta() {
		return columnMeta;
	}

	public void setColumnMeta(List<ColumnMetum> columnMeta) {
		this.columnMeta = columnMeta;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("exportDateFormatString", exportDateFormatString)
		    .append("dateTimeFormatString", dateTimeFormatString).append("sql", sql)
		    .append("databaseMetaData", databaseMetaData).append("columnMeta", columnMeta).toString();
	}

}
