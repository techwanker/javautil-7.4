package org.javautil.oracle.trace.record;

public interface CursorRecord extends Record {
	public int getCursorNumber();

	@Override
	public int getLineNumber();

	@Override
	public RecordType getRecordType();

	public void setCursorNumber(int cursorNumber);

	public String getSqlid();

	public void setSqlid(String sqlId);

	public Integer getCursorId();
}
