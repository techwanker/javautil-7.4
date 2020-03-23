
package org.javautil.core.csv.gson;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ColumnMetum {

	@SerializedName("columnName")
	@Expose
	private String  columnName;
	@SerializedName("dataType")
	@Expose
	private String  dataType;
	@SerializedName("precision")
	@Expose
	private Integer precision;
	@SerializedName("scale")
	@Expose
	private Integer scale;
	@SerializedName("columnDisplaySize")
	@Expose
	private Integer columnDisplaySize;
	@SerializedName("externalFlag")
	@Expose
	private Boolean externalFlag;
	@SerializedName("injectedGroupField")
	@Expose
	private Boolean injectedGroupField;
	@SerializedName("columnTypeName")
	@Expose
	private String  columnTypeName;
	@SerializedName("columnType")
	@Expose
	private Integer columnType;
	@SerializedName("headingOrColumnName")
	@Expose
	private String  headingOrColumnName;

	/**
	 * No args constructor for use in serialization
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

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Integer getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public Boolean getExternalFlag() {
		return externalFlag;
	}

	public void setExternalFlag(Boolean externalFlag) {
		this.externalFlag = externalFlag;
	}

	public Boolean getInjectedGroupField() {
		return injectedGroupField;
	}

	public void setInjectedGroupField(Boolean injectedGroupField) {
		this.injectedGroupField = injectedGroupField;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public Integer getColumnType() {
		return columnType;
	}

	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}

	public String getHeadingOrColumnName() {
		return headingOrColumnName;
	}

	public void setHeadingOrColumnName(String headingOrColumnName) {
		this.headingOrColumnName = headingOrColumnName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("columnName", columnName).append("dataType", dataType)
		    .append("precision", precision).append("scale", scale).append("columnDisplaySize", columnDisplaySize)
		    .append("externalFlag", externalFlag).append("injectedGroupField", injectedGroupField)
		    .append("columnTypeName", columnTypeName).append("columnType", columnType)
		    .append("headingOrColumnName", headingOrColumnName).toString();
	}

}
