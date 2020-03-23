package org.javautil.dataset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.javautil.core.collections.CollectionFormatter;
import org.javautil.core.sql.Dialect;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.lang.Coerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jjs
 */
public class ColumnMetadata implements ColumnAttributes {

	@Override
	public int hashCode() {
		return Objects.hash(aggregateFunction, attributeName, columnDisplaySize, columnIndex, columnName, columnSize,
				columnType, columnTypeName, comments, dataType, dataTypeName, definitelyNullable, excelFormat, externalFlag,
				groupName, heading, horizontalAlignment, injectedGroupField, javaFormat, label, notNullable, nullable,
				precision, scale, unKnownNullable, workbookFormula);
	}

	@JsonIgnore
	transient private static final Logger logger             = LoggerFactory.getLogger(ColumnMetadata.class);

	private String                 aggregateFunction;
	private String                 attributeName;
	private String                 columnName;
	private Integer                columnIndex;
	private Integer                columnSize;
	private Integer                columnDisplaySize;
	private String                 columnTypeName;
	private Integer                columnType;
	private String                 comments;
	private DataType               dataType;
	private String                 dataTypeName;
	private String                 excelFormat;
	private boolean                externalFlag;
	private String                 groupName;
	private String                 heading;
	private HorizontalAlignment    horizontalAlignment;
	private boolean                injectedGroupField = false;
	private Boolean                nullable;
	private SimpleDateFormat       inputDateFormat;
	private String                 inputDateFormatString;
	private String                 label;
	private String                 javaFormat;
	private Boolean                notNullable;
	private Integer                precision;
	private Integer                scale;
	private String                 workbookFormula;
	// 
	private Boolean                definitelyNullable;
	private Boolean                unKnownNullable;

	@JsonIgnore
	public static final String[]   headings           = new String[] { "columnName", "columnIndex", "dataType", "heading",
			"label", "precision", "scale", "columnDisplaySize", "comments", "isExternalFlag", "attributeName",
			"workbookFormula", "excelFormat", "javaFormat", "groupName", "horizontalAlignment", "aggregateFunction" };

	public ColumnMetadata() {

	}

	public ColumnMetadata(String columnName, Integer columnIndex, String dataTypeName, String heading, String label,
			Integer precision, Integer scale, Integer columnDisplaySize, String comments, boolean externalFlag,
			String attributeName, String workbookFormula, String excelFormat, String javaFormat, String groupName,
			HorizontalAlignment horizontalAlignment, String aggregateFunction, boolean injectedGroupField, Boolean nullable,
			Boolean unKnownNullable, Boolean notNullable, Boolean definitelyNullable, Integer columnSize,
			String columnTypeName, Integer columnType, String inputDateFormatString) {
		super();
		this.columnName = columnName;
		this.columnIndex = columnIndex;
		this.dataTypeName = dataTypeName;
		this.heading = heading;
		this.label = label;
		this.precision = precision;
		this.scale = scale;
		this.columnDisplaySize = columnDisplaySize;
		this.comments = comments;
		this.externalFlag = externalFlag;
		this.attributeName = attributeName;
		this.workbookFormula = workbookFormula;
		this.excelFormat = excelFormat;
		this.javaFormat = javaFormat;
		this.groupName = groupName;
		this.horizontalAlignment = horizontalAlignment;
		this.aggregateFunction = aggregateFunction;
		this.injectedGroupField = injectedGroupField;
		this.nullable = nullable;
		this.unKnownNullable = unKnownNullable;
		this.notNullable = notNullable;
		this.definitelyNullable = definitelyNullable;
		this.columnSize = columnSize;
		this.columnTypeName = columnTypeName;
		this.columnType = columnType;
		this.setInputDateFormatString(inputDateFormatString);
	}

