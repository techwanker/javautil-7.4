package org.javautil.core.misc;

import java.util.ArrayList;
import java.util.TreeMap;

public class BucketsList extends ArrayList<Buckets> {

	private TreeMap<String,Object> attributes;
	
	public BucketsList() {
		super();
		attributes = new TreeMap<String,Object>();
	}
	
	public BucketsList(TreeMap<String,Object> attributes) {
		this.attributes = attributes;
	}
	
	public Object putAttribute(String key, Object value) {
		return this.attributes.put(key,value);
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}
}
