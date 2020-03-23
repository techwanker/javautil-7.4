package org.javautil.core.collections;

import java.util.Collection;

public class CollectionsUtil {

	/**
	 * @param cols collection
	 * @return new line separated collection item toString()
	 */
	public static String asString(Collection<? extends Object> cols) {
		StringBuilder sb = new StringBuilder();
		for (Object o : cols) {
			sb.append(o.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static int maxStringLength(Collection<String> cols) {
		int maxLength = -1;
		for (String col : cols) {
			maxLength = col.length() > maxLength ? 
			    col.length() : maxLength;
		}
		return maxLength;
	}
}
