package org.javautil.poi.sheet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public abstract class AbstractWorkbookRenderer extends WorkbookRenderer implements WorksheetGenerator {

	public AbstractWorkbookRenderer(final HSSFWorkbook workbook) {
		super(workbook);
	}

}