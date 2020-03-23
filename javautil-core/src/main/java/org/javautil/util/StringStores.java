package org.javautil.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StringStores {
	private HashMap<String, HashMap<String, String>> stores = new HashMap<String, HashMap<String, String>>();

	public void add(String poolName, String value) {
		HashMap<String, String> store = stores.get(poolName);
		if (store == null) {
			store = new HashMap<String, String>();
			stores.put(poolName, store);
			// throw new IllegalArgumentException("no such store: '" + poolName + "'");
		}
		store.put(value, value);
	}

	public void addAll(StringStores store) {
		for (Map.Entry<String, HashMap<String, String>> entry : store.stores.entrySet()) {
			String storeKey = entry.getKey();
			HashMap<String, String> values = entry.getValue();
			for (String value : values.values()) {
				this.add(storeKey, value);
			}
		}
	}

	public void addStore(String storeName) {
		HashMap<String, String> store = stores.get(storeName);
		if (store == null) {
			store = new HashMap<String, String>();
			stores.put(storeName, store);
		}

	}

	public Collection<String> getStore(String storeName) {
		Collection<String> returnValue = null;
		HashMap<String, String> store = stores.get(storeName);
		if (store != null) {
			returnValue = store.values();
		}
		return returnValue;
	}
}
