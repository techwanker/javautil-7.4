package org.javautil.core.workbook;

import org.javautil.core.json.JsonUtils;

import com.google.gson.Gson;

public class WorkbookYamlParserGson {

	private JsonUtils          jsonUtils = new JsonUtils();
//	private String yamlString;
//	
//	private Map<String,Object> yamlMap;
//	
//	private final Yaml yaml = new Yaml();

	private final Gson         gson      = new Gson();

	private WorkbookDefinition workbookDefinition;

	public WorkbookYamlParserGson() {
	}

	public WorkbookYamlParserGson(String yamlString) {
		String json = jsonUtils.yamlToJson(yamlString);
		workbookDefinition = gson.fromJson(json, WorkbookDefinition.class);
	}

	public WorkbookDefinition getWorkbookDefinition() {
		return workbookDefinition;
	}

}
