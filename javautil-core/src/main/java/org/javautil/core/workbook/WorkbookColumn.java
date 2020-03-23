
package org.javautil.core.workbook;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkbookColumn {

	@SerializedName("name")
	@Expose
	private String  name;
	@SerializedName("heading")
	@Expose
	private String  heading;
	@SerializedName("label")
	@Expose
	private String  label;
	@SerializedName("precision")
	@Expose
	private Integer precision;
	@SerializedName("scale")
	@Expose
	private Integer scale;
	@SerializedName("columnDisplaySize")
	@Expose
	private Integer columnDisplaySize;
	@SerializedName("comments")
	@Expose
	private String  comments;
	@SerializedName("workbookFormula")
	@Expose
	private String  workbookFormula;
	@SerializedName("excelFormat")
	@Expose
	private String  excelFormat;
	@SerializedName("javaFormat")
	@Expose
	private String  javaFormat;
	@SerializedName("groupName")
	@Expose
	private String  groupName;
	@SerializedName("horizontalAlignment")
	@Expose
	private String  horizontalAlignment;
	@SerializedName("aggregateFunction")
	@Expose
	private String  aggregateFunction;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public WorkbookColumn() {
	}

	/*
	 * @param name
	 * 
	 * @param heading
	 * 
	 * @param label
	 * 
	 * @param precision
	 * 
	 * @param scale
	 * 
	 * @param columnDisplaySize
	 * 
	 * @param comments
	 * 
	 * @param workbookFormula
	 * 
	 * @param excelFormat
	 * 
	 * @param javaFormat
	 * 
	 * @param groupName
	 * 
	 * @param horizontalAlignment
	 * 
	 * @param aggregateFunction
	 */
	public WorkbookColumn(String name, String heading, String label, Integer precision, Integer scale, Integer columnDisplaySize,
	    String comments, String workbookFormula, String excelFormat, String javaFormat, String groupName,
	    String horizontalAlignment, String aggregateFunction) {
		super();
		this.name = name;
		this.heading = heading;
		this.label = label;
		this.precision = precision;
		this.scale = scale;
		this.columnDisplaySize = columnDisplaySize;
		this.comments = comments;
		this.workbookFormula = workbookFormula;
		this.excelFormat = excelFormat;
		this.javaFormat = javaFormat;
		this.groupName = groupName;
		this.horizontalAlignment = horizontalAlignment;
		this.aggregateFunction = aggregateFunction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WorkbookColumn withName(String name) {
		this.name = name;
		return this;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public WorkbookColumn withHeading(String heading) {
		this.heading = heading;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public WorkbookColumn withLabel(String label) {
		this.label = label;
		return this;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public WorkbookColumn withPrecision(Integer precision) {
		this.precision = precision;
		return this;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public WorkbookColumn withScale(Integer scale) {
		this.scale = scale;
		return this;
	}

	public Integer getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public WorkbookColumn withColumnDisplaySize(Integer columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public WorkbookColumn withComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getWorkbookFormula() {
		return workbookFormula;
	}

	public void setWorkbookFormula(String workbookFormula) {
		this.workbookFormula = workbookFormula;
	}

	public WorkbookColumn withWorkbookFormula(String workbookFormula) {
		this.workbookFormula = workbookFormula;
		return this;
	}

	public String getExcelFormat() {
		return excelFormat;
	}

	public void setExcelFormat(String excelFormat) {
		this.excelFormat = excelFormat;
	}

	public WorkbookColumn withExcelFormat(String excelFormat) {
		this.excelFormat = excelFormat;
		return this;
	}

	public String getJavaFormat() {
		return javaFormat;
	}

	public void setJavaFormat(String javaFormat) {
		this.javaFormat = javaFormat;
	}

	public WorkbookColumn withJavaFormat(String javaFormat) {
		this.javaFormat = javaFormat;
		return this;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public WorkbookColumn withGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(String horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	public WorkbookColumn withHorizontalAlignment(String horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public String getAggregateFunction() {
		return aggregateFunction;
	}

	public void setAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	public WorkbookColumn withAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("heading", heading).append("label", label)
		    .append("precision", precision).append("scale", scale).append("columnDisplaySize", columnDisplaySize)
		    .append("comments", comments).append("workbookFormula", workbookFormula).append("excelFormat", excelFormat)
		    .append("javaFormat", javaFormat).append("groupName", groupName)
		    .append("horizontalAlignment", horizontalAlignment).append("aggregateFunction", aggregateFunction).toString();
	}

}
