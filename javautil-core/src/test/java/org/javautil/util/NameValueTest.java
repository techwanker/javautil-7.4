package org.javautil.util;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.junit.Test;

public class NameValueTest {

	@Test
	public void test() {
		NameValue orig = new NameValue();
		orig.put("ic_item_mast", 1);
		orig.put("IC_BIN", 2);

		NameValue camel = orig.asAttributeName();
		int i = 0;
		for (Entry<String, Object> entry : camel.entrySet()) {
			switch (i) {
			case 0:
				assertEquals("icItemMast", entry.getKey());
				assertEquals(1, entry.getValue());
				break;
			case 1:
				assertEquals("icBin", entry.getKey());
				assertEquals(2, entry.getValue());
				break;

			}
			i++;

		}
	}

}
