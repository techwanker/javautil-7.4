package org.javautil.core.jdbc.metadata.containers;

import java.util.Map;

import org.javautil.core.jdbc.metadata.Table;

public interface DatabaseTables extends Map<String, Table> {

	public abstract void addTable(final Table table);

	@Override
	public abstract String toString();

}