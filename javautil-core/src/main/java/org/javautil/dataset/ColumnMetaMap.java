package org.javautil.dataset;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnMetaMap {

	final static Logger logger = LoggerFactory.getLogger(ColumnMetaMap.class);

	public static Map<String, Integer> getColumnNameIndexMap(final List<ColumnMetadata> columns) {
		LinkedHashMap<String, Integer> columnNameIndexMap = new LinkedHashMap<String, Integer>();

		columnNameIndexMap = new LinkedHashMap<String, Integer>();
		int indexNbr = 0;
		for (final ColumnMetadata col : columns) {
			if (col == null) {

				logger.warn("null column found at index " + indexNbr);
			} else {
				columnNameIndexMap.put(col.getColumnName(), indexNbr++);
			}
		}

		return columnNameIndexMap;
	}
}
