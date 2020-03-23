package org.javautil.core.csv.jackson;

import java.util.ArrayList;
import java.util.List;

import org.javautil.core.json.JsonSerializerJackson;
import org.javautil.dataset.ColumnMetadata;

public class SqlCsvExporterJsonParser {

	private SqlCsvExporterJson bean;

	public SqlCsvExporterJsonParser(String json) {
		JsonSerializerJackson dillon = new JsonSerializerJackson();
		bean = (SqlCsvExporterJson) dillon.toObjectFromJson(json, SqlCsvExporterJson.class);
	}

	public List<ColumnMetadata> getColumnMetadata() {
		ArrayList<ColumnMetadata> list = new ArrayList<>();
		for (ColumnMetum colser : bean.getColumnMeta()) {
			ColumnMetadata col = new ColumnMetadata();
			col.setColumnName(colser.getColumnName());
			col.setColumnDisplaySize(colser.getColumnDisplaySize());
			col.setColumnTypeName(colser.getColumnTypeName());
			col.setDataTypeName(colser.getDataType());
			// colser.getExternalFlag();
			col.setPrecision(colser.getPrecision());
			col.setScale(colser.getScale());
			list.add(col);
		}
		return list;

	}

}