	public ColumnMetadata(ColumnMetadata other) {
		this.columnName = other.columnName;
		this.columnIndex = other.columnIndex;
		this.dataType = other.dataType;
		this.heading = other.heading;
		this.label = other.label;
		this.precision = other.precision;
		this.scale = other.scale;
		this.columnDisplaySize = other.columnDisplaySize;
		this.comments = other.comments;
		this.externalFlag = other.externalFlag;
		this.attributeName = other.attributeName;
		this.workbookFormula = other.workbookFormula;
		this.excelFormat = other.excelFormat;
		this.javaFormat = other.javaFormat;
		this.groupName = other.groupName;
		this.horizontalAlignment = other.horizontalAlignment;
		this.aggregateFunction = other.aggregateFunction;
		this.injectedGroupField = other.injectedGroupField;
		this.nullable = other.nullable;
		this.unKnownNullable = other.unKnownNullable;
		this.notNullable = other.notNullable;
		this.definitelyNullable = other.definitelyNullable;
		this.columnSize = other.columnSize;
		this.columnTypeName = other.columnTypeName;
		this.columnType = other.columnType;
		this.setInputDateFormatString(other.inputDateFormatString);
	}

	public String getHeadingOrColumnName() {
		String retval = heading;
		if (retval == null) {
			logger.debug("Heading is null for " + this.toString() + " using columnName " + columnName);
			retval = columnName;
		}
		return retval;
	}

	public ArrayList<Object> toObjectList() {
		ArrayList<Object> list = new ArrayList<>();
		list.add(columnName);
		list.add(columnIndex);
		list.add(dataType);
		list.add(heading);
		list.add(label);
		list.add(precision);
		list.add(scale);
		list.add(columnDisplaySize);
		list.add(comments);
		list.add(externalFlag ? "Y" : "N");
		list.add(attributeName); // 10
		list.add(workbookFormula); // 11
		list.add(excelFormat); // 112
		list.add(javaFormat); // 13
		list.add(groupName); // 14
		list.add(horizontalAlignment);
		list.add(aggregateFunction);
		list.add(inputDateFormatString);
		return list;
	}

	public ArrayList<String> toStringList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(columnName);
		list.add(columnIndex == null ? null : columnIndex.toString());
		list.add(dataType == null ? null : dataType.toString());
		list.add(heading);
		list.add(label);
		list.add(precision == null ? null : precision.toString());
		list.add(scale == null ? null : scale.toString());
		list.add(columnDisplaySize == null ? null : columnDisplaySize.toString());
		list.add(comments);
		list.add(externalFlag ? "Y" : "N");
		list.add(attributeName); // 10
		list.add(workbookFormula); // 11
		list.add(excelFormat); // 112
		list.add(javaFormat); // 13
		list.add(groupName); // 14
		list.add(horizontalAlignment == null ? null : horizontalAlignment.toString());
		list.add(aggregateFunction);
		list.add(inputDateFormatString);

