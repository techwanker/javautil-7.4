package org.javautil.core.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MapUtil {
	public static LinkedHashMap<Object, Object> zip(Iterable<Object> keys, Iterable<Object> values) {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
		Iterator<Object> keyIter = keys.iterator();
		Iterator<Object> valuesIter = values.iterator();
		while (keyIter.hasNext()) {
			Object k = keyIter.next();
			if (valuesIter.hasNext()) {
				map.put(k, valuesIter.next());
			} else {
				throw new IllegalArgumentException("more keys than values");
			}
		}
		return map;
	}

}
