package org.javautil.core.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MultiKeyTreeMapOfLists<T>  extends TreeMap<MultiKey,ArrayList<T>> {
        
	
	public void putAdd(MultiKey k,  T v) {
		ArrayList<T> list = get(k);
		if (list == null)  {
			list  = new ArrayList<T> ();
			put(k,list);
		}
		list.add(v);
	}
	
	public String format() {
		StringBuilder sb = new StringBuilder();
		Collection<MultiKey> keys = this.keySet();
		Collection<ArrayList<T>> values = this.values();
		
		for (Entry<MultiKey, ArrayList<T>> e : this.entrySet()) {
			sb.append(e.getKey().format());
			sb.append("\n");
			for (T v : e.getValue()) {
				sb.append(" ");
				sb.append(v.toString());
			}
		}
		return sb.toString();
	}
	

	
	

}
