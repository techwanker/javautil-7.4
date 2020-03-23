/**
 * @(#) Cursor.java
 */
package org.javautil.oracle.trace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.javautil.oracle.trace.aggregation.CursorOperationAggregation;
import org.javautil.oracle.trace.aggregation.StatAggregations;
import org.javautil.oracle.trace.record.CursorOperation;
import org.javautil.oracle.trace.record.CursorRecord;
import org.javautil.oracle.trace.record.Exec;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.trace.record.RecordType;
import org.javautil.oracle.trace.record.Stat;
import org.javautil.oracle.trace.record.StatAggregation;
import org.javautil.oracle.trace.record.Wait;
import org.javautil.util.EventHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * There is nothing to aggregate on a Parsing.
 * 
 * @todo fix adding 1 to fetch count even if there is no fetch, maybe just on
 *       aggregation.
 */
public class CursorAggregate {
	private static ArrayList<Stat> emptyStats = new ArrayList<Stat>();
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static org.javautil.util.EventHelper events = new EventHelper();
	private static final Integer LOG_ADD_EVENT = 100;
	private static final Integer LOG_ADD_CHILD = 300;
	/**
	 *
	 */
	private String key;

	/**
	 *
	 */
	private ArrayList<Exec> execs;
	/**
	 * The sqlid if known else a surrogate.
	 */
	private String id;
	/**
	 * The sqlid field from the parsing record. If cursor actions are
	 * encountered with no prior Parsing record because trace was turned on
	 * after the parsing event this field will contain a numeric surrogate id.
	 */
	private String sqlId;
	/**
	 *
	 */
	/**
	 * Depth, Cursor Key,
	 */
	private final TreeMap<String, Cursor> childrenByKey = new TreeMap<String, Cursor>();
	private final HashSet<Cursor> children = new HashSet<Cursor>();
	private final boolean aggregateOnly = false;

	private CursorOperationAggregation fetchAggregation;

	private CursorOperationAggregation execAggregation;
	private CursorOperationAggregation parseAggregation;

	private CursorOperationAggregation unmapAggregation;
	private final String newline = System.getProperty("line.separator");
	private final boolean logConstructors = true;
	/**
	 * Keyed by wait event name
	 */
	private final HashMap<String, Wait> waitAggregations = new HashMap<String, Wait>();
	private Parsing parsing;
	private TreeMap<Integer, Stat> stats;
	// private final TreeMap<Integer, StatAggregation> statsById = new
	// TreeMap<Integer, StatAggregation>();
	private final StatAggregations statsById = new StatAggregations();
	private ArrayList<Wait> waits;

	public CursorAggregate(final CursorRecord record, int cursorId) {
		if (record.getRecordType().equals(RecordType.PARSING)) {
			if (parsing == null) {
				parsing = (Parsing) record;
				id = parsing.getSqlid();
			}
		} else {
			id = Integer.toString(cursorId);
		}

	}

	public void addChild(final Cursor child) {
		if (events.exists(LOG_ADD_CHILD)) {
			logger.info("adding child " + child.getKey() + " to " + getKey());
		}
		final String childKey = child.getKey();
		childrenByKey.put(childKey, child);
	}

	/**
	 * @param execAggregation
	 *            the execAggregation to set
	 */
	public void addChildren(final Collection<Cursor> children) {
		this.children.addAll(children);
	}

	/**
	 * It is possible this returns null if tracing was turned on after the
	 * statement was parsed.
	 * 
	 * @return
	 */
	public String getAddress() {
		return parsing == null ? null : parsing.getAddress();
	}

	public Collection<Cursor> getChildren() {
		return children;
	}

	public Collection<Cursor> getChildrenByKey() {
		return childrenByKey.values();
	}

	/**
	 * @return the cursorId
	 */
	public String getId() {
		return id;
	}

	public void aggregate(CursorOperationAggregation sum, CursorOperationAggregation in) {
		sum.aggregate(in);
	}

	/**
	 * @return the execAggregation
	 */
	public CursorOperationAggregation getExecAggregation() {
		return execAggregation;
	}

	/**
	 * @return the fetchAggregation
	 */
	public CursorOperationAggregation getFetchAggregation() {
		return fetchAggregation;
	}

	public CursorOperationAggregation getFetchAggregation(final boolean createIfNull) {
		if (fetchAggregation == null) {
			fetchAggregation = new CursorOperationAggregation();
		}
		return fetchAggregation;
	}

	public Long getHashValue() {
		return parsing == null ? -1 : parsing.getHashValue();
	}

	public String getKey() {
		if (key == null) {
			if (getAddress() != null && getHashValue() != null) {
				key = getAddress() + "-" + getHashValue();
			}
		}
		return key;
	}

	/**
	 * @return the parseAggregation
	 */
	public CursorOperationAggregation getParseAggregation() {
		return parseAggregation;
	}

	/**
	 * @return the parsing
	 */
	public Parsing getParsing() {
		return parsing;
	}

	/**
	 * @return the stats
	 * 
	 */
	public Collection<Stat> getStats() {
		Collection<Stat> returnValue;
		if (stats == null) {
			returnValue = emptyStats;
		} else {
			returnValue = stats.values();
		}
		return returnValue;
	}

	/**
	 * @return the unmapAggregation
	 */
	public CursorOperationAggregation getUnmapAggregation() {
		return unmapAggregation;
	}

