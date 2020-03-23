
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
@JsonPropertyOrder({ "userName", "URL", "databaseProductName", "databaseProductVersion", "driverName" })
public class DatabaseMetadata {

	@JsonProperty("userName")
	private String              userName;
	@JsonProperty("URL")
	private String              uRL;
	@JsonProperty("databaseProductName")
	private String              databaseProductName;
	@JsonProperty("databaseProductVersion")
	private String              databaseProductVersion;
	@JsonProperty("driverName")
	private String              driverName;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public DatabaseMetadata() {
	}

	/*
	 * 
	 * @param databaseProductName
	 * 
	 * @param uRL
	 * 
	 * @param userName
	 * 
	 * @param driverName
	 * 
	 * @param databaseProductVersion
	 */
	public DatabaseMetadata(String userName, String uRL, String databaseProductName, String databaseProductVersion,
	    String driverName) {
		super();
		this.userName = userName;
		this.uRL = uRL;
		this.databaseProductName = databaseProductName;
		this.databaseProductVersion = databaseProductVersion;
		this.driverName = driverName;
	}

	@JsonProperty("userName")
	public String getUserName() {
		return userName;
	}

	@JsonProperty("userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty("URL")
	public String getURL() {
		return uRL;
	}

	@JsonProperty("URL")
	public void setURL(String uRL) {
		this.uRL = uRL;
	}

	@JsonProperty("databaseProductName")
	public String getDatabaseProductName() {
		return databaseProductName;
	}

	@JsonProperty("databaseProductName")
	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	@JsonProperty("databaseProductVersion")
	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	@JsonProperty("databaseProductVersion")
	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	@JsonProperty("driverName")
	public String getDriverName() {
		return driverName;
	}

	@JsonProperty("driverName")
	public void setDriverName(String driverName) {
		this.driverName = driverName;
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
		return new ToStringBuilder(this).append("userName", userName).append("uRL", uRL)
		    .append("databaseProductName", databaseProductName).append("databaseProductVersion", databaseProductVersion)
		    .append("driverName", driverName).append("additionalProperties", additionalProperties).toString();
	}

}
