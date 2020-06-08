package org.javautil.conditionidentification;

import java.sql.Types;
import java.util.Map;

import org.javautil.core.sql.Binds;

public class ConditionRule {
	private String  ruleName;

	private String  tableName;

	private String  msg;

	private String  sqlText;

	private String  narrative;

	private Integer severity;

	private String  formatString;

	private String  javaFormat;

	/**
	 * 
	 * 
	 * <pre>
	  -   
	    rule_name:  NO_CUSTOMER_TOTAL
		table_name: ETL_FILE
	    msg:      No customer total record
	    sql_text: &amp;
	        select etl_file_id
	        from etl_file
	        where etl_file_id = %(ETL_FILE_ID)s
	        and not exists (
	           select 'x'
	           from etl_customer_tot
	           where etl_file_id = %(ETL_FILE_ID)s
	        )
	    narrative: &amp; 
	        There is no customer total record
	    severity: 3
	    format_str: "No customer total record in %s"
	    java_format: "No customer total record for etl_file_id %s"
	 * </pre>
	 * 
	 * @param conditionMap TODO this should be name value
	 */

	public ConditionRule(Map<String, Object> conditionMap) {
		setRuleName((String) required("rule_name", conditionMap, ruleName));
		tableName = (String) required("table_name", conditionMap, ruleName);
		msg = (String) conditionMap.get("msg");
		sqlText = (String) required("sql_text", conditionMap, ruleName);
		narrative = (String) conditionMap.get("narrative");
		severity = (Integer) required("severity", conditionMap, ruleName);
		formatString = (String) conditionMap.get("format_string");
		javaFormat = (String) required("java_format", conditionMap, ruleName);
	}

	public Object required(String key, Map<String, Object> condition, String ruleName) {

		Object retval = condition.get(key);
		if (retval == null) {
			throw new IllegalArgumentException("no value found for key '" + key + "' in " + ruleName);
		}
		return retval;

	}

	public Binds getBinds() {
		Binds binds = new Binds();
		binds.put("rule_name", ruleName, Types.VARCHAR);
		binds.put("table_name", tableName,Types.VARCHAR);
		binds.put("msg", msg,Types.VARCHAR);
		binds.put("sql_text", sqlText,Types.VARCHAR);
		binds.put("narrative", narrative,Types.VARCHAR);
		binds.put("severity", severity,Types.VARCHAR);
		binds.put("format_str", formatString,Types.VARCHAR);
		return binds;

	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		if (ruleName == null) {
			throw new IllegalArgumentException("ruleName is required");
		}
		if (ruleName.length() > 30) {
			throw new IllegalArgumentException(
			    "ruleName '" + ruleName + "' length " + ruleName.length() + " greater than 30");
		}
		this.ruleName = ruleName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	public String getJavaFormat() {
		return javaFormat;
	}

	public void setJavaFormat(String javaFormat) {
		this.javaFormat = javaFormat;
	}

}
