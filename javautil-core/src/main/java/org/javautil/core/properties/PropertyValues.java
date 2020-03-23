package org.javautil.core.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

class PropertyValues {
	private HashMap<String, PropertyValue> byName = new HashMap<String, PropertyValue>();

	public String[] getSortedNames() {
		TreeMap<String, String> propertyNames = new TreeMap<String, String>();
		String rc[] = new String[byName.size()];
		synchronized (byName) {
			Iterator<String> names = byName.keySet().iterator();
			while (names.hasNext()) {
				String nm = names.next();
				propertyNames.put(nm, nm);
			}
			Iterator it = byName.values().iterator();
			for (int ndx = 0; it.hasNext(); ndx++) {
				PropertyValue pv = (PropertyValue) it.next();
				rc[ndx] = pv.getName();
			}
			return rc;
		}

	}

	void add(PropertyValue arg) {
		if (byName.get(arg.getName()) == null) {
			put(arg);
		} else {
			throw new java.lang.IllegalArgumentException(arg.getName() + "has already been added");
		}
	}

	PropertyValue get(String arg) {
		return byName.get(arg);
	}

	void put(PropertyValue arg) {
		byName.put(arg.getName(), arg);
	}
}
