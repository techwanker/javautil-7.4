package org.javautil.core.jdbc.metadata.dto;

import java.util.HashMap;

import org.javautil.core.jdbc.metadata.Column;
import org.javautil.core.jdbc.metadata.renderer.XmlMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.dom4j.Element;

public class JsonSchemaColumn {
	transient private Logger                         logger        = LoggerFactory.getLogger(getClass());
	transient private String                         tableName;
	private String                                   columnName;

	private String                                   columnTypeName;
	/**
	 * For char or date types this is the maximum number of characters<br/>
	 * for numeric or decimal types this is precision.
	 * 
	 * Populated from DatabaseMetaData.getColumns()
	 */
	private Integer                                  columnSize;

	private Integer                                  precision;
	/**
	 * Number of fractional digits if the number is positive
	 */
	private int                                      scale;
	/** the number of fractional digits */
	// private int decimalDigits;
	/** Radix (typically either 10 or 2) */
	private int                                      numberPrecisionRadix;
	// private int javaType = -1;
	// private final String javaObjectType = null;

	/**
	 * is NULL allowed? Assumable values
	 * <ul>
	 * <li>0 - java.sql.DatabaseMetaData.columnNoNulls - might not allow NULL
	 * values</li>
	 * <li>1 - java.sql.DatabaseMetaData.columnNullable - definitely allows NULL
	 * values</li>
	 * <li>2 - java.sql.DatabaseMetaData.columnNullableUnknown - nullability
	 * unknown</li>
	 * </ul>
	 */
	private Boolean                                  nullable;

	// private int nullable;
	/*
	 * comment describing column (may be null)
	 * 
	 * TODO this is also comments;
	 */
	private String                                   remarks;
	/**
	 * default value (may be null)
	 */
	private String                                   defaultValue;

	/**
	 * For char types the maximum number of bytes in the column.
	 */
	// private int charOctetLength;
	/**
	 * index of column in table (starting at 1).
	 */
	// private int ordinalPosition;
	// IS_NULLABLE String => "NO" means column definitely does not allow NULL
	// values; "YES" means the column might allow NULL values. An empty string
	// means nobody knows.

	/**
	 * Java acceptable name for an attribute for this columnName.
	 */
	private String                                   attributeName = null;
	/**
	 * Same as attributeName with first letter capitalized.
	 * 
	 * Used for example in "get" + getAttributeNameInitCap() in generating
	 * accessors.
	 */
	// private final String attributeNameInitCap = null;

//	private String sqlType = null;
	/**
	 * Database catalog.
	 */
	// private String catalog;

	transient private static HashMap<String, String> typeMap       = new HashMap<>();

	public JsonSchemaColumn(final Column c) {
		tableName = c.getTableName();
		if (typeMap == null) {
			initMap();
		}
		columnName = c.getColumnName();
		// dataType = rs.getInt("DATA_TYPE");
		columnTypeName = getJsonType(c.getColumnTypeName());
		// columnSize =
		// decimalDigits = rs.getInt("DECIMAL_DIGITS");
		scale = c.getScale();
		numberPrecisionRadix = c.getNumberPrecisionRadix();
		nullable = c.isNullable();
		remarks = c.getRemarks();
		defaultValue = c.getDefaultValue();
		// charOctetLength = rs.getInt("CHAR_OCTET_LENGTH");
		// ordinalPosition = rs.getInt("ORDINAL_POSITION");
		attributeName = c.getAttributeName();

	}

	/**
	 * is NULL allowed? Assumable values
	 * <ul>
	 * <li>0 - java.sql.DatabaseMetaData.columnNoNulls - might not allow NULL
	 * values</li>
	 * <li>1 - java.sql.DatabaseMetaData.columnNullable - definitely allows NULL
	 * values</li>
	 * <li>2 - java.sql.DatabaseMetaData.columnNullableUnknown - nullability
	 * unknown</li>
	 * </ul>
	 * 
	 * @param val per above
	 */
	public void setNullable(final int val) {
		switch (val) {
		case 0:
			nullable = Boolean.FALSE;
			break;
		case 1:
			nullable = Boolean.TRUE;
			break;
		case 2:
			nullable = null;
			break;
		default:
			throw new IllegalArgumentException("must be 0,1 or 2 see jdbc MetaData spec");
		}
	}

	public void setNullable(final String val) {
		if ("N".equals(val)) {
			nullable = Boolean.FALSE;
		} else if ("Y".equals(val)) {
			nullable = Boolean.TRUE;
		} else if (XmlMeta.NULL_ALLOWED.equals(val)) {
			nullable = Boolean.TRUE;
		} else if (XmlMeta.NOT_NULL.equals(val)) {
			nullable = Boolean.FALSE;
		} else if (val == null) {
			nullable = null;
		} else {
			throw new IllegalArgumentException("expected '" + XmlMeta.NULL_ALLOWED + "' |  '" + XmlMeta.NOT_NULL
			    + "' | null | 'Y' | 'N' but got '" + val + "'");
		}
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setColumnTypeName(String typeName) {
		this.columnTypeName = typeName;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public int getNumberPrecisionRadix() {
		return numberPrecisionRadix;
	}

	public void setNumberPrecisionRadix(int numberPrecisionRadix) {
		this.numberPrecisionRadix = numberPrecisionRadix;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getComments() {
		return remarks;
	}

	public Boolean isNullable() {
		return nullable;
	}

	public void setComments(String comments) {
		this.remarks = comments;

	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	private void initMap() {
		if (typeMap == null) {
			typeMap = new HashMap<>();
			typeMap.put("VARCHAR", "String");
			typeMap.put("VARCHAR2", "String");
			typeMap.put("CHAR", "String");
			typeMap.put("NUMBER", "Number");
		}
	}

	String getJsonType(String columnTypeName) {
		String retval = typeMap.get(columnTypeName);
		if (retval == null) {
			String message = String.format("Unknown type for table %s column %s type %s", tableName, columnName,
			    columnTypeName);
			logger.warn(message);
		}
		return retval;
	}

}