		return list;
	}

	public String toStringBrief() {
		return "ColumnMetadata [columnName=" + columnName + ", columnIndex=" + columnIndex + ", dataType=" + dataType
				+ ", heading=" + heading + ", label=" + label + ", groupName=" + groupName + "]";
	}

	public static ColumnMetadata fromObjectList(final List<Object> args) {
		Logger logger = LoggerFactory.getLogger(org.javautil.dataset.ColumnMetadata.class);
		final ColumnMetadata cm = new ColumnMetadata();
		cm.columnName = (String) args.get(0);
		cm.columnIndex = (Integer) args.get(1);
		if (args.get(2) != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("dataType: " + args.get(2));
			}
			cm.dataType = (DataType) args.get(2);
		}
		cm.heading = (String) args.get(3);
		cm.label = (String) args.get(4);
		cm.precision = (Integer) args.get(5);
		cm.scale = (Integer) args.get(6);
		cm.columnDisplaySize = (Integer) args.get(7);
		cm.comments = (String) args.get(8);
		if (args.get(9) != null) {
			cm.setExternalFlag(Coerce.asBoolean((String) args.get(9)));
		}
		for (int i = 10; i < args.size(); i++) {
			switch (i) {
			case 10:
				cm.attributeName = (String) args.get(10);
				break;
			case 11:
				cm.workbookFormula = (String) args.get(11);
				break;
			case 12:
				cm.excelFormat = (String) args.get(12);
				break;
			case 13:
				cm.javaFormat = (String) args.get(13);
				break;
			case 14:
				cm.groupName = (String) args.get(14);
				break;
			case 15:
				cm.horizontalAlignment = (HorizontalAlignment) args.get(15);
				break;
			case 16:
				cm.aggregateFunction = (String) args.get(16);
				break;
			case 17:
				cm.setInputDateFormatString((String) args.get(17));
				break;
			default:
				throw new IllegalStateException("at index " + i);
			}
		}
		return cm;
	}

	public static ColumnMetadata fromStringList(final List<String> args) {
		Logger logger = LoggerFactory.getLogger(org.javautil.dataset.ColumnMetadata.class);
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			for (String arg : args) {
				b.append("\"");
				b.append(arg);
				b.append("\" ");

			}
			logger.debug(b.toString());
		}
		final ColumnMetadata cm = new ColumnMetadata();
		cm.columnName = args.get(0);
		cm.columnIndex = getInteger(args.get(1));
		if (args.get(2) != null && args.get(2).length() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("dataType: " + args.get(2));
			}
			try {
				cm.dataType = DataType.valueOf(args.get(2));
			} catch (IllegalArgumentException iae) {
				logger.error("Illegal: '{}'", args.get(2));
				throw iae;
			}
		}
		cm.heading = args.get(3);
		cm.label = args.get(4);
		cm.precision = getInteger(args.get(5));
		cm.scale = getInteger(args.get(6));
		cm.columnDisplaySize = getInteger(args.get(7));
		cm.comments = args.get(8);
		if (args.get(9) != null & args.get(9).length() > 0) {
			if ("Y".equals(args.get(9))) {
				cm.externalFlag = true;
			} else if ("N".equals(args.get(9))) {
				cm.externalFlag = false;
			} else if ("FALSE".equals(args.get(9).toUpperCase())) {
				cm.externalFlag = false;
			} else if ("TRUE".equals(args.get(9).toUpperCase())) {
				cm.externalFlag = true;
			} else {
				throw new IllegalArgumentException("external flag(9)  must be 'Y' or 'N' is '" + args.get(9) + "'");
			}
		}
		for (int i = 10; i < args.size(); i++) {
			switch (i) {
			case 10:
				cm.attributeName = args.get(10);
				break;
			case 11:
				cm.workbookFormula = args.get(11);
				break;
			case 12:
				cm.excelFormat = args.get(12);
				break;
			case 13:
				cm.javaFormat = args.get(13);
				break;
			case 14:
				cm.groupName = args.get(14);
				break;
			case 15:
				if (args.get(15) != null) {
					cm.horizontalAlignment = HorizontalAlignment.valueOf(args.get(15));
				}
				break;
			case 16:
				cm.aggregateFunction = args.get(16);
				break;
			case 17:
				cm.setInputDateFormatString(args.get(17));
				break;
			default:
				throw new IllegalStateException("at index " + i);
			}
		}
		return cm;

	}

	public static Integer getInteger(String text) {
		Integer retval = null;
		if (text != null && text.length() > 0) {
			retval = new Integer(text);
		}
		return retval;
	}

	public List<Object> toList() {
		ArrayList<Object> list = new ArrayList<>();
		list.add(columnName);
		list.add(columnIndex);
		list.add(dataType);
		list.add(heading);
		list.add(label);
		list.add(precision);
		list.add(scale);
		list.add(columnDisplaySize);
		list.add(comments);
		list.add(externalFlag);
		list.add(attributeName); // 10
		list.add(workbookFormula); // 11
		list.add(excelFormat); // 112
		list.add(javaFormat); // 13
		list.add(groupName); // 14
		list.add(horizontalAlignment);
		list.add(aggregateFunction);
		list.add(inputDateFormat);
		return list;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnMetadata other = (ColumnMetadata) obj;
		return Objects.equals(aggregateFunction, other.aggregateFunction)
				&& Objects.equals(attributeName, other.attributeName)
				&& Objects.equals(columnDisplaySize, other.columnDisplaySize) && Objects.equals(columnIndex, other.columnIndex)
				&& Objects.equals(columnName, other.columnName) && Objects.equals(columnSize, other.columnSize)
				&& Objects.equals(columnType, other.columnType) && Objects.equals(columnTypeName, other.columnTypeName)
				&& Objects.equals(comments, other.comments) && dataType == other.dataType
				&& Objects.equals(dataTypeName, other.dataTypeName)
				&& Objects.equals(definitelyNullable, other.definitelyNullable)
				&& Objects.equals(excelFormat, other.excelFormat) && externalFlag == other.externalFlag
				&& Objects.equals(groupName, other.groupName) && Objects.equals(heading, other.heading)
				&& horizontalAlignment == other.horizontalAlignment && injectedGroupField == other.injectedGroupField
				&& Objects.equals(javaFormat, other.javaFormat) && Objects.equals(label, other.label)
				&& Objects.equals(notNullable, other.notNullable) && Objects.equals(nullable, other.nullable)
				&& Objects.equals(precision, other.precision) && Objects.equals(scale, other.scale)
				&& Objects.equals(unKnownNullable, other.unKnownNullable)
				&& Objects.equals(workbookFormula, other.workbookFormula);
	}
	
	String getPrecisionScale(int precision, int scale) {
		  String retval = null;
		  if (scale != 0) {
		  	if (precision != 0 )	 {
		  		retval = String.format("(%d,%d)",precision,scale);
		  	} else {
		  		retval = String.format("(%d)",precision);
		  	}
			} 
			else {
				retval = "";
			}
		  return retval;
	}
	
	String getDataTypeDeclaration(Dialect dialect) {
		switch(dialect) {
		case POSTGRES:
			return getDataTypeDeclarationPostgres();
		case H2:
			return getDataTypeDeclarationOracle();
		case ORACLE:
			return getDataTypeDeclarationOracle();
		case SQLITE:
			return getDataTypeDeclarationOracle();
		case UNSPECIFIED:
		default:
			throw new UnsupportedOperationException("unsupported dialect " + dialect);
		}
	}
	
	String getDataTypeDeclarationOracle() {
		String retval = null;
		int precision = getPrecision();
		int scale = getScale();
		//	if (dataTypeName == null) {
		DataType datatype = getDataType();
		logger.debug("datatype {} columnMeta {} ", dataType, this);
		switch (datatype) {
		case INTEGER: 
			if (precision != 0 || scale != 0  )	 {
				retval = String.format("number(%d,%d)",precision,scale);
			} 
			else {
				retval = "number(9)";
			}
			break;
		case LONG: 
		case SHORT: 
			if (precision != 0 || scale != 0  )	 {
				retval = String.format("number(%d,%d)",precision,scale);
			} 
			else {
				retval = "number(18)";
			}
			break;
		case DOUBLE: 
		case FLOAT: 
		case NUMERIC: 
			retval = "number";
			break;
		case DATE: 
		case SQLDATE: 
			retval = "date";
			break;
		case TIMESTAMP: 
			retval = "timestamp";
			break;
		case BIG_DECIMAL: 
		case BIG_INTEGER: 
			retval = "number";
			break;
		case CLOB: 
			retval = "clob";
			break;
		case VARBINARY: 
			retval = "varbinary";
			break;
		case CHAR: 
			retval = String.format("char(%d)", getColumnDisplaySize());
			break;
		case STRING: 
		case VARCHAR: 
			retval = String.format("varchar(%d)", getColumnDisplaySize());
			break;
		case LONGVARCHAR: 
			retval = String.format("longvarchar(%d)", getColumnDisplaySize());
			break;
		case LONGNVARCHAR: 
			retval = String.format("longnvarchar(%d)", getColumnDisplaySize());
			break;
		case NCHAR: 
			retval = String.format("nchar(%d)", getColumnDisplaySize());
			break;
		case NVARCHAR: 
			retval = String.format("nvarchar(%d)", getColumnDisplaySize());
			break;
		case NCLOB:
			retval = "nclob";
			break;
		default:
			throw new IllegalStateException();
		}
		logger.debug("datatype {}, retval {}  columnMeta {} ", dataType, retval, this);
		return retval;
	}
	
	String getDataTypeDeclarationPostgres() {
		String retval = null;
		int precision = getPrecision();
		int scale = getScale();
		//	if (dataTypeName == null) {
		DataType datatype = getDataType();
		logger.debug("datatype {} columnMeta {} ", dataType, this);
		switch (datatype) {
		case INTEGER: 
			retval = "int";
			break;
		case LONG: 
			retval = "long";
			break;
		case SHORT: 
			retval = "short";
			break;
		case DOUBLE: 
			retval = "double";
			break;
		case FLOAT:   
			retval = "float";
			break;
		case NUMERIC: 
			retval = "numeric " + getPrecisionScale(getPrecision(), getScale());
			break;
		case DATE: 
		case SQLDATE: 
			retval = "date";
			break;
		case TIMESTAMP: 
			retval = "timestamp";
			break;
		case BIG_DECIMAL: 
			retval = "numeric";
			break;
		case BIG_INTEGER: 
			retval = "big_int";
			break;
		case CLOB: 
			retval = "clob";
			break;
		case VARBINARY: 
			retval = "varbinary";
			break;
		case CHAR: 
			retval = String.format("char(%d)", getColumnDisplaySize());
			break;
		case STRING: 
		case VARCHAR: 
			retval = String.format("varchar(%d)", getColumnDisplaySize());
			break;
		case LONGVARCHAR: 
			retval = String.format("longvarchar(%d)", getColumnDisplaySize());
			break;
		case LONGNVARCHAR: 
			retval = String.format("longnvarchar(%d)", getColumnDisplaySize());
			break;
		case NCHAR: 
			retval = String.format("nchar(%d)", getColumnDisplaySize());
			break;
		case NVARCHAR: 
			retval = String.format("nvarchar(%d)", getColumnDisplaySize());
			break;
		case NCLOB:
			retval = "nclob";
			break;
		default:
			throw new IllegalStateException();
		}
		logger.debug("datatype {}, retval {}  columnMeta {} ", dataType, retval, this);
		return retval;
	}
	//
	// with
	//
	/**
	 * @param columnName the columnName to set
	 * @return this
	 */
	public ColumnMetadata withColumnName(String columnName) {
		this.columnName = columnName;
		return this;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 * @return this
	 */
	public ColumnMetadata withColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
		return this;
	}

	/**
	 * @param dataType the dataType to set
	 * @return this
	 */
	public ColumnMetadata withDataType(DataType dataType) {
		this.dataType = dataType;
		return this;
	}

	/**
	 * @param heading the heading to set
	 * @return this
	 */
	public ColumnMetadata withHeading(String heading) {
		this.heading = heading;
		return this;
	}

	/**
	 * @param label the label to set
	 * @return this
	 */
	public ColumnMetadata withLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * @param precision the precision to set
	 * @return this
	 */
	public ColumnMetadata withPrecision(Integer precision) {
		this.precision = precision;
		return this;
	}

	/**
	 * @param scale the scale to set
	 * @return this
	 */
	public ColumnMetadata withScale(Integer scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * @param columnDisplaySize the columnDisplaySize to set
	 * @return this
	 */
	public ColumnMetadata withColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
		return this;
	}

	/**
	 * @param comments the comments to set
	 * @return this
	 */
	public ColumnMetadata withComments(String comments) {
		this.comments = comments;
		return this;
	}

	/**
	 * @param externalFlag the externalFlag to set
	 * @return this
	 */

	public ColumnMetadata withExternalFlag(boolean externalFlag) {
		this.externalFlag = externalFlag;
		return this;
	}

	/**
	 * @param attributeName the attributeName to set
	 * @return this
	 */
	public ColumnMetadata withAttributeName(String attributeName) {
		this.attributeName = attributeName;
		return this;
	}

	/**
	 * @param workbookFormula the workbookFormula to set
	 * @return this
	 */
	public ColumnMetadata withWorkbookFormula(String workbookFormula) {
		this.workbookFormula = workbookFormula;
		return this;
	}

	/**
	 * @param excelFormat the excelFormat to set
	 * @return this
	 */
	public ColumnMetadata withExcelFormat(String excelFormat) {
		this.excelFormat = excelFormat;
		return this;
	}

	/**
	 * @param javaFormat the javaFormat to set
	 * @return this
	 */
	public ColumnMetadata withJavaFormat(String javaFormat) {
		this.javaFormat = javaFormat;
		return this;
	}

	/**
	 * @param groupName the groupName to set
	 * @return this
	 */
	public ColumnMetadata withGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	/**
	 * @param horizontalAlignment the horizontalAlignment to set
	 * @return this
	 */
	public ColumnMetadata withHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	/**
	 * @param aggregateFunction the aggregateFunction to set
	 * @return this
	 */
	public ColumnMetadata withAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
		return this;
	}

	/**
	 * @param injectedGroupField the injectedGroupField to set
	 * @return this
	 */
	public ColumnMetadata withInjectedGroupField(boolean injectedGroupField) {
		this.injectedGroupField = injectedGroupField;
		return this;
	}

	/**
	 * @param nullable the nullable to set
	 * @return this
	 */
	public ColumnMetadata withNullable(Boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	/**
	 * @param unKnownNullable the unKnownNullable to set
	 * @return this
	 */
	public ColumnMetadata withUnKnownNullable(Boolean unKnownNullable) {
		this.unKnownNullable = unKnownNullable;
		return this;
	}

	/**
	 * @param notNullable the notNullable to set
	 * @return this
	 */
	public ColumnMetadata withNotNullable(Boolean notNullable) {
		this.notNullable = notNullable;
		return this;
	}

	/**
	 * @param definitelyNullable the definitelyNullable to set
	 * @return this
	 */
	public ColumnMetadata withDefinitelyNullable(Boolean definitelyNullable) {
		this.definitelyNullable = definitelyNullable;
		return this;
	}

	/**
	 * @param columnSize the columnSize to set
	 * @return this
	 */
	public ColumnMetadata withColumnSize(Integer columnSize) {
		this.columnSize = columnSize;
		return this;
	}

	/**
	 * @param columnTypeName the columnTypeName to set
	 * @return this
	 */
	public ColumnMetadata withColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
		return this;
	}

	/**
	 * @param columnType the columnType to set
	 * @return this
	 */
	public ColumnMetadata withColumnType(Integer columnType) {
		this.columnType = columnType;
		return this;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return the columnIndex
	 */
	public Integer getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		if (dataType == null && dataTypeName != null) {
			return DataType.valueOf(dataTypeName);
		}
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the heading
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the precision
	 */
	public Integer getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	/**
	 * @return the scale
	 */
	public Integer getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	/**
	 * @return the columnDisplaySize
	 */
	public Integer getColumnDisplaySize() {
		return columnDisplaySize;
	}

	/**
	 * @param columnDisplaySize the columnDisplaySize to set
	 */
	public void setColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the externalFlag
	 */
	public boolean isExternalFlag() {
		return externalFlag;
	}

	/**
	 * @param externalFlag the externalFlag to set
	 */
	public void setExternalFlag(boolean externalFlag) {
		this.externalFlag = externalFlag;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the workbookFormula
	 */
	public String getWorkbookFormula() {
		return workbookFormula;
	}

	/**
	 * @param workbookFormula the workbookFormula to set
	 */
	public void setWorkbookFormula(String workbookFormula) {
		this.workbookFormula = workbookFormula;
	}

	/**
	 * @return the excelFormat
	 */
	public String getExcelFormat() {
		return excelFormat;
	}

	/**
	 * @param excelFormat the excelFormat to set
	 */
	public void setExcelFormat(String excelFormat) {
		this.excelFormat = excelFormat;
	}

	/**
	 * @return the javaFormat
	 */
	public String getJavaFormat() {
		return javaFormat;
	}

	/**
	 * @param javaFormat the javaFormat to set
	 */
	public void setJavaFormat(String javaFormat) {
		this.javaFormat = javaFormat;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the horizontalAlignment
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * @param horizontalAlignment the horizontalAlignment to set
	 */
	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 * @return the aggregateFunction
	 */
	public String getAggregateFunction() {
		return aggregateFunction;
	}

	/**
	 * @param aggregateFunction the aggregateFunction to set
	 */
	public void setAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	/**
	 * @return the injectedGroupField
	 */
	public boolean isInjectedGroupField() {
		return injectedGroupField;
	}

	/**
	 * @param injectedGroupField the injectedGroupField to set
	 */
	public void setInjectedGroupField(boolean injectedGroupField) {
		this.injectedGroupField = injectedGroupField;
	}

	/**
	 * @return the nullable
	 */
	public Boolean getNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the unKnownNullable
	 */
	public Boolean getUnKnownNullable() {
		return unKnownNullable;
	}

	/**
	 * @param unKnownNullable the unKnownNullable to set
	 */
	public void setUnKnownNullable(Boolean unKnownNullable) {
		this.unKnownNullable = unKnownNullable;
	}

	/**
	 * @return the notNullable
	 */
	public Boolean getNotNullable() {
		return notNullable;
	}

	/**
	 * @param notNullable the notNullable to set
	 */
	public void setNotNullable(Boolean notNullable) {
		this.notNullable = notNullable;
	}

	/**
	 * @return the definitelyNullable
	 */
	public Boolean getDefinitelyNullable() {
		return definitelyNullable;
	}

	/**
	 * @param definitelyNullable the definitelyNullable to set
	 */
	public void setDefinitelyNullable(Boolean definitelyNullable) {
		this.definitelyNullable = definitelyNullable;
	}

	/**
	 * @return the columnSize
	 */
	public Integer getColumnSize() {
		return columnSize;
	}

	/**
	 * @param columnSize the columnSize to set
	 */
	public void setColumnSize(Integer columnSize) {
		this.columnSize = columnSize;
	}

	/**
	 * @return the columnTypeName
	 */
	public String getColumnTypeName() {
		return columnTypeName;
	}

	/**
	 * @param columnTypeName the columnTypeName to set
	 */
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	/**
	 * @return the columnType
	 */
	public Integer getColumnType() {
		return columnType;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}

	/**
	 * @return the headings
	 */
	public static String[] getHeadings() {
		return headings;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	/**
	 * @param dataTypeName the dataTypeName to set
	 */
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
		// this.dataType = DataType.valueOf(dataTypeName);
	}

	@Override
	public Boolean isNullable() {
		return nullable;
	}

	/**
	 * @return the inputSimpleDateFormat
	 */
	public String getInputSimpleDateFormat() {
		return inputDateFormatString;
	}

	/**
	 * @param inputDateFormatString a SimpleDateFormat String
	 */
	public void setInputDateFormatString(String inputDateFormatString) {
		this.inputDateFormatString = inputDateFormatString;
		inputDateFormat = inputDateFormatString == null ? null : new SimpleDateFormat(inputDateFormatString);
	}

	/**
	 * @return the inputDateFormat
	 */
	public SimpleDateFormat getInputDateFormat() {
		return inputDateFormat;
	}

	public static String toString(Collection<ColumnMetadata> columns) {
		return CollectionFormatter.toString(columns);
	}

	public static int maxColumnNameLength(Collection<ColumnMetadata> columns) {
		int maxLength = -1;
		for (ColumnMetadata col : columns) {
			if (col.getColumnName().length() > maxLength) { 
				maxLength = col.getColumnName().length();
			}
		}
		logger.info("maxColumnNameLength is {} for {}", maxLength, columns);
		return maxLength;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnMetadata [");
		if (columnName != null) {
			builder.append("columnName=");
			builder.append(columnName);
			builder.append(", ");
		}
		if (columnIndex != null) {
			builder.append("columnIndex=");
			builder.append(columnIndex);
			builder.append(", ");
		}
		if (columnDisplaySize != null) {
			builder.append("columnDisplaySize=");
			builder.append(columnDisplaySize);
			builder.append(", ");
		}
		if (columnTypeName != null) {
			builder.append("columnTypeName=");
			builder.append(columnTypeName);
			builder.append(", ");
		}
		if (columnType != null) {
			builder.append("columnType=");
			builder.append(columnType);
			builder.append(", ");
		}
		if (dataType != null) {
			builder.append("dataType=");
			builder.append(dataType);
			builder.append(", ");
		}
		if (dataTypeName != null) {
			builder.append("dataTypeName=");
			builder.append(dataTypeName);
			builder.append(", ");
		}
		if (excelFormat != null) {
			builder.append("excelFormat=");
			builder.append(excelFormat);
			builder.append(", ");
		}
		if (heading != null) {
			builder.append("heading=");
			builder.append(heading);
			builder.append(", ");
		}
		if (horizontalAlignment != null) {
			builder.append("horizontalAlignment=");
			builder.append(horizontalAlignment);
			builder.append(", ");
		}
		if (label != null) {
			builder.append("label=");
			builder.append(label);
			builder.append(", ");
		}
		if (javaFormat != null) {
			builder.append("javaFormat=");
			builder.append(javaFormat);
			builder.append(", ");
		}
		if (precision != null) {
			builder.append("precision=");
			builder.append(precision);
			builder.append(", ");
		}
		if (scale != null) {
			builder.append("scale=");
			builder.append(scale);
			builder.append(", ");
		}
		if (workbookFormula != null) {
			builder.append("workbookFormula=");
			builder.append(workbookFormula);
		}
		builder.append("]");
		String retval = builder.toString();
		logger.debug(retval);
		return retval;
	}
}
