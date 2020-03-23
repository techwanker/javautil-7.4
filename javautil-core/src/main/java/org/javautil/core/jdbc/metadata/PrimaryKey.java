package org.javautil.core.jdbc.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

/**
 * 
 * @author jjs@javautil.org
 * 
 *         TODO why is this any different than a unique constraint or for that
 *         matter any other
 * @version 1.0
 */
public class PrimaryKey implements Iterable<String> {
	private final String                   schema;
	private final String                   tableName;
	private final String                   primaryKeyName;
	private final TreeMap<Integer, String> columns = new TreeMap<Integer, String>();

	public PrimaryKey(final String schema, final String tableName, final String primaryKeyName) {
		this.schema = schema;
		this.tableName = tableName;
		this.primaryKeyName = primaryKeyName;
	}

	public void addColumn(final String columnName, final int keySeq) {
		columns.put(new Integer(keySeq), columnName);
	}

	/*
	 * 
	 * @return the name of the columns in the primary key in the order in which they
	 * occur.
	 */
	public List<String> getColumnNames() {
		final ArrayList<String> columnNames = new ArrayList<String>();
		for (final Integer seq : columns.keySet()) {
			columnNames.add(columns.get(seq));
		}
		return columnNames;
	}

	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public String getSchema() {
		return schema;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public Iterator<String> iterator() {
		return columns.values().iterator();
	}

	/*
	 * for hibernate revenge generates <table schema="INTERFAST"
	 * name="IC_ITEM_MAST"> <primary-key> <key-column name="ITEM_NBR"/>
	 * </primary-key> </table>
	 * 
	 * @return
	 */
	public String toRevengXml() {
		Document doc = DocumentFactory.getInstance().createDocument();

		Element table = doc.addElement("table");
		table.addAttribute("schema", schema);
		table.addAttribute("name", tableName);
		Element pk = table.addElement("primary-key");
		for (String columnName : getColumnNames()) {
			pk.addElement("key-column").addAttribute("name", columnName);
		}

		String retval = table.asXML();
		System.out.println("retval: " + retval);
		return retval;
	}

}
