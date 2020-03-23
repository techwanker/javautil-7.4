package org.javautil.poi.formula;

import org.javautil.poi.cell.CellAddress;

public class Formula {
	/**
	 * If you don't know the column index for the letter of the column use POI's
	 * CellReference.convertColStringToIndex method to find it.
	 */
	public String getColumnSumFormula(final int columnIndex, final int startRow, final int endRow) {
		return "SUM(" + getRange(columnIndex, startRow, endRow) + ")";
	}

	public String getRange(final int columnIndex, final int startRow, final int endRow) {
		final String columnId = CellAddress.computeColumnName(columnIndex);
		return columnId + startRow + ":" + columnId + endRow;
	}
}
