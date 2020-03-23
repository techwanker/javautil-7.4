package org.javautil.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.TreeMap;

import org.junit.Test;

public class TreeMap3Test {

	@Test
	public void test() {
		TreeMap3 tm = new TreeMap3();
		Object abc = tm.put("a", "b", "c", "d");
		assertNull(abc);
		Object d = tm.get("a", "b", "c");
		assertEquals("d", d);
		TreeMap<Object, TreeMap> mapB = tm.get("a");
		assertEquals(1, mapB.size());
		TreeMap mapC = mapB.get("b");
		d = mapC.get("c");
		assertEquals("d", d);
	}

}
