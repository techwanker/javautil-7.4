/**
 * @(#) Stat.java
 */

package org.javautil.oracle.trace.record;

import java.util.regex.Pattern;

import org.javautil.oracle.trace.RegularExpressionHelper;
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
public class Stat extends AbstractCursorTraceEvent {
	// private StringTokenizer tokenizer;
	private static Logger logger = LoggerFactory.getLogger(Stat.class.getName());
	private static Pattern cursorNumberPattern = Pattern.compile("^STAT #(\\d*) ");
	// TODO isn't this redundant?
	private static Pattern idPattern = Pattern.compile("id=(\\d*)");
	private static Pattern cntPattern = Pattern.compile("cnt=(\\d*)");
	private static Pattern pidPattern = Pattern.compile("pid=(\\d*)");
	private static Pattern posPattern = Pattern.compile("pos=(\\d*).*");
	private static Pattern objPattern = Pattern.compile("obj=(\\d*)");
	private static Pattern opPattern = Pattern.compile("op='(.*) \\(");
	private static Pattern crPattern = Pattern.compile("\\(cr=(\\d*)");
	private static Pattern prPattern = Pattern.compile("pr=(\\d*)");
	private static Pattern pwPattern = Pattern.compile("pw=(\\d*)");
	private static Pattern timePattern = Pattern.compile("time='(\\d*)");
	/**
	 * Index starting with 1.
	 */
	private final int id;
	private final int cnt;
	/**
	 * Id of the parent Stat Record
	 */
	private final int pid;
	private final int pos;
	private final int obj;
	private final String operation;
	private final int consistentReads;
	private final int physicalReads;
	private final int physicalWrites;
	private final int time;
	/**
	 * How deep this is. If you follow the linked list
	 */
	private int depth;

	// public static Pattern getCntPattern() {
	// return cntPattern;
	// }
	//
	// public static Pattern getCrPattern() {
	// return crPattern;
	// }
	//
	// public static Pattern getIdPattern() {
	// return idPattern;
	// }
	//
	// public static Pattern getObjPattern() {
	// return objPattern;
	// }
	//
	// public static Pattern getOpPattern() {
	// return opPattern;
	// }
	//
	// public static Pattern getPidPattern() {
	// return pidPattern;
	// }
	//
	// public static Pattern getPosPattern() {
	// return posPattern;
	// }
	//
	// public static Pattern getPrPattern() {
	// return prPattern;
	// }
	//
	// public static Pattern getPwPattern() {
	// return pwPattern;
	// }
	//
	// public static Pattern getTimePattern() {
	// return timePattern;
	// }

	public Stat(final String stmt, final int lineNumber) {
		// recordText = stmt;
		setLineNumber(lineNumber);
		id = RegularExpressionHelper.getInt(stmt, idPattern);
		cnt = RegularExpressionHelper.getInt(stmt, cntPattern);
		pid = RegularExpressionHelper.getInt(stmt, pidPattern);
		pos = RegularExpressionHelper.getInt(stmt, posPattern);
		obj = RegularExpressionHelper.getInt(stmt, objPattern);
		operation = RegularExpressionHelper.getString(stmt, opPattern);
		consistentReads = RegularExpressionHelper.getInt(stmt, crPattern);
		physicalReads = RegularExpressionHelper.getInt(stmt, prPattern);
		physicalWrites = RegularExpressionHelper.getInt(stmt, pwPattern);
		time = RegularExpressionHelper.getInt(stmt, timePattern);
		final int cursorNumber = RegularExpressionHelper.getInt(stmt, cursorNumberPattern);
		setCursorNumber(cursorNumber);
	}

	public int getCnt() {
		return cnt;
	}

	public int getConsistentReads() {
		return consistentReads;
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
		return operation;
	}

	/**
	 * 
	 * @return the number of blocks read via operating system calls.
	 */
	public int getPhysicalReads() {
		return physicalReads;
	}

	public int getPhysicalWrites() {
		return physicalWrites;
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
	public RecordType getRecordType() {
		return RecordType.STAT;
	}

	public int getTime() {
		return time;
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
		builder.append(operation);
		builder.append(", cr=");
		builder.append(consistentReads);
		builder.append(", pr=");
		builder.append(physicalReads);
		builder.append(", pw=");
		builder.append(physicalWrites);
		builder.append(", time=");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}

	public int getObj() {
		return obj;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getParentId() {
		return pid;
	}

	// public StringTokenizer getTokenizer() {
	// return tokenizer;
	// }

}