	/**
	 * @return the waitAggregations
	 */
	public HashMap<String, Wait> getWaitAggregations() {
		return waitAggregations;
	}

	// /**
	// * @param key the key to set
	// */
	// public void setKey(String key) {
	// this.key = key;
	// }
	public boolean matches(final Cursor cursor) {
		boolean returnValue = false;
		if (cursor == null) {
			throw new IllegalArgumentException("cursor argument is null");
		}
		final String thisKey = getKey();
		final String otherKey = cursor.getKey();
		if (thisKey != null && otherKey != null) {
			returnValue = thisKey.equals(otherKey);
		}
		return returnValue;
	}

	/**
	 * @param cursorId
	 *            the cursorId to set
	 */
	// public void setCursorId(final Integer cursorId) {
	// this.cursorId = cursorId;
	// }

	/**
	 * @param execAggregation
	 *            the execAggregation to set
	 */
	public void setExecAggregation(final CursorOperationAggregation execAggregation) {
		this.execAggregation = execAggregation;
	}

	/**
	 * @param execs
	 *            the execs to set
	 */
	public void setExecs(final ArrayList<Exec> execs) {
		this.execs = execs;
	}

	/**
	 * @param fetchAggregation
	 *            the fetchAggregation to set
	 */
	public void setFetchAggregation(final CursorOperationAggregation fetchAggregation) {
		this.fetchAggregation = fetchAggregation;
	}

	/**
	 * @param parseAggregation
	 *            the parseAggregation to set
	 */
	public void setParseAggregation(final CursorOperationAggregation parseAggregation) {
		this.parseAggregation = parseAggregation;
	}

	/**
	 * @param parsing
	 *            the parsing to set
	 */
	public void setParsing(final Parsing parsing) {
		if (this.parsing != null) {
			throw new IllegalStateException("may only call this once per cursor invoke from line number "
					+ parsing.getLineNumber() + " was declared from " + this.parsing.getLineNumber());
		}
		this.parsing = parsing;
	}

	/**
	 * @param waits
	 *            the waits to set
	 */
	public void setWaits(final ArrayList<Wait> waits) {
		this.waits = waits;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();

		if (parsing != null) {
			b.append(parsing.toString());
			b.append(newline);
		}
		appendAggregation(b, parseAggregation);
		appendAggregation(b, fetchAggregation);
		appendAggregation(b, execAggregation);
		final String retval = b.toString();
		return retval;
	}

	void aggregate(final CursorRecord rec) {

		final RecordType r = rec.getRecordType();
		switch (r) {
		case EXEC:
		case PARSE:
		case FETCH:
		case UNMAP:
			aggregateOperation((CursorOperation) rec);
			break;
		case PARSING:
			aggregateParsing((Parsing) rec);
			break;
		case WAIT:
			aggregateWait((Wait) rec);
			break;
		case STAT:
			aggregateStat((Stat) rec);
			break;
		default:
			logger.info("uncategorized event " + rec);
		}
	}

	void aggregateStat(Stat stat) {
		StatAggregation agg = statsById.get(stat.getId());
		if (agg == null) {
			agg = new StatAggregation(stat);
			statsById.put(agg.getId(), agg);
		}
		agg.aggregate(stat);
	}

	private void aggregateParsing(Parsing rec) {
		if (parsing == null) {
			this.parsing = rec;
		}
		// aggregateOperation(rec);

	}

	CursorOperationAggregation getTotalAggregation() {
		CursorOperationAggregation retval = new CursorOperationAggregation();
		retval.aggregate(parseAggregation);
		retval.aggregate(execAggregation);
		retval.aggregate(fetchAggregation);
		retval.aggregate(unmapAggregation);
		return retval;
	}

	private void aggregateOperation(final CursorOperation event) {
		final RecordType r = event.getRecordType();
		switch (r) {
		case EXEC:
			if (execAggregation == null) {
				execAggregation = new CursorOperationAggregation();
			}
			execAggregation.aggregate(event);
			break;
		case PARSE:
			if (parseAggregation == null) {
				parseAggregation = new CursorOperationAggregation();
			}
			parseAggregation.aggregate(event);
			break;
		case FETCH:
			if (fetchAggregation == null) {
				fetchAggregation = new CursorOperationAggregation();
			}
			fetchAggregation.aggregate(event);
			break;
		case UNMAP:
			if (unmapAggregation == null) {
				unmapAggregation = new CursorOperationAggregation();
			}
			unmapAggregation.aggregate(event);
			break;
		default:
			throw new IllegalStateException("unmapped operation");
		}
	}

	private void aggregateWait(final Wait event) {
		// todo implement
	}

	private void appendAggregation(final StringBuilder b, final CursorOperationAggregation o) {
		if (o != null) {
			b.append(o.toString());
			b.append("\n");
		}
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public void aggregate(Cursor cursor) {

		if (execAggregation == null) {
			execAggregation = new CursorOperationAggregation();
		}
		execAggregation.aggregate(cursor.getExecAggregation());

		if (fetchAggregation == null) {
			fetchAggregation = new CursorOperationAggregation();
		}

		fetchAggregation.aggregate(cursor.getFetchAggregation());

		// agg = cursor.getWaitAggregations();
	}

	public StatAggregations getStatsById() {
		return statsById;
	}

}
