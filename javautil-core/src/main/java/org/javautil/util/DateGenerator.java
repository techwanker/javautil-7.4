package org.javautil.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.javautil.core.text.SimpleDateFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to solve the recurring business problem of associated
 * a date with a date range.
 * 
 * This class will generate <i>buckets</i> of dates.
 * 
 */
// * todo cleanup function calls so it is clear which ones are to be used with
// *       what calls, or better yet split into a new class <code> <?xml
// *       version="1.0" encoding="UTF-8" standalone="yes" ?>
// *       <xsl:stylesheet version="1.0" xmlns:xsl=
// *       "http://www.w3.org/1999/XSL/Transform" xmlns:xalan=
// *       "http://xml.apache.org/xalan" xmlns:java="xalan://java" xmlns:javautil=
// *       "xalan://com.javautil" > <xsl:template match="/">
// *       <xsl:param name="cOrdDt"/> <job> <xsl:variable name="bucketer" select=
// *       "javautil:date.DateGenerator.new('month','yyyy-MM-dd','MM-yyyy')"/>
// *       <xsl:for-each select="/data/oeOrdHdr/row">
// *       <xsl:variable name="dt" select="@ordDt"/> bucket:
// *       <xsl:value-of select="javautil:addDate($bucketer,$dt)"/>
// *       </xsl:for-each> <xsl:variable name="delimiter">~</xsl:variable>
// *       <xsl:variable name="buckets" select=
// *       "javautil:getBucketNodes($bucketer)"/> <xsl:for-each select="$buckets">
// *       <bucket><xsl:value-of select="."/></bucket> </xsl:for-each> </job>
// *       </xsl:template> </xsl:stylesheet>
public class DateGenerator {
	// private Logger logger =
	// LoggerFactory.getLogger(DateGenerator.class.getName());

	private String                        inputFormat;

	private GregorianCalendar             cal                      = new GregorianCalendar();
	// private String outputFormat;

	private SimpleDateFormat              inputFormatter;

	private SimpleDateFormat              outputFormatter;

	private final GregorianCalendar       calendar                 = new GregorianCalendar();

	private java.util.Date                minimumDate              = null;

	private java.util.Date                maximumDate              = null;

	private java.util.Date                lastBucketEndsBeforeDate = null;

	private TreeMap<Date, Date>           dateMap                  = new TreeMap<Date, Date>();

	private final HashSet<java.util.Date> usedBuckets              = new HashSet<java.util.Date>();

	private Date                          minBucketDate;

	private Date                          maxBucketDate;

	private Logger                        logger                   = LoggerFactory.getLogger(getClass());

	private int                           incrementMultiplier      = 1;

	/*
	 * domain is Calendar.DATE, Calendar.MONTH, CALENDAR.YEAR
	 * 
	 * unfortunately, these are not declared static in java.util.Calendar, hence a
	 * bunch of goofy code.
	 */
	private Integer                       calendarIncrement        = null;

	boolean                               valid                    = true;

	/**
	 * Class to generate dates with "fixed" intervals support for days and months at
	 * this time.
	 */
	public DateGenerator() {

	}

	/**
	 * 
	 * @param bucketAlignment "month"
	 * @param inputFormat     - any format supported by SimpleDateFormat
	 * @param outputFormat    - any format supported by SimpleDateFormat
	 */
	public DateGenerator(String bucketAlignment, String inputFormat, String outputFormat) {
		if (bucketAlignment == null) {
			throw new IllegalArgumentException("bucketAlignment is null");
		}

		try {
			this.inputFormat = inputFormat;
			inputFormatter = new SimpleDateFormat(inputFormat);
		} catch (IllegalArgumentException i) {
			throw new IllegalArgumentException(
			    "unable to create formatter using inputFormat '" + inputFormat + "'" + i.getMessage());
		}

		try {
			outputFormatter = new SimpleDateFormat(outputFormat);
		} catch (IllegalArgumentException i) {
			throw new IllegalArgumentException(
			    "unable to create formatter using outputFormat '" + outputFormat + "'" + i.getMessage());
		}
		String align = bucketAlignment.toUpperCase();
		while (true) {
			if (align.equals("MONTH")) {
				setCalendarIncrement(Calendar.MONTH);
				break;
			}
			// add this in
			// if (align.equal("WEEK")) {
			// setCalendarIncrement(Calendar.)
			// }

			throw new IllegalArgumentException("bucketAlignment must be 'MONTH'");
		}
	}

