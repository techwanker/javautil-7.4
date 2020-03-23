package org.javautil.core.collections;

import java.util.Collection;

public class CollectionFormatter {

//	public static String toString(List<? extends Object> rows) {
//		StringBuilder sb = new StringBuilder();
//		for (Object row : rows) {
//			sb.append(row);
//			sb.append("\n"); // TODO add to CollectionsUtil
//		}
//		return sb.toString();
//	}

	public static String toString(Collection<? extends Object> rows) {
		StringBuilder sb = new StringBuilder();
		for (Object row : rows) {
			sb.append(row);
			sb.append("\n"); // TODO add to CollectionsUtil
		}
		return sb.toString();
	}

}
