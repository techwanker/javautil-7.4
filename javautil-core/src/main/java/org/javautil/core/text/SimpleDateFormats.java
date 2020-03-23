package org.javautil.core.text;

/**
 * http://support.sas.com/documentation/cdl/en/lrdict/64316/HTML/default/viewer.htm#a003169814.htm
 * 
 * @author jjs
 *
 */
public class SimpleDateFormats {
	/*
	 * y = year (yy or yyyy) M = month (MM) d = day in month (dd) h = hour (0-12)
	 * (hh) H = hour (0-23) (HH) m = minute in hour (mm) s = seconds (ss) S =
	 * milliseconds (SSS) z = time zone text (e.g. Pacific Standard Time...) Z =
	 * time zone, time offset (e.g. -0800)
	 */

	/*
	 * DateTimeFormatter parser2 = ISODateTimeFormat.dateTimeNoMillis(); String
	 * jtdate = "2010-01-01T12:00:00+01:00";
	 * logger.debug(parser2.parseDateTime(jtdate));
	 * 
	 * Or more simply, use the default parser via the constructor:
	 * 
	 * DateTime dt = new DateTime( "2010-01-01T12:00:00+01:00" ) ;
	 */

	public static final String dd_MM_YYYY                       = "dd-MM-YYYY";               // (31_12_2009)

	public static final String yyyy_MM_dd_HH_mm_ss              = "yyyy-MM-dd HH:mm:ss.SSS";  // (2009_12_31 23_59_59)

	public static final String HH_mm_ss_SSS                     = "HH:mm:ss.SSS";             // (23_59.59.999)

	public static final String yyyy_MM_dd_HH_mm_ss_SSS          = "yyyy-MM-dd HH:mm:ss.SSS";  // (2009_12_31 23_59_59.999)

	/**
	 * Example: 2009_12_31 23_59_59.999 +0100
	 */
	public static final String yyyy_MM_dd_HH_mm_ss_SSS_Z        = "yyyy-MM-dd HH:mm:ss.SSS Z";

	public static final String yyyy_MM_DDTHH_mm_ss_SSS          = "yyyy-MM-dd'T'HH-mm-ss.SSS";

	public static final String ISO8601_date                     = "yyyyMMdd";

	public static final String ISO8601_date_pretty              = "yyyy-MM-dd";

	public static final String ISO8601_datetime                 = "yyyyMMdd'T'HHmmss";

	public static final String ISO8601_datetime_pretty          = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String ISO8601_datetime_timezone        = "yyyyMMdd'T'HHmmssXXX";

	public static final String ISO8601_datetime_timezone_pretty = "yyyy-MM-dd'T'HH:mm:ssXXX";

}
