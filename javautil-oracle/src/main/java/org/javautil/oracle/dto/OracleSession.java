package org.javautil.oracle.dto;

import org.javautil.oracle.dao.OpenCursorS;

import oracle.jdbc.OracleConnection;

public class OracleSession extends OracleSessionBase {
	OpenCursor[] openCursors = null;

	OpenCursor[] getOpenCursor() {
		return openCursors;
	}

	OpenCursor[] getOpenCursor(final OracleConnection dbc) throws java.sql.SQLException {
		final OpenCursorS cursors = new OpenCursorS();
		return cursors.getForSession(dbc, this.getSid());
	}

	public void setOpenCursors(final OpenCursor[] cursors) {
		this.openCursors = cursors;
	}
}
