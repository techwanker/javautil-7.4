package com.pacificdataservices.pdssr;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class CdsReportingFileMetadata {

	private static Logger logger = LoggerFactory.getLogger(CdsReportingFileMetadata.class);

	@SuppressWarnings("unchecked")
	public static Map<String, ListOfNameValue> getRecordDefs() {
		Yaml yaml = new Yaml();
		InputStream is = CdsReportingFileMetadata.class.getResourceAsStream("cds_reporting_metadata.yaml");
		Map<String, List<Map<String, Object>>> recordDefs = (Map<String, List<Map<String, Object>>>) yaml.load(is);

		Map<String, ListOfNameValue> recordDefinitionByName = new HashMap<>();
		for (Entry<String, List<Map<String, Object>>> e : recordDefs.entrySet()) {
			ListOfNameValue recordFields = new ListOfNameValue();
			List<Map<String, Object>> recordDef = e.getValue();
			for (Map<String, Object> f : recordDef) {
				NameValue nv = new NameValue(f);
				recordFields.add(nv);
			}
			logger.debug("recordFields:\n{}", recordFields);
			recordDefinitionByName.put(e.getKey(), recordFields);
		}
		try {
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger.info("loaded definitions {} ", recordDefs);
		return recordDefinitionByName;
	}
}
