package org.javautil.core.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiKeyHashMap<T> extends LinkedHashMap<MultiKey,T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6263828110335813976L;
    private Logger logger  = LoggerFactory.getLogger(getClass());
    
	public MultiKeyHashMap() {
		super();
	}
	// TODO take a list of Strings for groupBy these will be key fields for identfier
		public MultiKeyHashMapOfLists<T> groupBy(int... indicies) {
			MultiKeyHashMapOfLists<T> grouped = new MultiKeyHashMapOfLists<T>();
			StringBuilder indexSB = new StringBuilder();
			indexSB.append("indexes ");
			for (int n : indicies) {
				indexSB.append(n);
				indexSB.append(" ");
			}
			for (Entry<MultiKey,T> e : entrySet()) {
				MultiKey supplyId  = e.getKey();
				MultiKey groupById = supplyId.getMultiKey(indicies);

				ArrayList<T> buckets = grouped.get(groupById);
				if (buckets == null) {
					buckets = new ArrayList<T>();
					grouped.put(groupById,buckets);
				}
				buckets.add(e.getValue());
				logger.info("adding to : {}\n value: {} :DONE:s", groupById.format(), e.getValue());
			}
			return grouped;
		}

	public String format() {
		StringBuilder sb = new StringBuilder();
		for (Entry<MultiKey,T> e : entrySet()) {
			sb.append(e.getKey().format());
			sb.append(" ");
			sb.append(e.getValue().toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	

	

}
