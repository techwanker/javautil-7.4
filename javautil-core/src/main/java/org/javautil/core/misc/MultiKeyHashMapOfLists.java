package org.javautil.core.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class MultiKeyHashMapOfLists<T>  extends LinkedHashMap<MultiKey,ArrayList<T>> {
        
    private ArrayList<String>  formats;
    
//	public MultiKeyHashMapOfLists(ArrayList<String> formats) {
//		if (formats != null) {
//			this.formats = formats;
//		} else {
//			formats = new ArrayList<String>();
//			formats.add("%8s");
//			formats.add("%8s");
//			formats.add("%8s");
//			formats.add("%20s");
//		}
//	}
	
	public void putAdd(MultiKey k,  T v) {
		ArrayList<T> list = get(k);
		if (list == null)  {
			list  = new ArrayList<T> ();
			put(k,list);
		}
		list.add(v);
	}

	
	public String format(ArrayList<String> formats, int maxIndex) {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		Collection<MultiKey> keys = this.keySet();
		Collection<ArrayList<T>> values = this.values();
		
		for (Entry<MultiKey, ArrayList<T>> e : this.entrySet()) {
			sb.append(e.getKey().format(formats));
			sb.append("\n");
			for (T v : e.getValue()) {
				sb.append(" ");
				sb.append(v.toString());
				if (++index > maxIndex) {
					break;
				}
			}
		}
		return sb.toString();
	}
	

	
	

}
