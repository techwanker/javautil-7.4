package com.pacificdataservices.pdssr;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.MappedResultSetIterator;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.text.StringUtils;
import org.javautil.core.text.StringUtils;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts the data from the following tables etl_customer etl_customer_tot
 * etl_sale etl_sale_tot etl_inventory etl_inventory_tot
 * 
 * The records are retrieved by the original order and the output file should be
 * identical to the input file.
 * 
 * @author jjs
 *
 */
class CdsUnload {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, ListOfNameValue> record_defs;
	private Dialect dialect;
	private FixedRecordUtil field_metadata = new FixedRecordUtil();
	private FileWriter outfile;
	private Connection connection = null;

	/**
	 *
	 * @param connection
	 * @param dialect
	 * @param calc_totals
	 * @throws FileNotFoundException
	 */
	public CdsUnload(Connection connection, Dialect dialect, boolean calc_totals) throws FileNotFoundException {

		this.connection = connection;
		this.dialect = dialect;
		record_defs = CdsReportingFileMetadata.getRecordDefs();

	}

	void emit(String string) throws IOException {

		int REQUIRED_LEN = 170;
		if (string.length() != REQUIRED_LEN) {
			throw new RuntimeException("required len " + REQUIRED_LEN + " actual " + string.length() + " " + string);
		}
		outfile.write(string);
		outfile.write("\n");
	}

	void emit_sale(NameValue binds) throws IOException {
		binds.put("FILLER_12", "            ");
		binds.put("FILLER", "         ");
		binds.put("RECORD_TYPE", "SA");
		binds.put("EXTENDED_NET_AMT", binds.getLong("EXTENDED_NET_AMT") * 100);
		binds.put("CASES_SHIPPED", binds.getLong("CASES_SHIPPED"));
		binds.put("BOXES_SHIPPED", binds.getLong("BOXES_SHIPPED"));
		binds.put("UNITS_SHIPPED", binds.getLong("UNITS_SHIPPED"));

		String record = field_metadata.formatLine(record_defs.get("sales"), binds, false);
		emit(record);
	}

	void unload_sale(long id, boolean by_etl_sale_id) throws IOException, SQLException {
		SqlStatement ss = null;
		Binds ebinds = new Binds();

		if (by_etl_sale_id) {
			ebinds.put("ETL_SALE_ID", id);
			ss = new SqlStatement("select * from etl_sale where etl_sale_id = %(ETL_SALE_ID)s");
		} else {
			ebinds.put("ETL_FILE_ID", id);
			ss = new SqlStatement("select * from etl_sale where etl_file_id = %(ETL_FILE_ID)s");
		}
		for (NameValue binds : ss.iterator(ebinds)) {
			emit_sale(binds);
		}
	}

	void emit_inventory(NameValue binds) {
		ListOfNameValue recordDefinition = record_defs.get("inventory");
		binds.put("RECORD_TYPE", "IR");
		binds.put("CASES", binds.getLong("CASES"));
		binds.put("BOXES", binds.getLong("BOXES"));
		binds.put("UNITS", binds.getLong("UNITS"));
		binds.put("FILLER", "            ");
		if (binds.get("CASE_GTIN") == null) {
			binds.put("CASE_GTIN", "000000000000000");
		}
		String outrec = field_metadata.formatLine(recordDefinition, binds, false);
		try {
			emit(outrec);
		} catch (Exception e) {
			String msg = String.format("binds: " + binds + "\n outrec: " + outrec + "\n" + e);
			throw new RuntimeException(msg);
		}
	}

	void unload_inventory(long id, boolean by_etl_inventory_id) throws SQLException {
		Binds binds = new Binds();
		SqlStatement ss = null;
		if (by_etl_inventory_id) {
			ss = new SqlStatement("select * from etl_inventory where etl_inventory_id = %(ETL_INVENTORY_ID)s");
			binds.put("ETL_INVENTORY_ID", id);
		} else {
			ss = new SqlStatement("select * from etl_inventory where etl_file_id = %(ETL_FILE_ID)s");
			binds.put("ETL_FILE_ID", id);
		}

		for (NameValue row : ss.getListOfNameValue(binds)) {
			emit_inventory(row);
		}
	}

