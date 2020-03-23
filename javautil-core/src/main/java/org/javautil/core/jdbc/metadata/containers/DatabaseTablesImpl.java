package org.javautil.core.jdbc.metadata.containers;

import java.util.TreeMap;

import org.javautil.core.jdbc.metadata.Table;

public class DatabaseTablesImpl extends TreeMap<String, Table> implements DatabaseTables {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void addTable(final Table table) {
		put(table.getTableName(), table);
	}

}