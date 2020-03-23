package com.pacificdataservices.pdssr.workbook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.javautil.core.workbook.WorkbookDefinition;
import org.javautil.document.renderer.WorkbookRenderer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class LoadFileWorkbook {

	private WorkbookDefinition definition;
	private Connection conn;

	public LoadFileWorkbook(Connection conn) throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.conn = conn;
		init();
	}

	void init() throws JsonParseException, JsonMappingException, IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String definitionPath = "src/main/resources/workbooks/ReportingFileWorkbook.yaml";
		definition = WorkbookDefinition.getWorkbookDefinition(new File(definitionPath));
	}

	public void process() {
		WorkbookRenderer render = new WorkbookRenderer() {

			@Override
			public void setRowHeight(int rowIndex, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setColumnWidth(int columnIndex, int width) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mergeCellRange(int rowIndexStart, int rowIndexEnd, int columnIndexStart, int columnIndexEnd) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getCellRange(int rowIndexStart, int rowIndexEnd, int columnIndexStart, int columnIndexEnd) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getCellAddress(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addSheet(String sheetName) {
				// TODO Auto-generated method stub

			}
		};
	}
}
