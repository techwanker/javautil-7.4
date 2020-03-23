
package org.javautil.core.csv.jackson;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "columnName", "dataType", "precision", "scale", "columnDisplaySize", "externalFlag",
    "injectedGroupField", "columnTypeName", "columnType", "headingOrColumnName" })
public class ColumnMetum {

	@JsonProperty("columnName")
	private String              columnName;
	@JsonProperty("dataType")
	private String              dataType;
	@JsonProperty("precision")
	private Integer             precision;
	@JsonProperty("scale")
	private Integer             scale;
	@JsonProperty("columnDisplaySize")
	private Integer             columnDisplaySize;
	@JsonProperty("externalFlag")
	private Boolean             externalFlag;
	@JsonProperty("injectedGroupField")
	private Boolean             injectedGroupField;
	@JsonProperty("columnTypeName")
	private String              columnTypeName;
	@JsonProperty("columnType")
	private Integer             columnType;
	@JsonProperty("headingOrColumnName")
	private String              headingOrColumnName;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public ColumnMetum() {
	}

	/*
	 * @param scale
	 * 
	 * @param injectedGroupField
	 * 
	 * @param dataType
	 * 
	 * @param precision
	 * 
	 * @param columnType
	 * 
	 * @param columnName
	 * 
	 * @param headingOrColumnName
	 * 
	 * @param columnTypeName
	 * 
	 * @param externalFlag
	 * 
	 * @param columnDisplaySize
	 */
	public ColumnMetum(String columnName, String dataType, Integer precision, Integer scale, Integer columnDisplaySize,
	    Boolean externalFlag, Boolean injectedGroupField, String columnTypeName, Integer columnType,
	    String headingOrColumnName) {
		super();
		this.columnName = columnName;
		this.dataType = dataType;
		this.precision = precision;
		this.scale = scale;
		this.columnDisplaySize = columnDisplaySize;
		this.externalFlag = externalFlag;
		this.injectedGroupField = injectedGroupField;
		this.columnTypeName = columnTypeName;
		this.columnType = columnType;
		this.headingOrColumnName = headingOrColumnName;
	}

	@JsonProperty("columnName")
	public String getColumnName() {
		return columnName;
	}

	@JsonProperty("columnName")
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@JsonProperty("dataType")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonProperty("precision")
	public Integer getPrecision() {
		return precision;
	}

	@JsonProperty("precision")
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	@JsonProperty("scale")
	public Integer getScale() {
		return scale;
	}

	@JsonProperty("scale")
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@JsonProperty("columnDisplaySize")
	public Integer getColumnDisplaySize() {
		return columnDisplaySize;
	}

	@JsonProperty("columnDisplaySize")
	public void setColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	@JsonProperty("externalFlag")
	public Boolean getExternalFlag() {
		return externalFlag;
	}

	@JsonProperty("externalFlag")
	public void setExternalFlag(Boolean externalFlag) {
		this.externalFlag = externalFlag;
	}

	@JsonProperty("injectedGroupField")
	public Boolean getInjectedGroupField() {
		return injectedGroupField;
	}

	@JsonProperty("injectedGroupField")
	public void setInjectedGroupField(Boolean injectedGroupField) {
		this.injectedGroupField = injectedGroupField;
	}

	@JsonProperty("columnTypeName")
	public String getColumnTypeName() {
		return columnTypeName;
	}

	@JsonProperty("columnTypeName")
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	@JsonProperty("columnType")
	public Integer getColumnType() {
		return columnType;
	}

	@JsonProperty("columnType")
	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}

	@JsonProperty("headingOrColumnName")
	public String getHeadingOrColumnName() {
		return headingOrColumnName;
	}

	@JsonProperty("headingOrColumnName")
	public void setHeadingOrColumnName(String headingOrColumnName) {
		this.headingOrColumnName = headingOrColumnName;
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
		return new ToStringBuilder(this).append("columnName", columnName).append("dataType", dataType)
		    .append("precision", precision).append("scale", scale).append("columnDisplaySize", columnDisplaySize)
		    .append("externalFlag", externalFlag).append("injectedGroupField", injectedGroupField)
		    .append("columnTypeName", columnTypeName).append("columnType", columnType)
		    .append("headingOrColumnName", headingOrColumnName).append("additionalProperties", additionalProperties)
		    .toString();
	}

}
