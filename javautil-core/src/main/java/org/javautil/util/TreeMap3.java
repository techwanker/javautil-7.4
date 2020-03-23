package org.javautil.util;

import java.util.Map;
import java.util.TreeMap;

public class TreeMap3 {

	private TreeMap<Object, TreeMap> mapA = new TreeMap();

	public Object put(Object a, Object b, Object c, Object value) {
		Object retval = null;
		TreeMap<Object, TreeMap> mapB = mapA.get(a);
		TreeMap<Object, Object> mapC;
		if (mapB == null) {
			mapB = new TreeMap<>();
			mapA.put(a, mapB);
		}
		mapC = mapB.get(b);
		if (mapC == null) {
			mapC = new TreeMap();
			mapB.put(b, mapC);
		}
		retval = mapC.put(c, value);
		return retval;
	}

	public TreeMap get(Object a) {
		return mapA.get(a);
	}

	public TreeMap get(Object a, Object b) {
		TreeMap retval = null;
		Map<Object, TreeMap> mapB = get(a);
		if (mapB != null) {
			retval = mapB.get(b);
		}
		return retval;
	}

	public Object get(Object a, Object b, Object c) {
		Object retval = null;
		TreeMap mapC = get(a, b);
		if (mapC != null) {
			retval = mapC.get(c);
		}
		return retval;
	}
}
