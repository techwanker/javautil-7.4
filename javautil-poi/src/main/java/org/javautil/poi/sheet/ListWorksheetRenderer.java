package org.javautil.poi.sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.javautil.core.collections.CollectionsUtil;
import org.javautil.dataset.ColumnMetaMap;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DataType;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetIterator;
import org.javautil.dataset.DatasetMetadata;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.document.style.Style;
import org.javautil.poi.range.DataRange;
import org.javautil.poi.style.HSSFCellStyleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListWorksheetRenderer extends BaseWorksheetRenderer {
	private static final Logger logger = LoggerFactory.getLogger(ListWorksheetRenderer.class);

	public ListWorksheetRenderer(final HSSFSheet sheet, final HSSFCellStyleFactory styleFactory) {
		super(sheet, styleFactory);

	}

	public DataRange emitRegion(final Dataset dataset ) {
		return emitRegion(dataset,  0, 0);
	}

	// TODO columns could have been tweaked before getting here
	public DataRange emitRegion(final Dataset dataset,  final int startingRow,
			final int startingColumn) {
		List<ColumnMetadata> columns= dataset.getMetadata().getColumnMetadata();
		logger.info("before tweaking:\n{}",ColumnMetadata.toString(columns));
		List<ColumnMetadata> tweakedMetadata = getTweakedMetadata(columns);
		logger.info("before tweaking:\n{}",ColumnMetadata.toString(columns));
		final DataRange range = new DataRange();
		range.setFirstRow(startingRow);
		range.setFirstColumn(startingColumn);
		range.setDataBeginsRow(startingRow + 1);
		range.setLastColumn(startingColumn + columns.size());
		range.setLastRow(startingRow + dataset.getDatasetIterator().getRowCount() + 2);
		range.setFooterBeginsRow(startingRow + dataset.getDatasetIterator().getRowCount() + 2);
		// headers
		emitHeaders(tweakedMetadata, getStyleFactory().getBaseHeaderStyle(), startingRow, startingColumn, null, null);
		// body
		final int lastDataRowIndex = emitData(dataset, tweakedMetadata, getStyleFactory().getBaseDataStyle(),
				startingRow + 1, startingColumn);
		// footers
		emitSummary(tweakedMetadata, lastDataRowIndex + 1, startingColumn, startingRow + 1, lastDataRowIndex,
				getStyleFactory().getBaseFooterStyle());

		return range;
	}

	
	// TODO this duplicates a lot of WorkbookRenderer.OverRideMetadata
	public void emitHeaders(final List<ColumnMetadata> columns, final Style baseStyle, final int startingRow,
			final int startingColumn, final List<String> nonRowIdentifyingColumns,
			final Map<String, ColumnMetadata> metadataByColumnName) {

		if (baseStyle == null) {
			throw new IllegalArgumentException("baseStyle is null");
		}
		logger.debug("baseStyle: " + baseStyle);
		final int rowIndex = startingRow;
		int columnIndex = startingColumn;
		int entry = 0;
		for (final ColumnMetadata column : columns) {
			Style cellStyle = null;
			String heading = "";
			if (column == null) {
				logger.warn("null column at entry " + entry);
				cellStyle = baseStyle;
			} else {
				logger.debug("horizontal alignment for '" + column + "' is " + column.getHorizontalAlignment());

				HorizontalAlignment horizAlign = column.getHorizontalAlignment();
				if (horizAlign == null && column.getDataType() != null) {
					switch (column.getDataType()) {
					case NUMERIC:
						horizAlign = HorizontalAlignment.RIGHT;
						break;
					default:
						horizAlign = HorizontalAlignment.LEFT;
					}
				}
				column.setHorizontalAlignment(horizAlign);
				logger.info("horizAlign for {} is {}", column.getColumnName(), horizAlign);
				cellStyle = getStyle(baseStyle, column.getExcelFormat(), horizAlign);
				logger.debug("87 cellStyle: " + cellStyle.toString());
				//
				if (column.getGroupName() != null) {
					heading = column.getGroupName() + "\n" + column.getHeading();
				} else {
					heading = column.getHeadingOrColumnName();
				}
			}
			if (heading == null) {
				logger.info(String.format("heading is null for columnName %s setting to heading %s %s ",
						column.getColumnName(), column.getHeading(), column.toString()));
				column.setHeading(column.getHeading());
			} else {
				if (heading.indexOf("\\n") > -1) {
					heading = heading.replace("\\n", "\n");
				}
			}
			if (heading.indexOf('\n') > -1) {
				cellStyle = addWordWrap(cellStyle);
			}
			logger.debug("row: {} column: {} heading: '{}' cellStyle: '{}'", rowIndex, columnIndex, heading,
					cellStyle.toString());
			addCell(rowIndex, columnIndex, heading, cellStyle);
			if (column != null && column.getColumnDisplaySize() != null) {
				// TODO Apply auto
				setColumnWidth(columnIndex, column.getColumnDisplaySize().intValue());
			} else {
				// setAutoColumnWidth(columnIndex, 0, 60);
			}
			columnIndex++;
			entry++;
		}
		getSheet().createFreezePane(0, 1 , 1, 1);

	}

//	/**
//	 * Automatically sets the column width based on contents based on the longest
//	 * cell
//	 * 
//	 * @param columnIndex
//	 * @param minimum
//	 *            value for the minimum columns
//	 * @param set
//	 *            to wrap
//	 */
//	public void setAutoColumnWidth(int columnIndex, Integer minimum, Integer maximum) {
//		throw new UnsupportedOperationException();
//	}

	/*
	 * @param dataset
	 * 
	 * @param columns
	 * 
	 * @param baseStyle
	 * 
	 * @return the row index of the last row generated
	 */
	protected int emitData(final Dataset dataset, final List<ColumnMetadata> columns, final Style baseStyle,
			final int startingRow, final int startingColumn) {
		if (dataset == null) {
			throw new IllegalArgumentException("dataset is null");
		}
		if (columns == null) {
			throw new IllegalArgumentException("columns is null");
		}
		if (baseStyle == null) {
			throw new IllegalArgumentException("baseStyle is null");
		}

		logger.info("emitData: columns:\n{}", CollectionsUtil.asString(columns));

		final Map<String, Integer> columnIndexByName = ColumnMetaMap.getColumnNameIndexMap(columns);
		final DatasetIterator datasetIterator = dataset.getDatasetIterator();
		int rowIndex = startingRow;

		while (datasetIterator.next()) {
			// logger.trace("152 adding row " + rowIndex);
			int columnIndex = startingColumn;
			for (final ColumnMetadata column : columns) {
				logger.debug("adding cell row {} column {} name {}", rowIndex, columnIndex, column);
				if (column == null) {
					throw new IllegalStateException("column is null");
				}
				final Object data = datasetIterator.getObject(column.getColumnName());
				HSSFCell cell = addCell(rowIndex, columnIndex, data, baseStyle, column);
				columnIndex++;
			}
			rowIndex++;
		}
		return rowIndex - 1;
	}

	/*
	 * @param columns
	 * 
	 * @param rowIndex
	 * 
	 * @param columnIndex
	 * 
	 * @param firstDataRowIndex
	 * 
	 * @param lastDataRowIndex
	 * 
	 * @param baseStyle
	 */
	public void emitSummary(final List<ColumnMetadata> columns, final int rowIndex, final int columnIndex,
			final int firstDataRowIndex, final int lastDataRowIndex, final Style baseStyle) {
		final Map<String, Integer> columnIndexByName = ColumnMetaMap.getColumnNameIndexMap(columns);

		int currentColumn = columnIndex;
		for (final ColumnMetadata column : columns) {
			if (logger.isDebugEnabled()) {
				logger.debug(column.toString());
			}
			checkColumns(columns);
			final Style cellStyle = getStyle(baseStyle, column.getExcelFormat(), column.getHorizontalAlignment());

			if (column.getAggregateFunction() != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("emitting aggregate function " + column.getAggregateFunction() + " at column "
							+ columnIndex);
				}
				addColumnFormulaCell(rowIndex, currentColumn, firstDataRowIndex, lastDataRowIndex, cellStyle,
						column.getAggregateFunction());

			} else if (column.getWorkbookFormula() != null) {
				final HSSFCell cell = addFormulaCell(column.getWorkbookFormula(), columnIndexByName,
						column.getColumnName(), rowIndex, currentColumn);
				cell.setCellStyle(getStyleFactory().getHSSFCellStyle(cellStyle));
			}
			currentColumn++;
		}
	}

	
	// TODO much of this is in DatasetMetaDataImpl enhance 
	// TODO this hard overrides the excel format
	public static List<ColumnMetadata> getTweakedMetadata(List<ColumnMetadata> metaIn) {
		List<ColumnMetadata> retval = new ArrayList<ColumnMetadata>();
		final String integral = "###,###,###,###";
		final String fractional = "0000000000000000000";
		for (ColumnMetadata meta : metaIn) {
			logger.info("getTweakedMetadata() name: {} excelFormat: {} dataType: {}", meta.getColumnName(),
					meta.getExcelFormat(), meta.getDataType());
			ColumnMetadata newmeta = new ColumnMetadata(meta);
			retval.add(newmeta);
			if (newmeta.getHorizontalAlignment() == null) {
				DataType dataType = newmeta.getDataType();
				if (dataType == null) {
					logger.info("dataType is null for {}", newmeta);
				} else {
					newmeta.setHorizontalAlignment(newmeta.getDataType().getDefaultHorizontalAlignment());
				}
			}

			if (meta.getExcelFormat() == null && meta.getDataType() != null) {
				switch (meta.getDataType()) {
				case SQLDATE:
				case DATE:
					newmeta.setExcelFormat("YYYY-MM-DD");  
					break;
				case TIMESTAMP:
					newmeta.setExcelFormat("YYYY-MM-DD");
					break;
				case NUMERIC:
					int precision = meta.getPrecision();
					int scale = meta.getScale();
					int commas = (precision - 1) / 3;
					int length = scale + precision;
					int beginIndex = integral.length() - 1 - commas;
					String picture = integral.substring(beginIndex);
					String fractionalPicture = "";
					if (scale > 0) {
						fractionalPicture = "." + fractional.substring(0, scale);
					}
					String excelPicture = picture + fractionalPicture;
					newmeta.setExcelFormat(excelPicture);
				default:
					break;
				}
				logger.info("getTweakedMetadata() ", newmeta.toString());
			}
		}
		return retval;
	}

}
