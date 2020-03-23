package org.javautil.document.crosstab;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */
public class CrosstabColumns {
	private ArrayList<String> rowIdentifiers;
	private String            columnIdentifier;
	private ArrayList<String> cellIdentifiers;

	public CrosstabColumns(ArrayList<String> rowIdentifiers) {
		super();
		this.rowIdentifiers = rowIdentifiers;
	}

	public CrosstabColumns(ArrayList<String> rowIdentifiers, String columnIdentifier, ArrayList<String> cellIdentifiers) {
		super();
		this.rowIdentifiers = rowIdentifiers;
		this.columnIdentifier = columnIdentifier;
		this.cellIdentifiers = cellIdentifiers;
	}

	public CrosstabColumns(List<String> rowIdentifiers, String columnIdentifier, List<String> cellIdentifiers) {
		super();
		this.rowIdentifiers = new ArrayList<String>();
		this.rowIdentifiers.addAll(rowIdentifiers);
		this.columnIdentifier = columnIdentifier;
		this.cellIdentifiers = new ArrayList<String>();
		this.cellIdentifiers.addAll(cellIdentifiers);
	}

	public ArrayList<String> getCellIdentifiers() {
		return cellIdentifiers;
	}

	public String getColumnIdentifier() {
		return columnIdentifier;
	}

	public ArrayList<String> getRowIdentifiers() {
		return rowIdentifiers;
	}

	public void setRowIdentifiers(ArrayList<String> rowIdentifiers) {
		this.rowIdentifiers = rowIdentifiers;
	}

	public void setColumnIdentifier(String columnIdentifier) {
		this.columnIdentifier = columnIdentifier;
	}

	public void setCellIdentifiers(ArrayList<String> cellIdentifiers) {
		this.cellIdentifiers = cellIdentifiers;
	}

}