	/**
	 * @param date - a date in a format corresponding to inputFormat on constructor.
	 * @return the date bucket formatted to the outputFormat specified on
	 *         constructor.
	 */
	public String addDate(String date) {

		java.util.Date datum = null;
		java.util.Date dateBucket = null;
		String returnValue = "";
		if (date == null) {
			throw new IllegalArgumentException("date is null");
		}

		try {
			datum = inputFormatter.parse(date);
		} catch (IllegalArgumentException i) {
			throw new IllegalArgumentException(
			    "unable to parse '" + date + "'" + " using format " + inputFormat + i.getMessage());
		} catch (ParseException e) {
			throw new IllegalArgumentException(
			    "unable to parse '" + date + "'" + " using format " + inputFormat + e.getMessage());
		}

		switch (calendarIncrement.intValue()) {
		case Calendar.MONTH:
			dateBucket = toFirstDayInMonth(datum);
			break;
		default:
			throw new IllegalStateException("can only bucket by month at this time");
		}
		usedBuckets.add(dateBucket);
		returnValue = outputFormatter.format(dateBucket);

		return returnValue;
	}

	public void generateBuckets(int numberOfBuckets)
	    throws java.lang.IllegalStateException, java.lang.IllegalArgumentException {
		while (dateMap.size() < numberOfBuckets) {
			addDate(generateNextDate(maximumDate));
		}
		lastBucketEndsBeforeDate = computeNextDate(maximumDate);
	}

	public void generateDates(Date firstDate, Date lastDate) {
		if (firstDate == null) {
			throw new IllegalArgumentException("firstDate is null");
		}
		if (lastDate == null) {
			throw new IllegalArgumentException("lastDate is null");
		}
		Date workDate = firstDate;
		while (!workDate.after(lastDate)) {
			addDate(workDate);
			workDate = generateNextDate(workDate);
		}
	}

	private void addDate(Date date) {
		dateMap.put(date, date);
	}

	public Date getBucketDate(Date date) {
		Date retval = dateMap.floorKey(date);
		if (retval == null) {
			StringBuilder sb = new StringBuilder(
			    String.format("bucketNotFound for date: %s minimumDate %s", date.toString(), minimumDate.toString()));
			sb.append("\n");
			for (Date d : dateMap.keySet()) {
				sb.append(d);
				sb.append("\n");
			}
			String message = sb.toString();
			logger.error(message);
			throw new IllegalStateException(message);

		}
		return retval;
	}

	/**
	 * @param date date to be checked
	 * @return int
	 *         <ul>
	 *         <li>-1 Date is before range</li>
	 *         <li>0 Date is on or after first date and on or before last date
	 *         <li>1 Date is after last date
	 *         </ul>
	 * @exception java.lang.IllegalArgumentException if null
	 * @exception IllegalStateException              If range of dates has not been
	 *                                               populated
	 */
	public int getBucketDomain(java.util.Date date) throws java.lang.IllegalArgumentException {
		int rc = 0;
		if (date == null) {
			throw new java.lang.IllegalArgumentException("date is null");
		}
		if (minimumDate == null) {
			throw new java.lang.IllegalStateException("minimum date has not been established");
		}
		if (lastBucketEndsBeforeDate == null) {
			throw new java.lang.IllegalStateException("lastBucketEndsBeforeDate has not been established");
		}
		if (date.before(minimumDate)) {
			rc = -1;
		} else if (!date.before(lastBucketEndsBeforeDate)) {
			rc = 1;
		}
		return rc;
	}

	/**
	 * @return ArrayList of java.Util.Date ordered increasing by Date
	 */
	public Iterator<Date> getDateIterator() {
		return dateMap.keySet().iterator();
	}

