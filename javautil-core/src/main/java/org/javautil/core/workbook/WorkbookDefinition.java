
package org.javautil.core.workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.javautil.core.text.YamlUtils;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkbookDefinition {

	@SerializedName("name")
	@Expose
	private String                                name;
	@SerializedName("description")
	@Expose
	private String                                description;
	@SerializedName("narrative")
	@Expose
	private String                                narrative;
	@SerializedName("connectionName")
	@Expose
	private String                                connectionName;
	private LinkedHashMap<String, ColumnMetadata> columns;
	@SerializedName("worksheets")
	@Expose
	private LinkedHashMap<String, Worksheet>      worksheets = null;

//	private Map<String, BindDefinition> bindDefinitions;
	private BindDefinitions                       bindDefinitions;
	private File                                  file;

//	@SerializedName("binds")
//	@Expose
//	private List<Bind> binds = null;

	@SuppressWarnings("unused")
	private transient static Logger               logger     = LoggerFactory.getLogger(WorkbookDefinition.class);

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public WorkbookDefinition() {
	}

	public WorkbookDefinition(String name, String description, String narrative, String connectionName,
	    LinkedHashMap<String, ColumnMetadata> columns, LinkedHashMap<String, Worksheet> worksheets,
	    BindDefinitions bindDefinitions) {
		super();
		this.name = name;
		this.description = description;
		this.narrative = narrative;
		this.connectionName = connectionName;
		this.columns = columns;
		this.worksheets = worksheets;
		this.bindDefinitions = bindDefinitions;
	}

	public static WorkbookDefinition getWorkbookDefinition(InputStream is) throws JsonParseException,
	    JsonMappingException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (is == null) {
			throw new IllegalArgumentException("is is null");
		}
		Yaml yaml = new Yaml();
		WorkbookDefinition retval = yaml.loadAs(is, WorkbookDefinition.class);
		retval.populateColumnNames();
		return retval;
	}

	public static WorkbookDefinition getWorkbookDefinition(File file) throws JsonParseException, JsonMappingException,
	    IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (file == null) {
			throw new IllegalArgumentException("file is null");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("can't read " + file.getAbsolutePath());
		}
		FileInputStream is = new FileInputStream(file.getAbsoluteFile());
		WorkbookDefinition retval = getWorkbookDefinition(is);
		retval.file = file;
		retval.populateColumnNames();
		return retval;
	}

	void populateColumnNames() {
		for (Entry<String, ColumnMetadata> c : columns.entrySet()) {
			String k = c.getKey();
			ColumnMetadata col = c.getValue();
			if (col.getColumnName() == null) {
				col.setColumnName(k);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WorkbookDefinition withName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WorkbookDefinition withDescription(String description) {
		this.description = description;
		return this;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public WorkbookDefinition withNarrative(String narrative) {
		this.narrative = narrative;
		return this;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public WorkbookDefinition withConnectionName(String connectionName) {
		this.connectionName = connectionName;
		return this;
	}

	public LinkedHashMap<String, Worksheet> getWorksheets() {
		return worksheets;
	}

	public void setWorksheets(LinkedHashMap<String, Worksheet> worksheets) {
		this.worksheets = worksheets;
	}

	public BindDefinitions getBindDefinitions() {
		return bindDefinitions;
	}

	public void setBindDefinitions(BindDefinitions bindDefinitions) {
		this.bindDefinitions = bindDefinitions;
	}

	public LinkedHashMap<String, ColumnMetadata> getColumns() {
		return columns;
	}

	public void setColumns(LinkedHashMap<String, ColumnMetadata> columns) {
		this.columns = columns;
	}

	public String getFileAsString() throws IOException {
		String filePath = file.getCanonicalPath();
		return FileUtil.getAsString(filePath);
	}

	public String toString() {
		return YamlUtils.asYaml(this);
	}
}
