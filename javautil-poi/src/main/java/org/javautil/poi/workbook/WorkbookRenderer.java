package org.javautil.poi.workbook;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.ResultSetMetaDataCache;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.text.StringUtils;
import org.javautil.core.workbook.WorkbookDefinition;
import org.javautil.core.workbook.Worksheet;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetMetadataImpl;
import org.javautil.dataset.ListOfNameValueDataset;
import org.javautil.dataset.MatrixDataset;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.document.style.StyleFactory;
import org.javautil.core.misc.Timer;
import org.javautil.poi.sheet.ListWorksheetRenderer;
import org.javautil.poi.style.HSSFCellStyleFactory;
import org.javautil.util.ListOfNameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookRenderer {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private Connection conn;
	private WorkbookDefinition workbookDefinition;
	private Binds binds;
	private StyleFactory styleFactory;
	private HSSFWorkbook workbook;
	private HSSFCellStyleFactory cellStyleFactory;
	
	public WorkbookRenderer() {
		workbook = new HSSFWorkbook();
		cellStyleFactory = new HSSFCellStyleFactory(workbook);
	}

	public WorkbookRenderer(Connection conn, WorkbookDefinition wd, Binds binds) {
		this.conn = conn;
		this.workbookDefinition = wd;
		this.binds = binds;
		workbook = new HSSFWorkbook();
		cellStyleFactory = new HSSFCellStyleFactory(workbook);
	}

	public void process() throws SQLException {
		int sheetIndex = 0;
		for (Worksheet worksheet : workbookDefinition.getWorksheets().values()) {
			Timer sqlTime = new Timer();
			// create the sheet
			logger.info("processing {}", worksheet);
			HSSFSheet sheet = workbook.createSheet(worksheet.getName());
			ListWorksheetRenderer sheetRenderer = new ListWorksheetRenderer(sheet, cellStyleFactory);
			// get the data
			String sql = worksheet.getSql();
			SqlStatement ss = new SqlStatement(conn, sql);
			ListOfNameValueDataset dataset = ss.getListOfNameValueDataSet(binds,workbookDefinition.getColumns());
			sheetRenderer.emitRegion(dataset);
			sheetIndex++;
		}
	}
	
	public void emitDataSet(String sheetName, MatrixDataset dataset) {
		HSSFSheet sheet = workbook.createSheet(sheetName);
		ListWorksheetRenderer sheetRenderer = new ListWorksheetRenderer(sheet, cellStyleFactory);
		sheetRenderer.emitRegion(dataset);
	}
	
	
	public WorkbookDefinition getWorkbookDefinition() {
		return workbookDefinition;
	}

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	
	
	
	public HSSFCellStyleFactory getCellStyleFactory() {
		return cellStyleFactory;
	}

	public void write(File file) throws IOException {
		workbook.write(file);
	}

	public Connection getConnection() {
		return conn; 
	}

	public Binds getBinds() {
		return binds;
	}
}
