
package org.javautil.core.csv.jackson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "exportDateFormatString", "dateTimeFormatString", "sql", "databaseMetaData", "columnMeta" })
public class SqlCsvExporterJson {

	@JsonProperty("exportDateFormatString")
	private String              exportDateFormatString;
	@JsonProperty("dateTimeFormatString")
	private String              dateTimeFormatString;
	@JsonProperty("sql")
	private String              sql;
	@JsonProperty("databaseMetaData")
	private DatabaseMetadata    databaseMetaData;
	@JsonProperty("columnMeta")
	private List<ColumnMetum>   columnMeta           = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public SqlCsvExporterJson() {
	}

	/**
	 * 
	 * @param exportDateFormatString SimpleDateFormat
	 * @param databaseMetaData       DatabaseMetadata
	 * @param columnMeta             columnMeta
	 * @param sql                    sql
	 * @param dateTimeFormatString   TODO
	 */
	public SqlCsvExporterJson(String exportDateFormatString, String dateTimeFormatString, String sql,
	    DatabaseMetadata databaseMetaData, List<ColumnMetum> columnMeta) {
		super();
		this.exportDateFormatString = exportDateFormatString;
		this.dateTimeFormatString = dateTimeFormatString;
		this.sql = sql;
		this.databaseMetaData = databaseMetaData;
		this.columnMeta = columnMeta;
	}

	@JsonProperty("exportDateFormatString")
	public String getExportDateFormatString() {
		return exportDateFormatString;
	}

	@JsonProperty("exportDateFormatString")
	public void setExportDateFormatString(String exportDateFormatString) {
		this.exportDateFormatString = exportDateFormatString;
	}

	@JsonProperty("dateTimeFormatString")
	public String getDateTimeFormatString() {
		return dateTimeFormatString;
	}

	@JsonProperty("dateTimeFormatString")
	public void setDateTimeFormatString(String dateTimeFormatString) {
		this.dateTimeFormatString = dateTimeFormatString;
	}

	@JsonProperty("sql")
	public String getSql() {
		return sql;
	}

	@JsonProperty("sql")
	public void setSql(String sql) {
		this.sql = sql;
	}

	@JsonProperty("databaseMetaData")
	public DatabaseMetadata getDatabaseMetaData() {
		return databaseMetaData;
	}

	@JsonProperty("databaseMetaData")
	public void setDatabaseMetaData(DatabaseMetadata databaseMetaData) {
		this.databaseMetaData = databaseMetaData;
	}

	@JsonProperty("columnMeta")
	public List<ColumnMetum> getColumnMeta() {
		return columnMeta;
	}

	@JsonProperty("columnMeta")
	public void setColumnMeta(List<ColumnMetum> columnMeta) {
		this.columnMeta = columnMeta;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("exportDateFormatString", exportDateFormatString)
		    .append("dateTimeFormatString", dateTimeFormatString).append("sql", sql)
		    .append("databaseMetaData", databaseMetaData).append("columnMeta", columnMeta)
		    .append("additionalProperties", additionalProperties).toString();
	}

}
