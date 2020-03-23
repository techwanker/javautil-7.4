package org.javautil.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class TreeHash {

	// TODO document
	// TODO this is just plain goofy
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String hash(@SuppressWarnings("rawtypes") Map map) {
		@SuppressWarnings("rawtypes")
		TreeMap tree = null;
		if (!(map instanceof TreeMap)) {
			tree = new TreeMap();
			tree.putAll(map);
		} else {
			tree = (TreeMap) map;
		}
		StringBuilder b = new StringBuilder();
		for (Object k : map.keySet()) {
			b.append("\"");
			b.append(k.toString());
			b.append("\"");
			b.append(":");
			Object o = tree.get(k);
			b.append(o == null ? "null" : k.toString());
			b.append(",");
		}
		String text = b.toString().substring(0, b.length() - 1);
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

}
