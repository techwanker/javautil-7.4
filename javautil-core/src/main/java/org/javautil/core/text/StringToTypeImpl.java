package org.javautil.core.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.javautil.dataset.DataType;

public class StringToTypeImpl implements StringToType {
	private String           dateFormatterString = SimpleDateFormats.ISO8601_datetime_pretty;
	private SimpleDateFormat dateFormat          = new SimpleDateFormat(dateFormatterString);

	// TODO exhaust the date types

	public StringToTypeImpl(String simpleDateFormatString) {
		this.dateFormatterString = simpleDateFormatString;
		this.dateFormat = new SimpleDateFormat(dateFormatterString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.core.text.StringToType#coerceString(java.lang.String,
	 * org.javautil.dataset.DataType)
	 */
	@Override
	public Object coerceString(final String value, DataType type) {
		Object returnValue = null;
		if (value != null) {
			switch (type) {
			case STRING:
				returnValue = value;
				break;
			case LONG:
				returnValue = Long.parseLong(value);
				break;
			case SHORT:
				returnValue = Short.parseShort(value);
				break;
			case DOUBLE:
				returnValue = Double.parseDouble(value);
				break;
			case INTEGER:
				returnValue = Integer.parseInt(value);
				break;
			case BIG_INTEGER:
				returnValue = new BigInteger(value);
				break;
			case BIG_DECIMAL:
				returnValue = new BigDecimal(value);
				break;
			case SQLDATE:
				try {
					returnValue = dateFormat.parse(value);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				break;
			case TIMESTAMP:
				try {
					returnValue = dateFormat.parse(value);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				break;
			case CLOB:
			case VARBINARY:
			default:
				throw new UnsupportedOperationException("unsupported operation for " + this + "'" + value + "'");

			}
		}
		return returnValue;
	}

	public StringToType withDateFormatString(String format) {
		this.dateFormat = new SimpleDateFormat(dateFormatterString);
		try {
			dateFormat.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			throw new IllegalArgumentException("invalid date format " + e.getMessage());
		}
		return this;
	}
}