	/**
	 * 
	 * @param etl_file_id
	 * @param computeTrue - compute from etl_customer records False - extract only
	 * @throws SQLException
	 * @throws IOException
	 */
	void unload_customer_total(long etl_file_id, boolean compute) throws SQLException, IOException {
		ListOfNameValue recordDefinition = record_defs.get("customer_total");
		SqlStatement ss = new SqlStatement("select * from etl_customer_tot where etl_file_id = %(ETL_FILE_ID)s");
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID", etl_file_id);
		NameValue dataBinds = ss.getNameValue();
		dataBinds.put("HEADER", "9999999999");
		dataBinds.put("RECORD_TYPE", "CT");
		dataBinds.put("FILLER_127", "");
		dataBinds.put("CUSTOMER_COUNT", dataBinds.getLong("CUSTOMER_COUNT"));
		dataBinds.put("FILLER_22", StringUtils.padRight("", 22));
		String outrec = field_metadata.formatLine(recordDefinition, dataBinds, false);
		emit(outrec);
	}

	/**
	 * 
	 * @param etl_file_id the primary key of etl_file being unloaded
	 * @param compute     from actual values False - extract only if record exists
	 * @throws SQLException
	 * @throws IOException
	 */
	void unload_sale_tot(long etl_file_id, boolean compute) throws SQLException, IOException {
		ListOfNameValue recordDef = record_defs.get("sales_total");
		SqlStatement ss = new SqlStatement("select * from etl_sale_tot where etl_file_id = %(ETL_FILE_ID)s");
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID", etl_file_id);
		// Binds dataBinds = new Binds();
		NameValue dataBinds = ss.getNameValue(binds, true);
		if (dataBinds.size() == 1) {
			dataBinds.put("HEADER", "9999999999");
			dataBinds.put("FILLER_28", StringUtils.padRight("", 28));
			dataBinds.put("SALES_REC_CNT", dataBinds.getLong("SALES_REC_CNT"));
			dataBinds.put("SUM_EXT_NET_AMT", dataBinds.getLong("SUM_EXT_NET_AMT") * 100);
			dataBinds.put("FILLER_86", StringUtils.padRight("", 86));
			dataBinds.put("RECORD_TYPE", "ST");
			String outrec = field_metadata.formatLine(recordDef, dataBinds, false);
			emit(outrec);
		} else {
			logger.warn("etl_file_id " + etl_file_id + " has no sales total record");
		}
	}

	void unload_inventory_tot(long etl_file_id, boolean compute) throws IOException, SQLException {

		ListOfNameValue recordDefinition = record_defs.get("inventory_total");
		SqlStatement ss = new SqlStatement("select * from etl_inventory_tot where etl_file_id = %(ETL_FILE_ID)s");
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID", etl_file_id);
		MappedResultSetIterator it = ss.iterator(binds);
		if (it.hasNext()) {
			NameValue dataBinds = it.next();

			dataBinds.put("HEADER", "9999999999");
			dataBinds.put("FILLER36", StringUtils.padRight("", 36));

			dataBinds.put("RECORD_TYPE", "IT");
			dataBinds.put("RECORD_CNT_REPORTED", dataBinds.getLong("RECORD_CNT_REPORTED"));
			dataBinds.put("FILLER97", StringUtils.padRight("", 97));

			logger.debug("record definition " + recordDefinition);
			logger.debug("dataBinds = " + dataBinds);
			String outrec = field_metadata.formatLine(recordDefinition, dataBinds, false);

			emit(outrec);
		}
	}

	void emit_customer(NameValue binds) throws IOException {
		binds.put("FILLER_00_05", "999999");
		binds.put("CLASS_OF_TRADE", "    ");
		binds.put("FILLER_1", " ");
		binds.put("RECORD_TYPE", "CD");
		String outrec = field_metadata.formatLine(record_defs.get("customer"), binds, false);
		emit(outrec);
	}

