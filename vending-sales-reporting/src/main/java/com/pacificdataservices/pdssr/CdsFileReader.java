package com.pacificdataservices.pdssr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javautil.core.sql.Binds;
import org.javautil.util.ListOfNameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CdsFileReader {
	private Logger logger = LoggerFactory.getLogger(getClass());
	static final String NUMERIC = "NUMERIC";
	static final String INTEGER = "INTEGER";
	static final String DECIMAL = "DECIMAL";
	static final String DATE = "DATE";
	static final String TEXT = "TEXT";
	static final String LITERAL = "LITERAL";
	static final String DIGITS = "DIGITS";

	private String filename;

	// private HashMap<String, ListOfNameValue> recordDefinitions;
	private HashMap<String, ListOfNameValue> recordDefinitionByRecordType;
	private BufferedReader reader;
	// TODO load from resource
	// final String fileName =
	// "src/main/resources/com/pacificdataservices/pdssr/cds_reporting_metadata.yaml";
	FixedRecordUtil fixedRecordUtil = new FixedRecordUtil();

	private String inputLine;
	private String recordType;
	private ListOfNameValue recordDefinition;

	private int lineNumber = 0;

	CdsFileReader() throws IOException {
		loadRecordDefinitions();
	}

	public CdsFileReader(String filename) {
		this.filename = filename;

		FileReader fr;
		try {
			fr = new FileReader(this.filename);
			reader = new BufferedReader(fr);
			loadRecordDefinitions();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	void loadRecordDefinitions() throws IOException {
		new CdsReportingFileMetadata();
		Map<String, ListOfNameValue> recordDefs = CdsReportingFileMetadata.getRecordDefs();
		recordDefinitionByRecordType = new HashMap<String, ListOfNameValue>();
		recordDefinitionByRecordType.put("CD", recordDefs.get("customer"));
		recordDefinitionByRecordType.put("CT", recordDefs.get("customer_total"));
		recordDefinitionByRecordType.put("IR", recordDefs.get("inventory"));
		recordDefinitionByRecordType.put("IT", recordDefs.get("inventory_total"));
		recordDefinitionByRecordType.put("SA", recordDefs.get("sales"));
		recordDefinitionByRecordType.put("AT", recordDefs.get("sales_total"));
		logger.info(getRecordDefinitionsText());
	}

	public String getRecordDefinitionsText() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, ListOfNameValue> e : recordDefinitionByRecordType.entrySet()) {
			sb.append("type: ");
			sb.append(e.getKey());
			sb.append("\n");
			sb.append(e.getValue().toString());
		}
		return sb.toString();
	}

	String inputLine() throws IOException {
		do {
			inputLine = reader.readLine();
			lineNumber += 1;
		} while (inputLine != null && inputLine.startsWith("#"));
		if (inputLine != null) {
			recordType = inputLine.substring(168, 170); // line[168:170] TODO
			recordDefinition = recordDefinitionByRecordType.get(recordType);
			if (recordDefinition == null) {
				throw new IllegalStateException("unable to load recordDefinition for " + recordType);
			}
			if (recordDefinition.size() == 0) {
				throw new IllegalStateException("no fieldDefinitions in recordDefinition");
			}
		}
		return inputLine;
	}

	List<Object> readLineAsObjectList() throws ParseException, IOException {
		inputLine = inputLine();
		List<Object> retval = null;

		if (inputLine != null)
			retval = fixedRecordUtil.getObjectList(recordDefinition, inputLine);
		return retval;
	}

//	Binds readLine() throws ParseException, IOException {
//        inputLine = inputLine();
//        Map<String, Object> retval = null;
//        Binds binds = null;
//        if (inputLine != null) {
//          retval = fru.getBindMap(recordDefinition, inputLine);
//          binds = new Binds(retval);
//        }
//
//        return binds;
//    }

	Binds readLine() throws ParseException, IOException {
		inputLine = inputLine();
		Map<String, Object> retval = null;
		Binds binds = null;
		if (inputLine != null) {
			retval = fixedRecordUtil.getBinds(recordDefinition, inputLine);
			binds = new Binds(retval);
		}
		return binds;
	}

	void close() throws IOException {
		this.reader.close();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getInputLine() {
		return inputLine;
	}

	public String getRecordType() {
		return recordType;
	}

	/**
	 * @return the record_defs
	 */
	public HashMap<String, ListOfNameValue> getRecordDefinitionsByRecordType() {
		return recordDefinitionByRecordType;
	}

}
