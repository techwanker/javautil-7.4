
package org.javautil.core.workbook;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bind {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("dataType")
	@Expose
	private String dataType;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public Bind() {
	}

	/*
	 * @param dataType
	 * 
	 * @param name
	 */
	public Bind(String name, String dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bind withName(String name) {
		this.name = name;
		return this;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Bind withDataType(String dataType) {
		this.dataType = dataType;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("dataType", dataType).toString();
	}

}
