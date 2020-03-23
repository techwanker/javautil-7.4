package org.javautil.core.misc;

import java.util.ArrayList;
import java.util.List;

import org.javautil.core.collections.ListComparator;

public class MultiKey extends ArrayList<Comparable> implements Comparable{


	protected MultiKey() {
	}

	protected MultiKey(int n) {
		super(n);
	}

	public MultiKey(Object ... component) {
		for (Object o : component) {
			Comparable c = (Comparable) o;
			add(c);
		}
	}


	public MultiKey(ArrayList<? extends Comparable> k) {
		super(k);
	}

	public ArrayList<String> getAutoFormats(MultiKey key) {
		// TODO check max length for dates etc
		int i =  key.size();
		ArrayList<String> retval = new ArrayList<String>();
		while (i-- > 0) {
			retval.add("%-16s");
		}
		return retval;
	}

	public MultiKey getMultiKey(int... indices) {
		ArrayList<Comparable>  retval = new  ArrayList<Comparable>(indices.length);
		for (int index : indices) {
			retval.add(super.get(index));
		}
		return new MultiKey(retval);
	}

	public ArrayList<String> getAutoFormats(List<MultiKey> keys) {
		// TODO check max length for dates etc
		int i =  keys.get(0).size();
		ArrayList<String> retval = new ArrayList<String>();
		while (i-- > 0) {
			retval.add("%-16s");
		}
		return retval;
	}

	public String format(ArrayList<String> formats) {
		StringBuilder sb = new StringBuilder();
		int i  = 0;
		// TODO check ranges
		for (Comparable v : this) {
			sb.append(String.format(formats.get(i++),v));
			sb.append(" ");
		};
		return sb.toString();
	}

	public String format() {
		ArrayList formats = getAutoFormats(this);
		return format(formats);
	}

	public ArrayList<String> asStringList() {
		ArrayList<String> retval=new  ArrayList<String>();
		for (Comparable b : this) {
			if  (b == null) {
				retval.add(null);
			} else {
				retval.add(b.toString());  // TODO need a date formatter
			}
		}
		return retval;
	}

	@Override
	public int compareTo(Object arg) {
		ListComparator compie = new  ListComparator();
		return compie.compare(this,arg);
	}

}
