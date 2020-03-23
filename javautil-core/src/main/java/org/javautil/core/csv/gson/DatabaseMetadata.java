
package org.javautil.core.csv.gson;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatabaseMetadata {

	@SerializedName("userName")
	@Expose
	private String userName;
	@SerializedName("URL")
	@Expose
	private String uRL;
	@SerializedName("databaseProductName")
	@Expose
	private String databaseProductName;
	@SerializedName("databaseProductVersion")
	@Expose
	private String databaseProductVersion;
	@SerializedName("driverName")
	@Expose
	private String driverName;

	/**
	 * No args constructor for use in serialization
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getURL() {
		return uRL;
	}

	public void setURL(String uRL) {
		this.uRL = uRL;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("userName", userName).append("uRL", uRL)
		    .append("databaseProductName", databaseProductName).append("databaseProductVersion", databaseProductVersion)
		    .append("driverName", driverName).toString();
	}

}
