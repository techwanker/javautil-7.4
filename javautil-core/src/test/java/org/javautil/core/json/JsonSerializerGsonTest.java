package org.javautil.core.json;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;

import org.javautil.core.json.JsonSerializerGson;
import org.javautil.core.misc.Timer;
import org.javautil.util.Day;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// TODO make superclass for used with JsonSerializerGson
public class JsonSerializerGsonTest {
	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	JsonSerializerGson dillon = new JsonSerializerGson();


	public class DateFactory {
		public  Date getDate(int year, int month, int day) {
			return new GregorianCalendar(year, month - 1, day).getTime();
		}
	}

	DateFactory dateFactory = new DateFactory();

	@Ignore
	@Test
	public void testLocalGson() {
		Date d = dateFactory.getDate(2019,12,5);
//		logger.info("d is {}",d);
		TestBean bean = new TestBean(d, "text", 420);
		Gson  gson= new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(bean);
		logger.info("json:\n{}",json);
	}

	TestBean getPopulatedTestBean() {
		Date d = dateFactory.getDate(2019,12,5);
		TestBean bean = new TestBean(d, "text", 420);
		return bean;
	}
	@Test
	public void testGsonSerializer() {
		TestBean bean = getPopulatedTestBean();
		String json = dillon.toJson(bean);
		logger.info("json:\n{}",json);
		LinkedHashMap<String, Object> map = dillon.toMapFromJson(json);
		String serialDate = (String) map.get("date");
		// make test work in any time zone
		assertEquals("2019-12-05T00:00:00",serialDate.substring(0,19));
		TestBean bean2 = (TestBean) dillon.toObjectFromJson(json,TestBean.class);
		assertEquals(bean,bean2);
	}


	public void testSerialization(int count) {
		TestBean bean = getPopulatedTestBean();
		Timer timer = new Timer();
		for (int i = 0; i < count; i++) {
			dillon.toJson(bean);
		}

		long elapsedMicros = timer.getElapsedMicros();
		logger.info("serialization elapsed millis {} for {} per {}",elapsedMicros,
				elapsedMicros / count, count);
	}




	public void testDeserialization(int count) {
		TestBean bean = getPopulatedTestBean();
		Timer timer = new Timer();
		String json = dillon.toJson(bean);
		for (int i = 0; i < count; i++) {
			dillon.toObjectFromJson(json,TestBean.class);
		}

		long elapsedMicros = timer.getElapsedMicros();
		logger.info("deserialization elapsed millis {} for {} per {}",elapsedMicros,
				elapsedMicros / count, count);
	}

	@Test
	public void testSerializationRange() {
		testSerialization(1);
		testSerialization(10);
		testSerialization(100);
		testSerialization(1000);
		testSerialization(10000);
	}




	@Test
	public void testDeserializationRange() {
		testDeserialization(1);
		testDeserialization(10);
		testDeserialization(100);
		testDeserialization(1000);
		testDeserialization(10000);
	}



}

