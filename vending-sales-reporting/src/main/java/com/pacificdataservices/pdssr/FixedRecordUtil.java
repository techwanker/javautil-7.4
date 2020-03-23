package com.pacificdataservices.pdssr;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javautil.core.text.StringUtils;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FixedRecordUtil {
	public static final String TEXT = "TEXT";
	public static final String LITERAL = "LITERAL";
	public static final String NUMBER = "NUMERIC";
	public static final String DATE = "DATE";
	public static final String DIGITS = "DIGITS";
	public static final String NUMERIC = "NUMERIC";
	public static final String INTEGER = "INTEGER";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	//private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	//private Formatter formatter = new Formatter();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	Map<String, List<Map<String, Object>>> definitions;
	// https://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate

	Date toDate(String dateString) {
		Date d;
		try {
			d = sdf.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return d;
	}

	String toDateString(Date date) {
		String retval = sdf.format(date);
		return retval;
	}

	String to_formatted_string(Map<String, Object> fld_def, Object p_obj, boolean trace) {
		String retval = null;
		String java_format = (String) fld_def.get("java_format");
		String field_name = (String) fld_def.get("field_name");
		Integer field_length;
		try {
			field_length = (Integer) fld_def.get("fixed_length");
		} catch (Exception e) {
			throw new RuntimeException("While processing " + fld_def + " " + e.getMessage());
		}

		String fieldType = (String) fld_def.get("field_type");

		if (LITERAL.equals(fieldType)) {
			// retval = (String) fld_def.get("fixed_literal");
			retval = (String) p_obj;
			logger.debug("retval '" + retval + "' " + retval.length());
		} else if (DATE.equals(fieldType)) {
			retval = toDateString((Date) p_obj);
			logger.debug("retval " + retval + " for " + fieldType + " using simpleDateFormat");
		} else if (TEXT.equals(fieldType)) {
			if (field_length == null) {
				throw new IllegalArgumentException("fixed_length not specified for " + field_name);
			}
			String o = (String) p_obj;
			if (o == null) {
				o = "";
			}
			retval = StringUtils.padRight(o, field_length);
		} else if (DIGITS.equals(fieldType) || NUMERIC.equals(fieldType)) {
			retval = StringUtils.padLeft(p_obj.toString(), field_length, '0');
			logger.debug("numeric field_name " + field_name + " " + retval + "'");
		} else if (INTEGER.equals(fieldType)) {
			if (java_format == null) {
				throw new IllegalArgumentException("fld_def " + fld_def + " does not have a java_format");
			}
			retval = String.format(java_format, p_obj);
			logger.debug("INTEGER field_name " + field_name + " '" + retval + "'");
		} else {
			throw new IllegalStateException("Not using formats any more");
		}

		return retval;
	}

	Object toObject(Map<String, Object> meta, String text) throws ParseException { // TODO make these module level and
																					// call from class methods

		String field_type = (String) meta.get("field_type");

		Object retval = null;
		if (field_type.equals(NUMERIC)) { // TODO regular expression validation
			retval = new BigDecimal(text);
		} else if (field_type.equals(TEXT)) {
			retval = text.trim();
		} else if (field_type.equals(DATE)) {
			Date dt = toDate(text);
			retval = dt;
		} else if (field_type.equals(LITERAL)) {
			retval = text;
		} else if (field_type.equals(DIGITS)) {
			retval = text;
		} else if (field_type.equals(INTEGER)) {
			retval = new Integer(text);
		} else {
			throw new RuntimeException("unhandled field type " + field_type);
		}

		return retval;
	}

	/**
	 * Returns an object representation of the specified field in the fixed length
	 * record as specified in config/cd # TODO could make this generic by passing
	 * the fixed_offset and length, field_type and date parsing format param
	 * field_metadata param record return
	 * 
	 * @param field_metadata
	 * @param record
	 * @return
	 * @throws ParseException
	 */
	Object getFieldObject(NameValue field_metadata, String record) throws ParseException {
		int startIndex = (int) field_metadata.get("fixed_offset");
		int length = (int) field_metadata.get("fixed_length");
		int endIndex = startIndex + length;
		String text = record.substring(startIndex, endIndex);
		return toObject(field_metadata, text);
	}

	/**
	 * Creates a record of fixed length with fixed length fields field_data_map must
	 * have the attributes length and offset
	 * 
	 * param record_definition a list of FieldMetadata at least supports
	 * __get_item__ param field_data_map return
	 * 
	 * @param record_definition
	 * @param field_data
	 * @param trace             boolean if true trace
	 * @return the formatted line
	 */
	public String formatLine(ListOfNameValue record_definition, NameValue field_data, boolean trace) {

		//StringBuilder sb = new StringBuilder(170);
		char[] buffer = new char[170];  // TODO s/b dynamic based on record definition

		for (NameValue field_def : record_definition) {
			// logger.info("field_def " + field_def);
			String fieldName = (String) field_def.get("field_name");
			// logger.info("about to get " + fieldName);
			Object datum = field_data.getInsensitive(fieldName);
			String formatted_field = to_formatted_string(field_def, datum, trace);
			if (formatted_field == null) {
				throw new IllegalStateException("formatted field is null for " + fieldName);
			}

			int start_pos = (int) field_def.get("fixed_offset");

//			logger.info("fieldName " + fieldName + " start_pos " + start_pos + 
//					"\ndatum:      '" + datum + "'" +
//					"\nformatted:  '" + formatted_field + "'" + 
//					"\nlength " + formatted_field.length()
//					+ " datum string length " + datum.toString().length());

			System.arraycopy(formatted_field.toCharArray(), 0, buffer, start_pos, formatted_field.length());

		}
		String retval = new String(buffer);
		// logger.info("record:\n" + retval);
		return retval;
	}

	public Map<String, Object> getBinds(ListOfNameValue fieldDefinitions, String inrec) throws ParseException {
		if (fieldDefinitions == null) {
			throw new IllegalArgumentException("fieldDefinitions is null");
		}
		if (fieldDefinitions.size() == 0) {
			throw new IllegalArgumentException("No fields in fieldDefinitions");
		}
		logger.debug("getBinds: record {}\nfield_defs{}", inrec, fieldDefinitions);
		HashMap<String, Object> binds = new HashMap<String, Object>();
		for (NameValue fieldDefinition : fieldDefinitions) {
			String name = (String) fieldDefinition.get("field_name");
			Object value = getFieldObject(fieldDefinition, inrec);
			binds.put(name, value);
			logger.debug("getBinds fieldDefinition {}\nname: {}\nvalue:{}", fieldDefinition, name, value);
		}
		return binds;
	}

	public List<Object> getObjectList(ListOfNameValue field_defs, String inrec) throws ParseException {
		List<Object> retval = new ArrayList<Object>();
		for (NameValue field_def : field_defs) {
			Object value = getFieldObject(field_def, inrec);
			retval.add(value);
		}
		return retval;
	}
}