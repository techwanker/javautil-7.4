package org.javautil.oracle.trace.aggregation;

import java.util.TreeMap;

import org.javautil.oracle.trace.record.StatAggregation;

public class StatAggregations extends TreeMap<Integer, StatAggregation> {
	public void setDepths() {
		for (StatAggregation stat : this.values()) {
			if (stat.getPid() == 0) {
				stat.setDepth(0);
			} else {
				StatAggregation parentStat = get(stat.getPid());
				stat.setDepth(parentStat.getDepth() + 1);
			}

		}
	}
}
