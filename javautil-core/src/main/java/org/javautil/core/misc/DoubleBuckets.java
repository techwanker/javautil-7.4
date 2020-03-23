package org.javautil.core.misc;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import org.javautil.core.text.SimpleDateFormats;
import org.javautil.util.DateGenerator;

public class DoubleBuckets extends  Buckets<Double>  {
	// implements GsonSerializer {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	private List<String> identifiers;
	private transient SimpleDateFormat sdf = new SimpleDateFormat(SimpleDateFormats.ISO8601_date_pretty);
//	//private TreeMap<Date,Double> dateMap = new TreeMap<>();
//	private Date lastBucketEndsBeforeDate;
//	private Double beforeData;
//	private Double afterData;
//	private transient Logger logger = LoggerFactory.getLogger(getClass());
//	//private transient DateGenerator dateGenerator;
//	private String identifierFormat = "%-16s ";
//	private String valueFormat = "%10f ";
	private String nullValueFormat = String.format("%10s ","");
//	
  	public DoubleBuckets(DateGenerator dg, Double initialValue) {
		super(dg,initialValue);
	}
	
	public DoubleBuckets(MultiKey identifiers, DateGenerator dg) {
		super(identifiers,dg);
	}

	public DoubleBuckets(DateGenerator dg) {
		super(dg);
	}
	

	public DoubleBuckets(Collection<Date> dates, Date lastBucketEndsBeforeDate) {
		super(dates,lastBucketEndsBeforeDate);
	}

	
	public DoubleBuckets(MultiKey key, TreeMap<Date, Double> dateMap2) {
		super(key,dateMap2);
	}


	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DoubleBuckets= ");
		//builder.append(dateMap);
		builder.append("lastBucketEndsBeforeDate="); 
		builder.append(getLastBucketEndsBeforeDate());
		builder.append(" ");

		builder.append("beforeData: ");
		builder.append(getBeforeData());
		builder.append(" ");
		builder.append("afterData: ");
		builder.append(getAfterData());
		
		builder.append("dateMap.size() " + getDateMap().size());
			builder.append("\n");

		for (Date k : getDateMap().keySet()) {
			builder.append(sdf.format(k));
			builder.append(" ");
		}
		builder.append("\n");

		for (Date k : getDateMap().keySet()) {
			Double v = getDateMap().get(k);
			if (v == null) {
				builder.append(nullValueFormat);
			} else {
				builder.append(String.format("%10f ",v));
			}
		}
		builder.append("\n");

		return builder.toString();
	}

	
	public void increment(Date date, double d) {
		Double v = (java.lang.Double) getBucketData(date);
		if (v == null) {
			v = 0.0;
		}
		v += d;
		put(date,v);
	}

}
