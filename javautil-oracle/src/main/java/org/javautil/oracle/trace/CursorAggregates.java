package org.javautil.oracle.trace;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.javautil.oracle.trace.record.CursorRecord;
import org.javautil.oracle.trace.record.Parsing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Aggregated cursors
 * 
 * @author jjs
 * 
 */
public class CursorAggregates {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LinkedHashMap<String, CursorAggregate> aggregatesById = new LinkedHashMap<String, CursorAggregate>();
	private final static String newline = System.getProperty("line.separator");
	private int surrogateId = 1;
	private final HashMap<Integer, CursorAggregate> aggregatesByCursorNumber = new HashMap<Integer, CursorAggregate>();

	public void aggregate(CursorRecord event) {
		/**
		 * if (event.getSqlid() == null) { throw new
		 * IllegalStateException("event has no sqlid " + event); }
		 */
		// if (event.getId().equals("0k8522rmdzg4k")) {
		CursorAggregate agg = getCursorAggregate(event);
		agg.aggregate(event);
		// }

	}

	public CursorAggregate getCursorAggregate(CursorRecord event) {

		StringBuilder sb = null;

		if (logger.isDebugEnabled()) {
			sb = new StringBuilder();
			sb.append("getting aggregate for event: " + event + newline);
		}

		CursorAggregate aggregateCursor = null;

		if (event instanceof Parsing) {
			aggregateCursor = aggregatesById.get(event.getSqlid());
			aggregatesByCursorNumber.put(event.getCursorNumber(), aggregateCursor);
		} else {
			aggregateCursor = aggregatesByCursorNumber.get(event.getCursorNumber());
		}
		if (aggregateCursor == null) {
			aggregateCursor = new CursorAggregate(event, surrogateId++);

			aggregatesByCursorNumber.put(event.getCursorNumber(), aggregateCursor);
			if (logger.isDebugEnabled()) {
				sb.append("cursor # " + event.getCursorNumber() + "in aggregatesByCursorNumber " + aggregateCursor
						+ newline);
			}
			aggregatesById.put(aggregateCursor.getId(), aggregateCursor);
		}

		// TODO should remove on STAT record
		if (logger.isDebugEnabled()) {
			sb.append("returning " + aggregateCursor);
			logger.debug(sb.toString());
		}
		return aggregateCursor;
	}

	// public void aggregateParsing(Parsing parsing) {
	// aggre
	// }

	public HashMap<String, CursorAggregate> getAggregatesById() {
		return aggregatesById;
	}

	// public HashMap<String, CursorAggregate> getAggregateCursorsById() {
	// return aggregatesBySqlid;
	// }
}
