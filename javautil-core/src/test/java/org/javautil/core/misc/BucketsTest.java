package org.javautil.core.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.javautil.util.DateGenerator;
import org.javautil.util.Day;
import org.junit.Test;

public class BucketsTest {

	public BucketsTest() {

	}

	@Test
	public void test() {
		DateGenerator dg = new DateGenerator();
		
		Buckets<Double> b = new Buckets<Double>(getDates(dg),dg.getLastBucketEndsBeforeDate());
		Date before = new Day(2018,12,25);
		Double payload = b.getBucketData(before);
		assertNull(payload);
		b.put(before,new Double(2.0));
		payload = b.getBucketData(before);
		assertEquals(new Double(2.0),payload);
	
		Date vday = new Day(2019,2,14);
		b.put(vday,new Double(3.14));
		Date noVday = new Day(2019,2,16);
		Double value = b.getBucketData(noVday);
		assertEquals(new Double(3.14),value);
		
		List<Double> values= b.getValues();
		assertEquals(14,values.size());
		assertEquals(new Double(2.0),values.get(0));
		assertEquals(new Double(3.14),values.get(2));
	}

	private List<Date> getDates(DateGenerator dg) {
	
		Date firstDate = new Day(2019,1,1);
		dg.setFirstDate(firstDate);

		dg.setIncrementInMonths(1);
		dg.generateBuckets(12);
		List<Date>dateBuckets = new ArrayList<Date>();
		for (Iterator<Date> dateIterator = dg.getDateIterator(); dateIterator.hasNext();) {
			Date dt = dateIterator.next();
			dateBuckets.add(dt);
		}
		return dateBuckets;
	}
}
