package org.javautil.lang.reflect;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * Key is Name.
 * 
 * @author jjs
 * 
 */
public class FieldCache extends LinkedHashMap<String, FieldCacheEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7933646897449815626L;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, FieldCacheEntry> e : super.entrySet()) {
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
}
