package org.javautil.core.csv;

import static org.junit.Assert.assertEquals;

import org.javautil.core.collections.ListHelper;
import org.javautil.dataset.csv.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 */
public class CsvWriterTest extends BaseTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test3() {
		final CsvWriter cw = new CsvWriter();
		final Object[] oa = new Object[] { "STATE", Integer.valueOf(0), "STRING", null, null, null, null, null, null,
		    Boolean.FALSE, null, null, null, null, null, null };
		final String expected = "\"STATE\",0,\"STRING\",,,,,,,\"false\"";
		final String actual = cw.asString(ListHelper.toList(oa));
		logger.debug("e " + expected);
		logger.debug("a " + actual);

		assertEquals(expected, actual);
	}

	@Test
	public void test1() {
		final CsvWriter cw = new CsvWriter();
		Object[] oa = new Object[] { "A", "B" };

		assertEquals("\"A\",\"B\"", cw.asString(ListHelper.toList((oa))));

		oa = new Object[] { "A", null };
		assertEquals("\"A\"", cw.asString(ListHelper.toList(oa)));

		oa = new Object[] { "\"A", null };
		assertEquals("\"\"\"A\"", cw.asString(ListHelper.toList(oa)));
	}

}
