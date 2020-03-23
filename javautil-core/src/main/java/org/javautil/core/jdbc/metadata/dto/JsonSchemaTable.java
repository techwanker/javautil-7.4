package org.javautil.core.jdbc.metadata.dto;

import java.util.ArrayList;
import java.util.List;

import org.javautil.core.jdbc.metadata.Column;
import org.javautil.core.jdbc.metadata.Table;
import org.javautil.core.json.JsonSerializerJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSchemaTable {

	private String                 tableName;

	private String                 remarks;
	private List<JsonSchemaColumn> columns = new ArrayList<>();

	transient private final Logger logger  = LoggerFactory.getLogger(this.getClass().getName());

	public JsonSchemaTable(Table table) {
		tableName = table.getTableName();
		remarks = table.getRemarks();
		logger.info("table {}", tableName);
		for (Column col : table.getColumns()) {
			logger.info("adding column {} to table {}", tableName, col.getColumnName());
			columns.add(new JsonSchemaColumn(col));
		}
		logger.info(toJson());
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	public List<JsonSchemaColumn> getColumns() {
		return columns;
	}

	public String toJson() {
		JsonSerializerJackson dillon = new JsonSerializerJackson();
		return dillon.toJsonPretty(this);
	}

}