/**
 * @(#) Stat.java
 */

package org.javautil.oracle.trace.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * STAT #<CURSOR> id=N cnt=0 [pid=0 pos=0 obj=0 op='SORT AGGREGATE ']
 * ----------------------------------------------------------------------------
 * 
 * STAT Lines report explain plan statistics for the numbered <CURSOR>.
 * 
 * <CURSOR> Cursor which the statistics apply to.
 * 
 * id Line of the explain plan which the row count applies to (starts at line
 * 1). This is effectively the row source row count for all row sources in the
 * execution tree. cnt Number of rows for this row source.
 * 
 * As of 7.3.3 the items in '[...]' are also reported:
 * 
 * pid Parent id of this row source. pos Position in explain plan. obj Object id
 * of row source (if this is a base object). op='...' The row source access
 * operation.
 * 
 * In reality we find this STAT #3 id=1 cnt=188220 pid=0 pos=1 obj=77538
 * op='INDEX FAST FULL SCAN FC_FCST_NDX_02 (cr=2366 pr=0 pw=0 time=1129914 us)'
 */
public class StatAggregation {

	private static Logger logger = LoggerFactory.getLogger(Stat.class.getName());

	private final int id;
	private int cnt;
	private final int pid;
	private final int pos;
	private final int obj;
	private final String op;
	private int cr;
	private int pr;
	private int pw;
	private int depth;

	public StatAggregation(Stat stat) {
		this.id = stat.getId();
		this.pid = stat.getParentId();
		this.pos = stat.getPosition();
		this.obj = stat.getObj();
		this.op = stat.getOperation();
	}

	public void aggregate(Stat stat) {
		cr += stat.getConsistentReads();
		pr += stat.getPhysicalReads();
		pw += stat.getPhysicalWrites();
		// cnt += stat.getCnt();
	}

	public int getCnt() {
		return cnt;
	}

	public int getCr() {
		return cr;
	}

	public int getId() {
		return id;
	}


	/**
	 * @return the database object number
	 */
	public int getObjectNumber() {
		return obj;
	}

	/**
	 * The op value in the trace log. Describes the operation.
	 * 
	 * @return the operation description.
	 */
	public String getOperation() {
		return op;
	}

	/**
	 * 
	 * @return the number of blocks read via operating system calls.
	 */
	public int getPhysicalReads() {
		return pr;
	}

	public int getPhysicalWrites() {
		return pw;
	}

	public int getPid() {
		return pid;
	}

	/**
	 * The position in the explain plan.
	 * 
	 * @return
	 */
	public int getPosition() {
		return pos;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stat [id=");
		builder.append(id);
		builder.append(", cnt=");
		builder.append(cnt);
		builder.append(", pid=");
		builder.append(pid);
		builder.append(", pos=");
		builder.append(pos);
		builder.append(", obj=");
		builder.append(obj);
		builder.append(", op=");
		builder.append(op);
		builder.append(", cr=");
		builder.append(cr);
		builder.append(", pr=");
		builder.append(pr);
		builder.append(", pw=");
		builder.append(pw);
		builder.append("]");
		return builder.toString();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	// public StringTokenizer getTokenizer() {
	// return tokenizer;
	// }

}
