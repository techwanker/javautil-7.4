package org.javautil.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NullCompareTest {

	private static final String dog = "dog";

	@Test
	public void test1() {

		NullPair nc = new NullPair(true); // null collates low
		assertEquals(-1, nc.compare(null, dog));
		assertEquals(1, nc.compare(dog, null));
		assertEquals(0, nc.compare(null, null));
		assertEquals(-1, nc.compare("cat", "dog"));
		nc = new NullPair(false); // null collates low
		assertEquals(1, nc.compare(null, dog));
		assertEquals(-1, nc.compare(dog, null));
		assertEquals(0, nc.compare(null, null));
		assertEquals(-1, nc.compare("cat", "dog"));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void test2() {

		final NullPair nc = new NullPair(true); // null collates low
		assertEquals(-1, nc.compare(2, dog));

	}
}
