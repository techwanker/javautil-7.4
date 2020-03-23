package org.javautil.core.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StringHelperTest {

	@Test
	public void testGetChars() {
		char[] result;
		result = StringUtils.getChars("ABC");
		assertEquals(3, result.length);
		assertEquals('A', result[0]);
		result = StringUtils.getChars(null);
		assertNull(result);

	}
}