	void unload_customer(long id, boolean by_etl_customer_id) throws SQLException, IOException {

		Binds binds = new Binds();
		SqlStatement ss;
		if (by_etl_customer_id) {
			ss = new SqlStatement("select * from etl_customer where etl_customer_id = %(ETL_CUSTOMER_ID)s");
			binds.put("ETL_CUSTOMER_ID", id);
		} else {
			ss = new SqlStatement("select * from etl_customer where etl_file_id = %(ETL_FILE_ID)s");
			binds.put("ETL_FILE_ID", id);
		}
		for (NameValue dataBinds : ss.iterator(binds)) {
			emit_customer(dataBinds);
		}
	}

	void process_by_line_number(long etl_file_id, String file_name) throws IOException, SQLException {
		outfile = new FileWriter(file_name);

		SqlStatement lineNumbers = new SqlStatement("select table_name, id, line_number\n" + "from\n" + "(\n"
				+ "        select 'etl_customer' table_name, etl_customer_id id, line_number\n"
				+ "        from etl_customer\n" + "        where etl_file_id = %(ETL_FILE_ID)s\n" + "        union\n"
				+ "        select 'etl_sale', etl_sale_Id, line_number\n" + "        from etl_sale\n"
				+ "        where etl_file_id = %(ETL_FILE_ID)s\n" + "        union\n"
				+ "        select 'etl_inventory', etl_inventory_id, line_number\n" + "        from etl_inventory\n"
				+ "        where etl_file_id = %(ETL_FILE_ID)s\n" + "        union\n"
				+ "        select 'etl_inventory_tot', etl_file_id, line_number\n" + "        from etl_inventory_tot\n"
				+ "        where etl_file_id = %(ETL_FILE_ID)s\n" + "        union\n"
				+ "        select 'etl_customer_tot', etl_file_id, line_number\n" + "        from etl_customer_tot\n"
				+ "        where etl_file_id = %(ETL_FILE_ID)s\n" + ")  as by_line_number\n"
				+ "        order by line_number");

		Binds ebinds = new Binds();
		ebinds.put("ETL_FILE_ID", etl_file_id);

		for (NameValue binds : lineNumbers.iterator(ebinds)) {

			String table_name = binds.getString("table_name");
			long lineNumber = binds.getLong("line_number");
			long id = binds.getLong("id");

			if (table_name.equals("etl_customer")) {
				unload_customer(id, true);
			} else if (table_name.equals("etl_sale")) {
				unload_sale(id, true);
			} else if (table_name.equals("etl_inventory")) {
				unload_inventory(id, true);
			} else if (table_name.equals("etl_customer_tot")) {
				unload_customer_total(etl_file_id, true);
			} else if (table_name.equals("etl_inventory_tot")) {
				unload_inventory_tot(etl_file_id, true);
			} else if (table_name.equals("etl_sale_tot")) {
				unload_sale_tot(etl_file_id, true);
			} else {
				throw new UnsupportedOperationException(String.format("unsupported record %s", table_name));
			}
		}
		outfile.close();
	}

	void process_by_table(long etl_file_id, String file_name) throws SQLException, IOException {
		outfile = new FileWriter(file_name);

		unload_customer(etl_file_id, false);
		unload_sale(etl_file_id, false);
		unload_inventory(etl_file_id, false);
		unload_customer_total(etl_file_id, false);
		unload_inventory_tot(etl_file_id, false);
		unload_sale_tot(etl_file_id, false);
		outfile.close();
	}

	/**
	 * 
	 * @param etl_file_id
	 * @param file_name
	 * @param by_line_number if true, the output is generated in the same order as
	 *                       the input else it is unloaded by table
	 * @throws SQLException
	 * @throws IOException
	 */
	void process(long etl_file_id, String file_name, boolean by_line_number) throws IOException, SQLException {
		if (by_line_number) {
			process_by_line_number(etl_file_id, file_name);
		} else {
			process_by_table(etl_file_id, file_name);
		}
	}
}

// void unload_all() {
//
// cursor = CursorHelper(conn.cursor())
// sql = "select etl_file_id from etl_file"
// rows = cursor.execute(sql)
//
// for row in rows {
// etl_file_id = row[0]
// process(etl_file_id, "../pdssr_testdata/%s.cds" % etl_file_id, False)
//
// }
// # if __name__ == "__main__" {
// # myconn = ConnectionHelper().get_named_connection("it")
// # unloader = CdsUnload(myconn)
// # unloader.process(30, "/tmp/201502.cds", False) # TODO
