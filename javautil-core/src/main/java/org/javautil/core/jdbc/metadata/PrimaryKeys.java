package org.javautil.core.jdbc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO delete only needed by JDBCTables
 * 
 * @author jim
 */
public class PrimaryKeys {
	static Logger logger = LoggerFactory.getLogger(PrimaryKeys.class.getName());

	public static PrimaryKey getPrimaryKey(final DatabaseMetaData meta, final String catalog, final String schemaPattern,
	    final String tableName) throws SQLException {
		PrimaryKey pk = null;
		logger.debug("getting pk catalog {} schemaPattern {} tableName {}", catalog, schemaPattern, tableName);
		final ResultSet rs = meta.getPrimaryKeys(catalog, schemaPattern, tableName);
		if (rs.next()) {
			final String schema = rs.getString("table_schem");
			final String pkName = rs.getString("pk_name");
			pk = new PrimaryKey(schema, tableName, pkName);
			short keySeq = rs.getShort("key_seq");
			String columnName = rs.getString("column_name");
			pk.addColumn(columnName, keySeq);
			while (rs.next()) {
				keySeq = rs.getShort("key_seq");
				columnName = rs.getString("column_name");
				pk.addColumn(columnName, keySeq);
			}
		} else {
			logger.warn("No Pk for " + catalog + " " + schemaPattern + " " + tableName);
		}

		rs.close();

		if (pk == null) {
			logger.info(" Index infos not found");
		}
		return pk;
	}

}
