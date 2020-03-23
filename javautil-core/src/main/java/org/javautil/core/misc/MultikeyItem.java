//package org.javautil.core.misc;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MultikeyItem <T> {
//
//	private ArrayList<Comparable> key;
//	private T value;
//	
//	
//	protected MultikeyItem() {
//		
//	}
//	
//	public MultikeyItem(ArrayList<Comparable> k, T v) {
//		this.key = k;
//		this.value = v;
//	}
//
//	
//	public static ArrayList<Comparable> getKeys(List<Comparable> list, int... indices) {
//		ArrayList<Comparable>  retval = new  ArrayList<Comparable>(indices.length);
//		for (int index : indices) {
//			retval.add(list.get(index));
//		}
//		return retval;
//	}
//	
//	public ArrayList<Comparable> getKeys(int... indices) {
//		ArrayList<Comparable> retval= new ArrayList<>();
//		for (int index : indices) {
//			retval.add(key.get(index));
//		}
//		return retval;
//	}
//	/**
//	 * @return the key
//	 */
//	public ArrayList<Comparable> getKey() {
//		return key;
//	}
//
//	/**
//	 * @param key the key to set
//	 */
//	public void setKey(ArrayList<Comparable> key) {
//		this.key = key;
//	}
//
//	/**
//	 * @return the value
//	 */
//	public T getValue() {
//		return value;
//	}
//
//	/**
//	 * @param value the value to set
//	 */
//	public void setValue(T value) {
//		this.value = value;
//	}
//
//}
