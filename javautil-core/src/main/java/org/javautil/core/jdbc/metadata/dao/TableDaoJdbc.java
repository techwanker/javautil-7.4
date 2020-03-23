package org.javautil.core.jdbc.metadata.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.javautil.core.jdbc.metadata.NonexistantTableException;
import org.javautil.core.jdbc.metadata.Table;
import org.javautil.core.jdbc.metadata.containers.DatabaseTables;
import org.javautil.core.jdbc.metadata.containers.DatabaseTablesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 */
public class TableDaoJdbc {
	private static Logger        logger        = LoggerFactory.getLogger(TableDaoJdbc.class.getName());
	private final DatabaseTables tables        = new DatabaseTablesImpl();
	private String               schemaName;
	private DatabaseMetaData     meta;
	private Connection           conn;
	private String               catalog;
	private String               tablePattern  = ".*";
	private boolean              processCalled = false;

	public TableDaoJdbc() {

	}

	public TableDaoJdbc(final Connection conn, String catalog, final String schema, final String tablePattern)
	    throws SQLException {
		this(conn, conn.getMetaData(), schema, catalog, tablePattern);
	}

	/**
	 * TODO why connection and metadata, faster just to pass metadata
	 * 
	 * @param conn         The connection
	 * @param meta         The database metadata
	 * @param schema       The schema name
	 * @param catalog      The catalog
	 * @param tablePattern The table pattern
	 * @throws SQLException Perhaps
	 */
	public TableDaoJdbc(final Connection conn, final DatabaseMetaData meta, final String schema, final String catalog,
	    final String tablePattern) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		if (meta == null) {
			throw new IllegalArgumentException("meta is null");
		}
		if (schema == null) {
			throw new IllegalArgumentException("schema is null");
		}
		if (tablePattern == null) {
			throw new IllegalArgumentException("tablePattern is null");
		}
		this.conn = conn;
		this.schemaName = schema;
		this.catalog = catalog;
		this.meta = meta;
		this.tablePattern = tablePattern;

	}

	public DatabaseTables getDatabaseTables() throws SQLException {
		if (!processCalled) {
			throw new IllegalStateException("process() has not been called");
		}
		return tables;
	}

	public void process() throws SQLException {
		String username = meta.getUserName();
		boolean tableFound = false;
		final String[] tableTypes = new String[] { "TABLE", "VIEW" };
		logger.info("catalog: " + catalog + " schema " + schemaName);
		String message = DatabaseMetadataHelper.toString(conn);
		logger.info(message);
		final ResultSet rs = meta.getTables(catalog, schemaName, tablePattern, tableTypes);

		while (rs.next()) {
			tableFound = true;
			final String tableName = rs.getString("table_name");
			final String tableType = rs.getString("table_type");
			logger.info("processing schema " + schemaName + "." + tableName);
			addTable(getTable(meta, conn, schemaName, catalog, tableName, tableType));
		}
		rs.close();
		if (!tableFound) {
			logger.warn("no tables available to {} for  catalog {}  schema {}  tablePattern {}", username, catalog,
			    schemaName, tablePattern);
		}
		rs.close();
		processCalled = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dbexperts.jdbc.DatabaseMetaData.DatabaseTables#addTable(com.dbexperts
	 * .jdbc.DatabaseMetaData.Table)
	 */
	public void addTable(final Table table) {
		tables.put(table.getTableName(), table);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dbexperts.jdbc.DatabaseMetaData.DatabaseTables#getSchemaName()
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/*
	 * Nasty hack for the fact that there appears to be no way to close the cursors
	 * that gave rise to the MetaData ResultSets
	 */
	// private DatabaseMetaData getMetaData() throws SQLException {
	// if (meta == null) {
	// conn = ds.getConnection();
	// meta = conn.getMetaData();
	// }
	// if (metaUseCount > maxMetaUseCount) {
	// conn.close();
	// conn = ds.getConnection();
	// meta = conn.getMetaData();
	// metaUseCount = 1;
	// } else {
	// metaUseCount++;
	// }
	// // logger.info("metaUseCount " + metaUseCount);
	// return meta;
	// }
	public TableJdbc getTable(final DatabaseMetaData meta, final Connection conn, final String schema,
	    final String catalog, final String tableName, final String tableType) throws SQLException {
		TableJdbc table = null;
		try {
			if (conn == null) {
				throw new IllegalArgumentException("conn is null");
			}
			if (tableName.startsWith("BIN$")) {
				logger.info("discarding " + tableName);
			} else {
				logger.info(tableType + " " + tableName);
				table = new TableJdbc(schema, catalog, tableName, "" /* remarks */, tableType);
				table.init(meta, conn);
				return table;
			}
		} catch (final NonexistantTableException n) {
			logger.warn(n.getMessage());
		}
		return table;
	}

}
