package org.javautil.oracle.trace.record;

public abstract class AbstractCursorTraceEvent extends AbstractRecord implements CursorRecord {

	private int cursorNumber;

	private String sqlid;

	private Integer cursorId;

	@Override
	public final int getCursorNumber() {
		return cursorNumber;
	}

	@Override
	public final void setCursorNumber(final int cursorNumber) {
		this.cursorNumber = cursorNumber;
	}

	@Override
	public String getSqlid() {
		return sqlid;
	}

	@Override
	public void setSqlid(String sqlid) {
		this.sqlid = sqlid;
	}

	@Override
	public Integer getCursorId() {
		return cursorId;
	}

	public void setCursorId(Integer cursorId) {
		this.cursorId = cursorId;
	}

}
