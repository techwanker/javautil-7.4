package org.javautil.oracle.dto;

/**
 * Contains a temporal representation of the data persisted in a tuple of
 * sys.v$open_cursor Persistence management is supported in class
 * OpenCursorBase. This code was generated by com.javautil.JavaGen on Sat Jul 12
 * 23:39:04 CDT 2003
 */
public class OpenCursor {

	// class attributes
	/** the container for the data persisted in SADDR */
	private byte[] saddr;

	/** the container for the data persisted in SID */
	private long sid;

	/** the container for the data persisted in USER_NAME */
	private String userName;

	/** the container for the data persisted in ADDRESS */
	private byte[] address;

	/** the container for the data persisted in HASH_VALUE */
	private long hashValue;

	/** the container for the data persisted in SQL_TEXT */
	private String sqlText;

	public byte[] getAddress() {
		return address;
	}

	public long getHashValue() {
		return hashValue;
	}

	public byte[] getSaddr() {
		return saddr;
	}

	public long getSid() {
		return sid;
	}

	public String getUserName() {
		return userName;
	}

	public void setAddress(final byte[] val) {
		address = val;
	}

	public void setHashValue(final long val) {
		hashValue = val;
	}

	public void setSaddr(final byte[] val) {
		saddr = val;
	}

	public void setSid(final long val) {
		sid = val;
	}

	public void setSqlText(final String val) {
		sqlText = val;
	}

	public void setUserName(final String val) {
		userName = val;
	}

	public String getSqlText() {
		return sqlText;
	}

}
