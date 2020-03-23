package org.javautil.core.jdbc.metdata.renderer.jsonschema;

import org.javautil.core.jdbc.metadata.ForeignKeys;
import org.javautil.core.jdbc.metadata.Table;

public class TableDTO {

	private String      tableName;
	private String      tableType;
	private String      remarks;
	private ForeignKeys exportedKeys;
	private ForeignKeys importedKeys;

	private TableDTO(Table table) {
		this.tableName = table.getTableName();
		this.tableType = table.getTableType();
		this.remarks = table.getRemarks();
		this.exportedKeys = table.getExportedKeys();
		this.importedKeys = table.getImportedKeys();
	}

}
