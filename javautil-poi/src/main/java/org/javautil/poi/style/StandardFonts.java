package org.javautil.poi.style;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */
public class StandardFonts extends FontLibrary {

	private Boolean initialized = Boolean.FALSE;
	private final HSSFWorkbook workbook;

	private synchronized void createFonts() {

		addFont("White8Bold", createFont(workbook, 8, WHITE, true, UNDERLINE_NONE));
		addFont("White10", createFont(workbook, 10, WHITE, false, UNDERLINE_NONE));
		addFont("Black10", createFont(workbook, 10, BLACK, false, UNDERLINE_NONE));
		addFont("Black8Bold", createFont(workbook, 8, BLACK, true, UNDERLINE_NONE));
		addFont("Black10Bold", createFont(workbook, 10, BLACK, true, UNDERLINE_NONE));
		addFont("Black12Bold", createFont(workbook, 12, BLACK, true, UNDERLINE_NONE));

	}

	public StandardFonts(final HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	@Override
	public Map<String, HSSFFont> getFonts(final HSSFWorkbook workbook) {

		if (!initialized.booleanValue()) {

			createFonts();
			initialized = Boolean.TRUE;

		}
		return super.getFonts();
	}

	@Override
	public HSSFFont getFont(final String fontName) {

		if (!initialized.booleanValue()) {

			createFonts();
			initialized = Boolean.TRUE;

		}
		return super.getFont(fontName);
	}
}
