package org.javautil.poi.workbook;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.javautil.poi.style.AbstractWorkbookStyle;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */

public class StandardWorkbookStyle extends AbstractWorkbookStyle implements WorkbookStyle {

	public StandardWorkbookStyle(final HSSFWorkbook workbook) {
		if (workbook == null) {
			throw new IllegalArgumentException("workbook is null");
		}
		setWorkbook(workbook);
		createFonts();
		setStyles();
	}

	HSSFCellStyle getHeaderStyle(HorizontalAlignment alignment) {
		HSSFCellStyle style;
		style = getWorkbook().createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(alignment);
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
		style.setFont(getHdrFont());
		return style;
	}

	HSSFCellStyle getDataStyle(HorizontalAlignment alignment) {
		final HSSFCellStyle style = getWorkbook().createCellStyle();
		style.setAlignment(alignment);
		style.setFont(getDataFont());

		return style;
	}

	// Initialization
	private void setStyles() {
		//
		// Header Styles
		//
		setLeftHeaderString(getHeaderStyle(HorizontalAlignment.LEFT));
		setCenterHeader(getHeaderStyle(HorizontalAlignment.CENTER));
		setRightHeaderString(getHeaderStyle(HorizontalAlignment.RIGHT));

		//
		// Data Styles
		//
		setLeftDataString(getDataStyle(HorizontalAlignment.LEFT));
		setCenterData(getDataStyle(HorizontalAlignment.CENTER));
		setRightDataNumber(getDataStyle(HorizontalAlignment.RIGHT));

		final HSSFCellStyle style = getDataStyle(HorizontalAlignment.LEFT);
		style.setFont(super.getBigBoldFont());
		super.setLeftBigBold(style);

		final HSSFCellStyle style2 = getDataStyle(HorizontalAlignment.LEFT);
		style2.setFont(super.getBigFont());
		super.setLeftBig(style2);

		// Workbook workbook = new Workbook();
		// HSSFDataFormat format = new HSSFDataFormat(workbook);
		//
		// format.setFormat("[$$-409]#,##0.00;-[$$-409]#,##0.00");

	}

	private void createFonts() {
		HSSFFont font;

		font = getWorkbook().createFont();
		font.setFontHeightInPoints((short) 8);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		// font.setColor(HSSFColor.WHITE.index);

		font.setBold(true);
		// font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		setHdrFont(font);

		font = getWorkbook().createFont();
		font.setFontHeightInPoints((short) 8);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		// font.setColor(HSSFColor.BLACK.index);
		setDataFont(font);

		font = getWorkbook().createFont();
		font.setFontHeightInPoints((short) 10);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		// font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		// font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		setBigFont(font);

		font = getWorkbook().createFont();
		font.setFontHeightInPoints((short) 12);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		// font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		setBigBoldFont(font);

	}

}