	public ArrayList<Day> getDays() {
		ArrayList<Day> returnValue = new ArrayList<Day>();
		for (Date date : dateMap.keySet()) {
			returnValue.add(new Day(date));
		}
		return returnValue;
	}

	public java.util.Date getFirstBucket() {
		return dateMap.firstKey();
	}

	/**
	 * get the minimum date bucket greater than the specified date
	 * 
	 * @param date The date to be bucketed
	 * @return THe next date in the series
	 */
	public java.util.Date getNextBucket(java.util.Date date) {
		Date rc = dateMap.higherKey(date);
		return rc;
	}

	/**
	 * Set the calendar increment.
	 * 
	 * Valid values are GregorianCalendar.DATE, GregorianCalendar.MONTH, *
	 * GregorianCalendar.YEAR.
	 * 
	 * @param increment Calendar.Date MONTH YEAR
	 * @exception java.lang.IllegalArgumentException if increment is not in
	 *                                               Calendar.DATE, Calendar.MONTH
	 *                                               or Calendar.YEAR
	 */
	public void setCalendarIncrement(int increment) throws java.lang.IllegalArgumentException {
		boolean valid = false;
		while (true) {
			if (increment == Calendar.DATE) {
				valid = true;
				break;
			}
			if (increment == Calendar.MONTH) {
				valid = true;
				break;
			}
			if (increment == Calendar.YEAR) {
				valid = true;
				break;
			}
			break;
		}
		if (valid) {
			this.calendarIncrement = new Integer(increment);
		} else {
			throw new java.lang.IllegalArgumentException("illegal increment: " + increment);
		}
	}

	/**
	 * provided a date, generate the next date using the incrementor rule
	 * 
	 * @param date the first date in the date series
	 */
	public void setFirstDate(java.util.Date date) {

		minimumDate = date;
		maximumDate = date;
		addDate(date);
	}

	public void setIncrementInDays(int numberOfDays) {
		setCalendarIncrement(Calendar.DATE);
		incrementMultiplier = numberOfDays;
	}

	/**
	 * Set the increment in the number of months between dates.
	 * 
	 * @param numberOfMonths increment
	 */
	public void setIncrementInMonths(int numberOfMonths) {
		setCalendarIncrement(Calendar.MONTH);
		incrementMultiplier = numberOfMonths;
	}

	public void setMaximumDate(Date day) {
		maximumDate = day;
	}

	public int size() {
		return dateMap.size();
	}

	/**
	 * Converts the date to midnight on the first day of the month.
	 * 
	 * @param date the date
	 * @return The first day in the month of the specified date
	 */
	public java.util.Date toFirstDayInMonth(java.util.Date date) {

		java.util.Date returnValue = null;
		synchronized (calendar) {

			calendar.setTime(date);
			calendar.set(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			returnValue = calendar.getTime();
		}
		return returnValue;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (Date dt : dateMap.keySet()) {
			buff.append(dt);
			buff.append(" ");
		}
		return new String(buff);
	}

	private java.util.Date computeNextDate(java.util.Date date) {
		java.util.Date rc = null;
		Date midnight = DateUtils.getDateMidnight(date);
		cal.setTime(midnight);
		if (calendarIncrement == null) {
			throw new java.lang.IllegalStateException("calendarIncrement has not been set");
		}
		int increment = calendarIncrement.intValue();
		cal.add(increment, incrementMultiplier);
		rc = cal.getTime();
		return rc;
	}

	private java.util.Date generateNextDate(java.util.Date date) {
		maximumDate = computeNextDate(date);
		return maximumDate;
	}

	/**
	 * @return the lastBucketEndsBeforeDate
	 */
	public java.util.Date getLastBucketEndsBeforeDate() {
		return lastBucketEndsBeforeDate;
	}

	public List<String> getBucketNames() {
		SimpleDateFormat sdf = outputFormatter;
		if (sdf == null) {
			sdf = new SimpleDateFormat(SimpleDateFormats.ISO8601_date_pretty);
		}
		ArrayList<String> retval = new ArrayList<>();
		for (Date d : dateMap.keySet()) {
			String v = sdf.format(d);
			retval.add(v);
		}
		return retval;
	}
}
