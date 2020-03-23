package org.javautil.dataset;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.javautil.core.collections.CollectionsUtil;
import org.javautil.core.sql.Dialect;
import org.javautil.core.text.StringUtils;

public class DatasetToRdbmsTable {

	

	public static String toPaddedTable(String tableName, Dataset dataset) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ");
		sb.append(tableName);
		sb.append("(\n");
		ArrayList<String> coldefs = new ArrayList<String>();
		final int colsPerRow = 4;
		int colsThisRow = 0;
		int commaCount = dataset.getMetadata().getColumnCount() - 1;
		for (ColumnMetadata col : dataset.getMetadata().getColumnMetadata()) {
			String coldef = String.format("%s %s", col.getColumnName(), col.getDataTypeDeclaration(Dialect.POSTGRES));
			Boolean nullable = col.isNullable();
			if (nullable != null && ! nullable) {
				coldef = coldef + " not null";
			}
			coldefs.add(coldef);
		}
		int maxColDefLength = CollectionsUtil.maxStringLength(coldefs);
		for (String coldef : coldefs) {
			sb.append(coldef);
			if (commaCount-- > 0) {
				sb.append(", ");
				int padLength = maxColDefLength - coldef.length();
				sb.append(StringUtils.rightPad("",padLength));
			}
			colsThisRow++;
			if (colsThisRow >= colsPerRow) {
				sb.append("\n");
				colsThisRow = 0;
			}
		}
		sb.append(")\n");
		String retval = sb.toString();
		
		return retval;
	}


	public void populate(Connection conn, String tableName, Dataset dataset) throws SQLException {
		String DDL = toPaddedTable(tableName,dataset);
		Statement stmt = conn.createStatement();
		stmt.execute(DDL);
		// TODO iterate and insert

	}
}
