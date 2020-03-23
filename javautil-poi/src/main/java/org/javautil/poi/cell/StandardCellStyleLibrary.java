package org.javautil.poi.cell;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.javautil.poi.style.CellStyleLibrary;
import org.javautil.poi.style.FontLibrary;
import org.javautil.poi.style.StandardFonts;
import org.javautil.poi.style.StyleLibrary;

/**
 * 
 * @author jjs
 * 
 */

public class StandardCellStyleLibrary extends StyleLibrary implements CellStyleLibrary {

	private final StandardFonts fonts;
	private final HSSFWorkbook workbook;

	public StandardCellStyleLibrary(final HSSFWorkbook workbook) {
		if (workbook == null) {
			throw new IllegalArgumentException("workbook is null");
		}
		this.workbook = workbook;
		fonts = new StandardFonts(workbook);

	}

	/**
	 */
	public FontLibrary getFonts() {

		return fonts;
	}

	@Override
	public HSSFFont getFont(final String fontName) {

		return fonts.getFont(fontName);
	}

	public HSSFWorkbook getWorkbook() {
		// TODO Auto-generated method stub
		return workbook;
	}

}
