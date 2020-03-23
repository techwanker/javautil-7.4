package org.javautil.poi.sheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.javautil.core.text.SimpleDateFormatFactory;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.javautil.document.style.FontAttributes;
import org.javautil.document.style.FontAttributesImpl;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.document.style.Style;
import org.javautil.document.style.StyleImpl;
import org.javautil.document.style.VerticalAlignment;
import org.javautil.poi.cell.CellAddress;
import org.javautil.poi.cell.CellFormula;
import org.javautil.poi.style.HSSFCellStyleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jjs
 */
public class BaseWorksheetRenderer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat dateFormat = SimpleDateFormatFactory.getYyyyDashMmDashDd();
	private HSSFSheet sheet;
	private HSSFWorkbook workbook;
	private HSSFCellStyleFactory styleFactory;
	private CreationHelper createHelper;

	public BaseWorksheetRenderer(final HSSFSheet sheet, final HSSFCellStyleFactory styleFactory) {
		super();
		if (sheet == null) {
			throw new IllegalArgumentException("sheet is null");
		}
		if (styleFactory == null) {
			throw new IllegalArgumentException("styleFactory is null");
		}
		this.sheet = sheet;
		this.styleFactory = styleFactory;
		this.workbook = sheet.getWorkbook();
		createHelper = workbook.getCreationHelper();
		defaultSheetFormat();
	}

	Style addWordWrap(final Style _style) {
		Style style = _style;
		if (style == null) {
			style = new StyleImpl();
		} else {
			style = style.copyOf();
		}
		if (style.getFontAttributes() == null) {
			style.setFontAttributes(new FontAttributesImpl());
		}
		style.getFontAttributes().put(FontAttributes.KEY_WORD_WRAP, "true");
		return style;
	}

	@Deprecated
	HSSFCell addCell(final int rowIndex, final int columnIndex, final Object value, Style style) {
		return addCell(rowIndex, columnIndex, value, style, (DataType) null);
	}

	@Deprecated
	HSSFCell addCell(final int rowIndex, final int columnIndex, final Object value, final Style _style,
			DataType dataType) {

		Style style = _style;
		final HSSFCell cell = getCell(rowIndex, columnIndex);
		if (value != null) {
			if (DataType.DATE.equals(dataType)) {
				if (value instanceof String) {
					Date d;
					try {
						d = dateFormat.parse((String) value);
					} catch (ParseException e) {
						throw new IllegalArgumentException("Expected date string ''YYY-MM-DD' not " + value);
					}
					cell.setCellValue(d);
				} else {
					throw new UnsupportedOperationException("invalid type");
				}
			}
			if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
			} else if (value instanceof String) {
				final String val = (String) value;
				if (val.indexOf('\n') > -1) {
					style = addWordWrap(style);
				}
				cell.setCellValue(new HSSFRichTextString(val));
			} else if (value instanceof Boolean) {
				final Boolean bool = (Boolean) value;
				cell.setCellValue(bool.booleanValue());
			}
		} else {
			cell.setCellValue("");
		}
		if (style != null) {
			cell.setCellStyle(styleFactory.getHSSFCellStyle(style));
		}
		return cell;
	}

	private void setDateInCell(HSSFCell cell, int row, int col, Object value, ColumnMetadata meta) {
		if (DataType.DATE.equals(DataType.DATE)) {
			if (value instanceof String) {
				String stringValue = (String) value;
				Date d;
				SimpleDateFormat sdf = meta.getInputDateFormat() == null ? dateFormat : meta.getInputDateFormat();
				try {
					d = sdf.parse(stringValue);
					cell.setCellValue(d);
				} catch (ParseException e) {
					String symbols = sdf.toPattern();
					String message = String.format(
							"Expected date format '%s' not consistent with '%s' for row %d col %d emitting as String",
							symbols, value, row, col);
					logger.warn(message);
					cell.setCellValue(stringValue);
					throw new IllegalArgumentException(message);
				}
			} else {
				throw new UnsupportedOperationException("invalid type source for date field must be String | Date");
			}
		}
	}

	HSSFCell addCell(final int rowIndex, final int columnIndex, final Object value, Style baseStyle,
			ColumnMetadata meta) {

		final HSSFCell cell = getCell(rowIndex, columnIndex);
		Style style = getStyle(baseStyle, meta.getExcelFormat(), meta.getHorizontalAlignment());

		DataType dataType = meta.getDataType();

		if (value != null && DataType.DATE.equals(dataType)) {
			setDateInCell(cell, rowIndex, columnIndex, value, meta);
		} else {
			if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
			} else if (value instanceof String) {
				final String val = (String) value;
				if (val.indexOf('\n') > -1) {
					style = addWordWrap(style);
				}
				cell.setCellValue(new HSSFRichTextString(val));
			} else if (value instanceof Boolean) {
				final Boolean bool = (Boolean) value;
				cell.setCellValue(bool.booleanValue());
			}
		}
		if (style != null) {
			cell.setCellStyle(styleFactory.getHSSFCellStyle(style));
		}
		if (meta.getExcelFormat() != null) {
			formatCell(cell, meta.getExcelFormat());
		}
		return cell;
	}

	void formatCell(HSSFCell cell, String format) {
		if (cell == null) {
			throw new IllegalArgumentException("cell is null");
		}
		if (format == null) {
			throw new IllegalArgumentException("format is null");
		}
		if (createHelper == null) {
			throw new IllegalStateException("createHelper is null");
		}
		CellStyle style = cell.getCellStyle();
		DataFormat dataFormat = createHelper.createDataFormat();
		style.setDataFormat(dataFormat.getFormat(format));
	}

	void formatCell(HSSFCell cell, ColumnMetadata meta) {
		if (cell == null) {
			throw new IllegalArgumentException("cell is null");
		}
		if (meta == null) {
			throw new IllegalArgumentException("meta is null");
		}
		CellStyle style = cell.getCellStyle();
		String format = meta.getExcelFormat();
		if (format != null) {
			style.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		}
	}

	void addColumnSumCell(final int row, final int columnIndex, final int firstDataRowIndex, final int lastDataRowIndex,
			final Style style) {
		addColumnFormulaCell(row, columnIndex, firstDataRowIndex, lastDataRowIndex, style, "sum");

	}

	HSSFCell getCell(final int rownum, final int colnum) {
		final HSSFRow row = getRow(rownum);
		HSSFCell cell = row.getCell(colnum);
		if (cell == null) {
			cell = row.createCell(colnum);
		}
		return cell;
	}

	HSSFCell addFormulaCell(final int rownum, final int columnIndex, final String formula, final Style style) {
		if (formula == null) {
			throw new IllegalArgumentException("formula is null");
		}

		final HSSFCell cell = getCell(rownum, columnIndex);
		cell.setCellFormula(formula);
		final HSSFCellStyle hstyle = styleFactory.getHSSFCellStyle(style);
		cell.setCellStyle(hstyle);
		return cell;
	}

	/**
	 * Creates a formula cell.
	 * 
	 * Example: to create a sum cell on
	 * 
	 * 
	 * @param rowIndex          The index (rownumber - 1) of the row into which the
	 *                          formula should be placed
	 * @param columnIndex       The index of the column into which the formula
	 *                          should be placed
	 * @param firstDataRowIndex The index of the row that starts the range
	 * @param lastDataRowIndex  The index of the row that ends the range
	 * @param style             The style of the cell containing the formula
	 * @param function          The function to be applied.
	 * @return
	 */
	HSSFCell addColumnFormulaCell(final int rowIndex, final int columnIndex, final int firstDataRowIndex,
			final int lastDataRowIndex, final Style style, final String function) {

		final String firstAddress = CellAddress.getCellAddress(firstDataRowIndex, columnIndex);
		final String secondAddress = CellAddress.getCellAddress(lastDataRowIndex, columnIndex);
		final String formula = function + "(" + firstAddress + ":" + secondAddress + ")";
		final HSSFRow row = getRow(rowIndex);
		final HSSFCell cell = row.createCell(columnIndex);
		cell.setCellFormula(formula);
		final HSSFCellStyle hstyle = styleFactory.getHSSFCellStyle(style);
		cell.setCellStyle(hstyle);
		return cell;
	}

	/**
	 * Creates a formula cell.
	 * 
	 * Example: to create a sum cell on
	 * 
	 * SUMIF(C2:C107;"B";I2:I107)+SUMIF(C2:C107;"I";I2:I107)
	 * 
	 * SUMIF(${OC_DESIGNATOR};"B";${CORE_REPORT})+SUMIF(${OC_DESIGNATOR};"I";${
	 * CORE_REPORT})
	 * 
	 * VLOOKUP(lookup_value, table_array, col_index_num, range_lookup)
	 * 
	 * VLOOKUP("B", A2:Q107, 2, FALSE)
	 * 
	 * @param rowIndex          The index (rownumber - 1) of the row into which the
	 *                          formula should be placed
	 * @param columnIndex       The index of the column into which the formula
	 *                          should be placed
	 * @param firstDataRowIndex The index of the row that starts the range
	 * @param lastDataRowIndex  The index of the row that ends the rang
	 * @param style             The style of the cell containing the formula
	 * @param function          The function to be applied.
	 * @return
	 */
	HSSFCell addColumnFormulaReferenceCell(final int rowIndex, final int columnIndex, final int firstDataRowIndex,
			final int lastDataRowIndex, final Style style, final String function) {

		final String firstAddress = CellAddress.getCellAddress(firstDataRowIndex, columnIndex);
		final String secondAddress = CellAddress.getCellAddress(lastDataRowIndex, columnIndex);
		final String formula = function + "(" + firstAddress + ":" + secondAddress + ")";
		final HSSFRow row = getRow(rowIndex);
		final HSSFCell cell = row.createCell(columnIndex);
		cell.setCellFormula(formula);
		final HSSFCellStyle hstyle = styleFactory.getHSSFCellStyle(style);
		cell.setCellStyle(hstyle);
		return cell;
	}

	HSSFRow getRow(final int index) {
		HSSFRow row = sheet.getRow(index);
		if (row == null) {
			row = sheet.createRow(index);

		}
		return row;
	}

	// todo this needs to be moved to a more common place
	void fixColumnWidths(final int startColumnIndex, final int endColumnIndex) {
		for (int i = startColumnIndex; i <= endColumnIndex; i++) {
			fixColumnWidth(i);
		}
	}

	void setColumnWidth(final int columnIndex, final int characterWidth) {
		final int unitsPerChar = 300;
		final int funkyWidth = characterWidth * unitsPerChar;
		final short shortFunkyWidth = funkyWidth < Short.MAX_VALUE ? (short) funkyWidth : Short.MAX_VALUE;
		sheet.setColumnWidth(columnIndex, shortFunkyWidth);
	}

	void fixColumnWidth(final int columnIndex) {
		final int rowCount = sheet.getPhysicalNumberOfRows();
		int maxWidth = 0;
		for (int i = 0; i < rowCount; i++) {
			final HSSFRow r = sheet.getRow(i);
			final HSSFCell c = r.getCell(columnIndex);

			if (c != null) {
				final String v = c.getStringCellValue();
				if (v != null && v.length() > maxWidth) {
					maxWidth = v.length();

				}
			}
		}
		if (maxWidth > 8) {
			sheet.setColumnWidth(columnIndex, maxWidth);
		}
	}

	void checkColumns(final List<ColumnMetadata> columns) {
		boolean nullFound = false;
		for (final ColumnMetadata col : columns) {
			if (col == null) {
				nullFound = true;
			}
		}
		if (nullFound) {
			final StringBuilder b = new StringBuilder();
			for (final ColumnMetadata col : columns) {
				b.append(col);
				b.append("\n");
			}
			logger.error(columns.toString());
		}
	}

	Style getStyle(final Style baseStyle, final String excelFormat, final HorizontalAlignment horizontalAlignment) {
		if (baseStyle == null) {
			throw new IllegalArgumentException("baseStyle is null");
		}
		final StyleImpl s = new StyleImpl();
		s.setFormatMask(excelFormat);
		s.setHorizontalAlignment(horizontalAlignment);
		s.setVerticalAlignment(VerticalAlignment.TOP);
		final Style cellStyle = s.getStyle(baseStyle, s);
		return cellStyle;
	}

	/**
	 * Creates a formula cell based on parameters given for location of that cell.
	 * 
	 * Example:
	 * 
	 * addFormulaCell(column.getWorkbookFormula(), columnIndexByName,
	 * column.getColumnName(), rowIndex, columnIndex)
	 * 
	 * @param template
	 * @param columnIndexByName
	 * @param columnName
	 * @return
	 */
	HSSFCell addFormulaCell(final String template, final Map<String, Integer> columnIndexByName,
			final String columnName, final int rowIndex, final int columnIndex) {
		if (template == null || columnIndexByName == null) {
			throw new IllegalArgumentException();
		}

		logger.debug("template : " + template + " columnIndexByName " + columnIndexByName + " " + columnName + " "
				+ rowIndex + " " + columnIndex);

		final String formula = CellFormula.getAsFormula(template, columnIndexByName, rowIndex);
		// logger.debug("formula is " + formula);
		final HSSFCell cell = getCell(rowIndex, columnIndex);
		logger.debug("cell is " + cell.getRowIndex() + " " + cell.getColumnIndex() + " formula " + formula);
		try {
			cell.setCellFormula(formula);
		} catch (final Exception wtf) {
			final StringBuilder sb = new StringBuilder();
			sb.append("this is really goofy\n");
			sb.append("but go ahead and replace the semicolons with commas and poi will change them back to ';' \n");
			sb.append("exception was " + wtf.getClass().getName() + "\n");
			sb.append(wtf.getMessage());
			throw new IllegalArgumentException(sb.toString());
		}
		return cell;
	}

	void defaultSheetFormat() {
		sheet.setMargin(Sheet.LeftMargin, .25); // in inches
		sheet.setMargin(Sheet.RightMargin, .25);
		sheet.setMargin(Sheet.TopMargin, .75);
		sheet.setMargin(Sheet.BottomMargin, .25);

		// These next three lines are to add gridlines to all cells on print
		sheet.setGridsPrinted(true);
		sheet.setPrintGridlines(true);
		sheet.setDisplayGridlines(true);

		// These next four lines are to fit the sheet to 1 page wide
		sheet.setAutobreaks(true);
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setFitWidth((short) 1);
		sheet.getPrintSetup().setFitHeight((short) 0);
	}

	public HSSFCellStyleFactory getStyleFactory() {
		return styleFactory;
	}

	public void setStyleFactory(final HSSFCellStyleFactory styleFactory) {
		this.styleFactory = styleFactory;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public void setSheet(final HSSFSheet sheet) {
		this.sheet = sheet;
	}

}
