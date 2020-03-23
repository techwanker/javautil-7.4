package org.javautil.core.misc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.javautil.core.json.JsonSerializer;
import org.javautil.core.json.JsonSerializerGson;
import org.javautil.core.text.SimpleDateFormats;
import org.javautil.util.DateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class BucketData<T>  {
	// JsonSerializer {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	private MultiKey identifiers;
	private transient SimpleDateFormat sdf = new SimpleDateFormat(SimpleDateFormats.ISO8601_date_pretty);
	private TreeMap<Date,T> dateMap = new TreeMap<>();
	private Date lastBucketEndsBeforeDate;
	private T beforeData;
	private T afterData;
	private transient Logger logger = LoggerFactory.getLogger(getClass());
	private transient DateGenerator dateGenerator;
	private transient JsonSerializer serializer = new JsonSerializerGson();
	
	public BucketData(TreeMap<Date,T> dateMap) {
		this.dateMap = dateMap;
	}
	
	public BucketData(DateGenerator dg, T initialValue) {
		for (Date date : dg.getDays()) {
			dateMap.put(date,initialValue);
		}
		this.lastBucketEndsBeforeDate = dg.getLastBucketEndsBeforeDate();
		this.dateGenerator = dg;
	}
	

	public BucketData(DateGenerator dg) {
		for (Date date : dg.getDays()) {
			dateMap.put(date,null);
		}
		this.lastBucketEndsBeforeDate = dg.getLastBucketEndsBeforeDate();
		this.dateGenerator = dg;
	}
	

	public BucketData(Collection<Date> dates, Date lastBucketEndsBeforeDate) {
		if (dates == null) {
			throw new IllegalArgumentException("dates is null");
		}
		if (dates.size() < 1) {
			throw new IllegalArgumentException("no dates");
		}
		if (lastBucketEndsBeforeDate == null) {
			throw new IllegalArgumentException("lastBucketEndsBeforeDate is null");
		}
		for (Date date : dates) {
			dateMap.put(date,null);
		}
		this.lastBucketEndsBeforeDate = lastBucketEndsBeforeDate;
	}


	public LinkedHashMap<String,T> getBucketMap(boolean withBefore, boolean withAfter) {
		LinkedHashMap<String,T> retval = new LinkedHashMap<>();
		if (withBefore) {
			retval.put("Before",beforeData);
		}
		for (Date d : dateMap.keySet() ) {
			String k = sdf.format(d); 
			//			String message = String.format("putting key is %s class: %s", k, k.getClass());
			//			logger.info(message);
			retval.put(k,dateMap.get(d));
		}
		if (withAfter) {
			retval.put("After",afterData);
		}
		return retval;
	}
	

	public T getBucketData(java.util.Date date) {
		T retval;
		int domain = getBucketDomain(date);
		switch (domain) {
		case -1:
			retval = beforeData;
			break;
		case 0:
			Date key = dateMap.floorKey(date);
			retval = dateMap.get(key);
			break;
		case 1:
			retval = afterData;
			break;
		default:
			throw new IllegalStateException("unknown bucket error");
		}
		return retval;
	}

	public TreeMap<Date,T> getDateMap() {
		return dateMap;
	}

	public int getBucketDomain(java.util.Date date) throws java.lang.IllegalArgumentException {
		int rc = 0;
		if (date == null) {
			throw new java.lang.IllegalArgumentException("date is null");
		}
		Date minKey = dateMap.firstKey();
		if (date.before(minKey)) {
			rc = -1;
		} else {
			if (lastBucketEndsBeforeDate == null) {
				throw new java.lang.IllegalStateException("lastBucketEndsBeforeDate has not been established");
			} else {
				if (! date.before(lastBucketEndsBeforeDate)) {
					rc = 1;
				}
			}
		}
		return rc;
	}


	public T put(Date d, T value) {
		T retval = null;
		int domain = getBucketDomain(d);
		switch (domain) {
		case -1:
			retval = beforeData;
			beforeData = value;
			break;
		case 0:
			Date key = dateMap.floorKey(d);
			retval = dateMap.put(key,value);
			break;
		case 1:
			retval = afterData;
			afterData = value;
		default:
			throw new IllegalStateException();
		}
		return retval;
	}

	public List<T> getValues() {
		List<T> retval = new ArrayList<T>();
		retval.add(beforeData);
		retval.addAll(dateMap.values());
		retval.add(afterData);
		return retval;

	}
	public Date getLastBucketEndsBeforeDate() {
		return lastBucketEndsBeforeDate;
	}
	
	public String asLine() {
		StringBuilder builder = new StringBuilder();
		builder.append("Buckets [dateMap=");
		builder.append(dateMap);
		builder.append("lastBucketEndsBeforeDate:"); 
		builder.append(lastBucketEndsBeforeDate);
		builder.append("\n");

		builder.append("beforeData: ");
		builder.append(beforeData);
		/*
		for (Date k : dateMap.keySet()) {
			builder.append(sdf.format(k));
			builder.append(" ");
		}
		builder.append("\n");
		for (Date k : dateMap.keySet()) {
			T v = dateMap.get(k);
			if (v == null) {
				builder.append("null");
			} else {
				builder.append(String.format("%10f ",v));
			}
		}
		*/
		builder.append("\n");
		builder.append("afterData:");
		builder.append(afterData);
		builder.append("\n");

		return builder.toString();
		
	}

	public String format(boolean emitDates) {
		StringBuilder sb = new StringBuilder();
		sb.append(getIdentifiers().format());
		for (T v :  dateMap.values()) {
			double value = (Double) v;
			sb.append(String.format("%10f ",value));
		}
		sb.append("\n");
		return sb.toString();
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Buckets [dateMap=");
		builder.append(dateMap);
		builder.append("lastBucketEndsBeforeDate:"); 
		builder.append(lastBucketEndsBeforeDate);
		builder.append("\n");

		builder.append("beforeData: ");
		builder.append(beforeData);
		builder.append("\n");


		for (Date k : dateMap.keySet()) {
			builder.append(k.toString());
			builder.append(" ");
			T v = dateMap.get(k);
			if (v == null) {
				builder.append("null");
			} else {
				builder.append(dateMap.get(k).toString());
			}
			builder.append("\n");
		}
		builder.append("afterData:");
		builder.append(afterData);
		builder.append("\n");

		return builder.toString();
	}

	public void setIdentifiers(MultiKey identifiers) {
		this.identifiers = identifiers;
	}

	public MultiKey getIdentifiers() {
		return identifiers;
	}

	public Collection<Date> getDateMapKeySet() {
		return dateMap.keySet();
	}
	
	public void increment(Date date, double d) {
		Double v = (java.lang.Double) getBucketData(date);
		if (v == null) {
			v = 0.0;
		}
		v += d;
		put(date,(T)v);
	}
	
	public DateGenerator getDateGenerator() {
		return dateGenerator;
	}
	
	public String toJson(Gson dillon) {
		return dillon.toJson(this);
	}

	public String toJson() {
		return serializer.toJson(this);
	}

	public String toJsonPretty() {
		return serializer.toJsonPretty(this);
	}

}
