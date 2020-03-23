//package org.javautil.dataset;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import org.javautil.core.text.AsString;
//import org.javautil.document.style.HorizontalAlignment;
//import org.javautil.properties.BooleanPropertyHelper;
//import org.javautil.properties.IllegalPropertyValueException;
//import org.javautil.properties.IntegerPropertyHelper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonSetter;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * @author jjs@dbexperts.com
// */
//public class ColumnMetadata implements ColumnAttributes {
//
//	@JsonIgnore
//	transient private final Logger logger = LoggerFactory.getLogger(getClass());
//	
//	@JsonIgnore
//	transient public static final String newline = System.getProperty("line.separator");
//	private String columnName;
//	private Integer columnIndex;
//	private DataType dataType;
//	private String heading;
//	private String label;
//	private Integer precision;
//	private Integer scale;
//	private Integer columnDisplaySize;
//	private String comments;
//	private boolean externalFlag;
//	private String attributeName;
//	private String workbookFormula;
//	private String excelFormat;
//	private String javaFormat;
//	private String groupName;
//	private HorizontalAlignment horizontalAlignment;
//	private String aggregateFunction;
//	/**
//	 * If this field is the result of a group operation that was injected back into
//	 * identifying columns tuple this is set to true;
//	 */
//	private boolean injectedGroupField = false;
//	private Boolean nullable;
//	private Boolean unKnownNullable;
//	private Boolean notNullable;
//	private Boolean definitelyNullable;
//	private Integer columnSize;
//	private String columnTypeName;
//	private Integer columnType;
//
//	@JsonIgnore
//	public static final String[] headings = new String[] { "columnName", "columnIndex", "dataType", "heading", "label",
//			"precision", "scale", "columnDisplaySize", "comments", "isExternalFlag", "attributeName", "workbookFormula",
//			"excelFormat", "javaFormat", "groupName", "horizontalAlignment", "aggregateFunction" };
//
////	public ColumnMetadata(String columnName, Integer columnIndex, DataType dataType, String heading, String label,
////			Integer precision, Integer scale, Integer columnDisplaySize, String comments, boolean externalFlag,
////			String attributeName, String workbookFormula, String excelFormat, String javaFormat, String groupName,
////			HorizontalAlignment horizontalAlignment, String aggregateFunction, boolean injectedGroupField,
////			Boolean nullable, Boolean unKnownNullable, Boolean notNullable, Boolean definitelyNullable,
////			Integer columnSize, String columnTypeName, Integer columnType) {
////		super();
////		this.columnName = columnName;
////		this.columnIndex = columnIndex;
////		this.dataType = dataType;
////		this.heading = heading;
////		this.label = label;
////		this.precision = precision;
////		this.scale = scale;
////		this.columnDisplaySize = columnDisplaySize;
////		this.comments = comments;
////		this.externalFlag = externalFlag;
////		this.attributeName = attributeName;
////		this.workbookFormula = workbookFormula;
////		this.excelFormat = excelFormat;
////		this.javaFormat = javaFormat;
////		this.groupName = groupName;
////		this.horizontalAlignment = horizontalAlignment;
////		this.aggregateFunction = aggregateFunction;
////		this.injectedGroupField = injectedGroupField;
////		this.nullable = nullable;
////		this.unKnownNullable = unKnownNullable;
////		this.notNullable = notNullable;
////		this.definitelyNullable = definitelyNullable;
////		this.columnSize = columnSize;
////		this.columnTypeName = columnTypeName;
////		this.columnType = columnType;
////	}
//	public ColumnMetadata() {
//
//	}
//	
//	public ColumnMetadata(String excelFormat) {
//		this.excelFormat = excelFormat;
//	}
//	public ColumnMetadata(ColumnMetadata other) {
//		this.columnName = other.columnName;
//		this.columnIndex = other.columnIndex;
//		this.dataType = other.dataType;
//		this.heading = other.heading;
//		this.label = other.label;
//		this.precision = other.precision;
//		this.scale = other.scale;
//		this.columnDisplaySize = other.columnDisplaySize;
//		this.comments = other.comments;
//		this.externalFlag = other.externalFlag;
//		this.attributeName = other.attributeName;
//		this.workbookFormula = other.workbookFormula;
//		this.excelFormat = other.excelFormat;
//		this.javaFormat = other.javaFormat;
//		this.groupName = other.groupName;
//		this.horizontalAlignment = other.horizontalAlignment;
//		this.aggregateFunction = other.aggregateFunction;
//		this.injectedGroupField = other.injectedGroupField;
//		this.nullable = other.nullable;
//		this.unKnownNullable = other.unKnownNullable;
//		this.notNullable = other.notNullable;
//		this.definitelyNullable = other.definitelyNullable;
//		this.columnSize = other.columnSize;
//		this.columnTypeName = other.columnTypeName;
//		this.columnType = other.columnType;
//	}
//
//
//	public ColumnMetadata(final String columnName, final DataType dataType) {
//		super();
//		this.columnName = columnName;
//		this.dataType = dataType;
//	}
//
//	public ColumnMetadata(final String columnName, final Integer columnIndex, final DataType dataType,
//			final Integer precision, final Integer scale, final Integer columnDisplaySize) {
//		super();
//		this.columnName = columnName;
//		this.columnIndex = columnIndex;
//		this.dataType = dataType;
//		this.precision = precision;
//		this.scale = scale;
//		this.columnDisplaySize = columnDisplaySize;
//	}
//
//	public ColumnMetadata(final String columnName, final Integer columnIndex, final DataType dataType,
//			final Integer precision, final Integer scale, final Integer columnDisplaySize,
//			final HorizontalAlignment horizontalAlignment, final String excelFormat, final String javaFormat) {
//		super();
//		this.columnName = columnName;
//		this.columnIndex = columnIndex;
//		this.dataType = dataType;
//		this.precision = precision;
//		this.scale = scale;
//		this.columnDisplaySize = columnDisplaySize;
//		this.horizontalAlignment = horizontalAlignment;
//		this.excelFormat = excelFormat;
//		this.javaFormat = javaFormat;
//	}
//
//	/*
//	 * @param columnName
//	 * 
//	 * @param columnIndex
//	 * 
//	 * @param dataType
//	 * 
//	 * @param heading
//	 * 
//	 * @param columnDisplaySize
//	 * 
//	 * @param workbookFormula
//	 * 
//	 * @param excelFormat
//	 * 
//	 * @param javaFormat
//	 * 
//	 * @param horizontalAlignment
//	 * 
//	 * @param aggregateFunction
//	 */
//	public ColumnMetadata(final String columnName, final Integer columnIndex, final DataType dataType,
//			final String heading, final Integer columnDisplaySize, final String workbookFormula,
//			final String excelFormat, final String javaFormat, final HorizontalAlignment horizontalAlignment,
//			final String aggregateFunction) {
//		super();
//		this.columnName = columnName;
//		this.columnIndex = columnIndex;
//		this.dataType = dataType;
//		this.heading = heading;
//		this.columnDisplaySize = columnDisplaySize;
//		this.workbookFormula = workbookFormula;
//		this.excelFormat = excelFormat;
//		this.javaFormat = javaFormat;
//		this.horizontalAlignment = horizontalAlignment;
//		this.aggregateFunction = aggregateFunction;
//	}
//
//	public ColumnMetadata(final String columnName, final int columnIndex, final DataType dataType,
//			final Integer precision, final int scale, final int displaySize) {
//		this(columnName, columnIndex, dataType, precision, scale, displaySize, null, null, null);
//	}
//
//	public ColumnMetadata(final String columnName, final Integer index, final String dataType, final String heading,
//			final Integer columnDisplaySize, final String workbookFormula, final String excelFormat,
//			final String javaFormat, final String horizontalAlignment, final String aggregateFunction) {
//		this.columnName = columnName;
//		this.columnIndex = index;
//		this.dataType = DataType.valueOf(dataType);
//		this.heading = heading;
//		this.columnDisplaySize = columnDisplaySize;
//		this.workbookFormula = workbookFormula;
//		this.excelFormat = excelFormat;
//		this.javaFormat = javaFormat;
//		this.horizontalAlignment = HorizontalAlignment.valueOf(horizontalAlignment);
//		this.aggregateFunction = aggregateFunction;
//
//	}
//
//	/*
//	 * If this is called get... then jackson marshaller chokes on a self reference.
//	 * 
//	 * @return
//	 */
//	public ColumnMetadata makeCopy() {
//		final ColumnMetadata cmd = new ColumnMetadata();
//		cmd.setHeading(heading);
//		cmd.setComments(comments);
//		cmd.setExcelFormat(excelFormat);
//		cmd.setAggregateFunction(aggregateFunction);
//		cmd.setAttributeName(attributeName);
//		cmd.setColumnDisplaySize(columnDisplaySize);
//		cmd.setExternalFlag(externalFlag);
//		cmd.setComments(comments);
//		cmd.setColumnName(getColumnName());
//		cmd.setColumnIndex(columnIndex);
//		cmd.setColumnName(columnName);
//		cmd.setDataType(dataType);
//		cmd.setGroupName(groupName);
//		cmd.setPrecision(precision);
//		cmd.setScale(scale);
//		cmd.setHorizontalAlignment(horizontalAlignment);
//		return cmd;
//
//	}
//
//	public ArrayList<String> toStringList() {
//		ArrayList<String> list = new ArrayList<String>();
//		list.add(columnName);
//		list.add(columnIndex == null ? null : columnIndex.toString());
//		list.add(dataType == null ? null : dataType.toString());
//		list.add(heading);
//		list.add(label);
//		list.add(precision == null ? null : precision.toString());
//		list.add(scale == null ? null : scale.toString());
//		list.add(columnDisplaySize == null ? null : columnDisplaySize.toString());
//		list.add(comments);
//		list.add(externalFlag ? "Y" : "N");
//		list.add(attributeName); // 10
//		list.add(workbookFormula); // 11
//		list.add(excelFormat); // 112
//		list.add(javaFormat); // 13
//		list.add(groupName); // 14
//		list.add(horizontalAlignment == null ? null : horizontalAlignment.toString());
//		list.add(aggregateFunction);
//
//		return list;
//
//	}
//
//	public static ColumnMetadata[] jsonToColumnMetadataArray(String json)
//			throws JsonParseException, JsonMappingException, IOException {
//		ObjectMapper mapper = new ObjectMapper();
//		ColumnMetadata[] retval = mapper.readValue(json, ColumnMetadata[].class);
//
//		return retval;
//
//	}
//
//
//	public static ColumnMetadata getInstance(final List<String> args) {
//		Logger logger = LoggerFactory.getLogger(org.javautil.dataset.ColumnMetadata.class);
//		if (logger.isDebugEnabled()) {
//			StringBuilder b = new StringBuilder();
//			for (String arg : args) {
//				b.append("\"");
//				b.append(arg);
//				b.append("\" ");
//
//			}
//			logger.debug(b.toString());
//		}
//		final ColumnMetadata cm = new ColumnMetadata();
//		cm.columnName = args.get(0);
//		cm.columnIndex = getInteger(args.get(1));
//		if (args.get(2) != null) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("dataType: " + args.get(2));
//			}
//			cm.dataType = DataType.valueOf(args.get(2));
//		}
//		cm.heading = args.get(3);
//		cm.label = args.get(4);
//		cm.precision = getInteger(args.get(5));
//		cm.scale = getInteger(args.get(6));
//		cm.columnDisplaySize = getInteger(args.get(7));
//		cm.comments = args.get(8);
//		if (args.get(9) != null & args.get(9).length() > 0) {
//			if ("Y".equals(args.get(9))) {
//				cm.externalFlag = true;
//			} else if ("N".equals(args.get(9))) {
//				cm.externalFlag = false;
//			} else if ("FALSE".equals(args.get(9).toUpperCase())) {
//				cm.externalFlag = false;
//			} else if ("TRUE".equals(args.get(9).toUpperCase())) {
//				cm.externalFlag = true;
//			} else {
//				throw new IllegalArgumentException("external flag(9)  must be 'Y' or 'N' is '" + args.get(9) + "'");
//			}
//		}
//		for (int i = 10; i < args.size(); i++) {
//			switch (i) {
//			case 10:
//				cm.attributeName = args.get(10);
//				break;
//			case 11:
//				cm.workbookFormula = args.get(11);
//				break;
//			case 12:
//				cm.excelFormat = args.get(12);
//				break;
//			case 13:
//				cm.javaFormat = args.get(13);
//				break;
//			case 14:
//				cm.groupName = args.get(14);
//				break;
//			case 15:
//				if (args.get(15) != null) {
//					cm.horizontalAlignment = HorizontalAlignment.valueOf(args.get(15));
//				}
//				break;
//			case 16:
//				cm.aggregateFunction = args.get(16);
//				break;
//			default:
//				throw new IllegalStateException("at index " + i);
//			}
//		}
//		return cm;
//	}
//
//	public static Integer getInteger(String text) {
//		Integer retval = null;
//		if (text != null && text.length() > 0) {
//			retval = new Integer(text);
//		}
//		return retval;
//	}
//
//	public String getHeading() {
//		return heading;
//	}
//
//	public String getHeadingOrColumnName() {
//		String retval = heading;
//		if (retval == null) {
//			logger.debug("Heading is null for " + this.toString() + " using columnName " + columnName);
//			retval = columnName;
//		}
//		return retval;
//	}
//
//	public void setHeading(final String heading) {
//		this.heading = heading;
//	}
//
//	public String getLabel() {
//		return label;
//	}
//
//	public void setLabel(final String label) {
//		this.label = label;
//	}
//
//	@Override
//	public String getComments() {
//		return comments;
//	}
//
//	public void setComments(final String comments) {
//		this.comments = comments;
//	}
//
//	public boolean isExternalFlag() {
//		return externalFlag;
//	}
//
//	public void setExternalFlag(final String externalFlag) {
//		final BooleanPropertyHelper bph = new BooleanPropertyHelper("externalFlag");
//		this.externalFlag = bph.parse(externalFlag);
//	}
//
//	@JsonSetter
//	public void setExternalFlag(final boolean externalFlag) {
//		this.externalFlag = externalFlag;
//	}
//
//	@Override
//	public String getAttributeName() {
//		return attributeName;
//	}
//
//	public void setAttributeName(final String attributeName) {
//		this.attributeName = attributeName;
//	}
//
//	public String getWorkbookFormula() {
//		return workbookFormula;
//	}
//
//	/*
//	 * An excel compliant expression used to populate this virtual column.
//	 * 
//	 * References to other columns in a given dataset row should be made using
//	 * "${COL_NAME}" notation.
//	 * 
//	 * TODO write another example one with
//	 * 
//	 * @param workbookFormula
//	 */
//	public void setWorkbookFormula(final String workbookFormula) {
//		this.workbookFormula = workbookFormula;
//	}
//
//	public String getAggregateFunction() {
//		return aggregateFunction;
//	}
//
//	public void setAggregateFunction(final String aggregateFunction) {
//		this.aggregateFunction = aggregateFunction;
//	}
//
//	/**
//	 * @return the horizontalAlignment
//	 */
//	@JsonSetter
//	public HorizontalAlignment getHorizontalAlignment() {
//		return horizontalAlignment;
//	}
//
//	/**
//	 * @param horizontalAlignment the horizontalAlignment to set
//	 */
//	public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment) {
//		this.horizontalAlignment = horizontalAlignment;
//	}
//
//	/**
//	 * @param horizontalAlignment the horizontalAlignment to set
//	 */
//	@JsonSetter
//	public void setHorizontalAlignment(final String horizontalAlignment) {
//		if (horizontalAlignment != null && horizontalAlignment.length() > 0) {
//			try {
//				this.horizontalAlignment = HorizontalAlignment.valueOf(horizontalAlignment);
//			} catch (final IllegalArgumentException iae) {
//				throw new IllegalPropertyValueException("Invalid value '" + horizontalAlignment + "'");
//			}
//		}
//	}
//
//	/**
//	 * @return the excelFormat
//	 */
//	public String getExcelFormat() {
//		return excelFormat;
//	}
//
//	/**
//	 * @param excelFormat the excelFormat to set
//	 */
//	public void setExcelFormat(final String excelFormat) {
//		this.excelFormat = excelFormat;
//	}
//
//	/**
//	 * @return the javaFormat
//	 */
//	public String getJavaFormat() {
//		return javaFormat;
//	}
//
//	/**
//	 * @param javaFormat the javaFormat to set
//	 */
//	public void setJavaFormat(final String javaFormat) {
//		this.javaFormat = javaFormat;
//	}
//
//	public Integer getColumnDisplaySize() {
//		return columnDisplaySize;
//	}
//
//	public Integer getColumnIndex() {
//		return columnIndex;
//	}
//
//	@Override
//	public String getColumnName() {
//		return columnName;
//	}
//
//	public DataType getDataType() {
//		return dataType;
//	}
//
//	public String getGroupName() {
//		return groupName;
//	}
//
//	public Integer getPrecision() {
//		return precision;
//	}
//
//	@Override
//	public Integer getScale() {
//		return scale;
//	}
//
//	@JsonSetter
//	public void setColumnDisplaySize(final Integer columnDisplaySize) {
//		this.columnDisplaySize = columnDisplaySize;
//	}
//
//	public void setColumnDisplaySize(final String columnDisplaySize) {
//		final IntegerPropertyHelper iph = new IntegerPropertyHelper("columnDisplaySize", 1, 4096);
//		this.columnDisplaySize = iph.parse(columnDisplaySize);
//	}
//
//	public void setColumnIndex(final String columnIndex) {
//		final IntegerPropertyHelper iph = new IntegerPropertyHelper("columnIndex", 0, null);
//		this.columnIndex = iph.parse(columnIndex);
//	}
//
//	@JsonSetter
//	public void setColumnIndex(final Integer columnIndex) {
//		this.columnIndex = columnIndex;
//	}
//
//	public void setColumnName(final String columnName) {
//		this.columnName = columnName;
//	}
//
//	@JsonSetter
//	public void setDataType(final String columnType) {
//		this.dataType = DataType.valueOf(columnType);
//	}
//
//	public void setDataType(final DataType columnType) {
//		this.dataType = columnType;
//	}
//
//	public void setGroupName(final String groupName) {
//		this.groupName = groupName;
//	}
//
//	public void setPrecision(final String precision) {
//		final IntegerPropertyHelper iph = new IntegerPropertyHelper("precision", null, null);
//		this.precision = iph.parse(precision);
//	}
//
//	@JsonSetter
//	public void setPrecision(final Integer precision) {
//		this.precision = precision;
//	}
//
//	public void setScale(final String scale) {
//		final IntegerPropertyHelper iph = new IntegerPropertyHelper("scale", null, null);
//		this.scale = iph.parse(scale);
//	}
//
//	@JsonSetter
//	public void setScale(final Integer scale) {
//		this.scale = scale;
//	}
//
//	public static String toString(final Collection<ColumnMetadata> columns) {
//		final StringBuilder sb = new StringBuilder();
//		for (final ColumnMetadata column : columns) {
//			sb.append(column);
//			sb.append(newline);
//		}
//		return sb.toString();
//	}
//
//	public boolean isInjectedGroupField() {
//		return injectedGroupField;
//	}
//
//	public void setInjectedGroupField(final boolean injectedGroupField) {
//		this.injectedGroupField = injectedGroupField;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((aggregateFunction == null) ? 0 : aggregateFunction.hashCode());
//		result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
//		result = prime * result + ((columnDisplaySize == null) ? 0 : columnDisplaySize.hashCode());
//		result = prime * result + ((columnIndex == null) ? 0 : columnIndex.hashCode());
//		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
//		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
//		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
//		result = prime * result + ((excelFormat == null) ? 0 : excelFormat.hashCode());
//		result = prime * result + (externalFlag ? 1231 : 1237);
//		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
//		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
//		result = prime * result + ((horizontalAlignment == null) ? 0 : horizontalAlignment.hashCode());
//		result = prime * result + (injectedGroupField ? 1231 : 1237);
//		result = prime * result + ((javaFormat == null) ? 0 : javaFormat.hashCode());
//		result = prime * result + ((label == null) ? 0 : label.hashCode());
//		result = prime * result + ((precision == null) ? 0 : precision.hashCode());
//		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
//		result = prime * result + ((workbookFormula == null) ? 0 : workbookFormula.hashCode());
//		return result;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ColumnMetadata other = (ColumnMetadata) obj;
//		if (aggregateFunction == null) {
//			if (other.aggregateFunction != null)
//				return false;
//		} else if (!aggregateFunction.equals(other.aggregateFunction))
//			return false;
//		if (attributeName == null) {
//			if (other.attributeName != null)
//				return false;
//		} else if (!attributeName.equals(other.attributeName))
//			return false;
//		if (columnDisplaySize == null) {
//			if (other.columnDisplaySize != null)
//				return false;
//		} else if (!columnDisplaySize.equals(other.columnDisplaySize))
//			return false;
//		if (columnIndex == null) {
//			if (other.columnIndex != null)
//				return false;
//		} else if (!columnIndex.equals(other.columnIndex))
//			return false;
//		if (columnName == null) {
//			if (other.columnName != null)
//				return false;
//		} else if (!columnName.equals(other.columnName))
//			return false;
//		if (comments == null) {
//			if (other.comments != null)
//				return false;
//		} else if (!comments.equals(other.comments))
//			return false;
//		if (dataType != other.dataType)
//			return false;
//		if (excelFormat == null) {
//			if (other.excelFormat != null)
//				return false;
//		} else if (!excelFormat.equals(other.excelFormat))
//			return false;
//		if (externalFlag != other.externalFlag)
//			return false;
//		if (groupName == null) {
//			if (other.groupName != null)
//				return false;
//		} else if (!groupName.equals(other.groupName))
//			return false;
//		if (heading == null) {
//			if (other.heading != null)
//				return false;
//		} else if (!heading.equals(other.heading))
//			return false;
//		if (horizontalAlignment != other.horizontalAlignment)
//			return false;
//		if (injectedGroupField != other.injectedGroupField)
//			return false;
//		if (javaFormat == null) {
//			if (other.javaFormat != null)
//				return false;
//		} else if (!javaFormat.equals(other.javaFormat))
//			return false;
//		if (label == null) {
//			if (other.label != null)
//				return false;
//		} else if (!label.equals(other.label))
//			return false;
//		if (precision == null) {
//			if (other.precision != null)
//				return false;
//		} else if (!precision.equals(other.precision))
//			return false;
//		if (scale == null) {
//			if (other.scale != null)
//				return false;
//		} else if (!scale.equals(other.scale))
//			return false;
//		if (workbookFormula == null) {
//			if (other.workbookFormula != null)
//				return false;
//		} else if (!workbookFormula.equals(other.workbookFormula))
//			return false;
//		return true;
//	}
//
//	@Override
//	public Integer getColumnSize() {
//		return columnSize;
//	}
//
//	@Override
//	public String getColumnTypeName() {
//		return columnTypeName;
//	}
//
//	public void setColumnType(Integer columnType) {
//		this.columnType = columnType;
//	}
//
//	@Override
//	public Integer getColumnType() {
//		return columnType;
//	}
//
//	@Override
//	public Boolean isDefinitelyNullable() {
//		return definitelyNullable;
//	}
//
//	@Override
//	public Boolean isNotNullable() {
//		return notNullable;
//	}
//
//	@Override
//	public Boolean isUnknownNullable() {
//		return unKnownNullable;
//	}
//
//	@Override
//	public Boolean isNullable() {
//		return nullable;
//	}
//
//	public void setColumnTypeName(String columnTypeName) {
//		this.columnTypeName = columnTypeName;
//	}
//
//	@Override
//	public String toString() {
//		final AsString as = new AsString();
//		return as.toString(this);
//
//	}
//
//	public String toStringBrief() {
//		return "ColumnMetadata [columnName=" + columnName + ", columnIndex=" + columnIndex + ", dataType=" + dataType
//				+ ", heading=" + heading + ", label=" + label + ", groupName=" + groupName + "]";
//	}
//
//}
