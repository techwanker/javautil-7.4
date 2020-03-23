package org.javautil.persist.hibernate;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.javautil.hibernate.HibernateMarshallerFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;




public class GsonTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Ignore
	@Test
	public void test() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		String json = gson.toJson(new TestData());
		assertNotNull(json);
		logger.warn(json);
	}
	
	
	@Test
	public void test2() {
		//GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = HibernateMarshallerFactory.getHibernateGson();
		String json = gson.toJson(new TestData());
		assertNotNull(json);
		logger.warn(json);
	}
	
	class TestData {
		BigDecimal bd = new BigDecimal(9);
		DecimalFormat df  = new DecimalFormat();
	}
	
	
}