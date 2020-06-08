package com.pacificdataservices.pdssr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.javautil.core.misc.Timer;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SequenceHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.sql.SqlStatements;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.TreeHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdsDataLoader implements FilenameFilter {

	private static final Logger logger = LoggerFactory.getLogger(CdsDataLoader.class);
	/**
	 * Loads a CDS format file into ETL tables
	 * 
	 */
	private Connection connection = null;
	private Dialect dialect;
	// TODO load from resource
	// private String etlSqlYamlFileName =
	// "src/main/resources/com/pacificdataservices/pdssr/etl_persistence_colon.yaml";
	private InputStream etlPersistenceStream = this.getClass().getResourceAsStream("etl_persistence_colon.yaml");

	SqlStatements sqlStatements;

	private Map<String, String> sqlNameByType = new HashMap<String, String>();

	private static final String CUSTOMER_RECORD = "CD";
	private static final String CUSTOMER_TOTAL_RECORD = "CT";
	private static final String INVENTORY_RECORD = "IR";
	private static final String INVENTORY_TOTAL_RECORD = "IT";
	private static final String SALES_RECORD = "SA";
	private static final String SALES_TOTAL_RECORD = "AT";

	public CdsDataLoader(Connection conn, Dialect dialect) throws FileNotFoundException, SQLException {
		this.connection = conn;
		this.dialect = dialect;
		connection.setAutoCommit(false);
		sqlNameByType.put(CUSTOMER_RECORD, "etl_customer_insert");
		sqlNameByType.put(CUSTOMER_TOTAL_RECORD, "etl_customer_tot_insert");
		sqlNameByType.put(INVENTORY_RECORD, "etl_inventory_insert");
		sqlNameByType.put(INVENTORY_TOTAL_RECORD, "etl_inventory_tot_insert");
		sqlNameByType.put(SALES_RECORD, "etl_sale_insert");
		sqlNameByType.put(SALES_TOTAL_RECORD, "etl_sale_tot_insert");
		sqlStatements = new SqlStatements(etlPersistenceStream, conn);
		//logger.info("sqlStatements:\n{}", getSqlStatementsToString());
	}

	/**
	 * 
	 * @param filename
	 * @param conn
	 * @param distributor_cd
	 * @param validate
	 * @throws ParseException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void process(String filename, Connection conn, String distributor_cd, boolean validate)
			throws ParseException, IOException, SQLException {
		SequenceHelper sequences = new SequenceHelper(conn);
		long startTime = System.nanoTime();
		Binds binds = new Binds();
		binds.put("ORG_CD", distributor_cd);

		CdsFileReader reader = new CdsFileReader(filename);

		Long fileId = initialInsert(binds);
		logger.debug("process: fileId {}",fileId);
		while ((binds = reader.readLine()) != null) {
			binds.put("ETL_FILE_ID", fileId);
			binds.put("LINE_NUMBER", reader.getLineNumber());
			if (binds.containsKey("EXTENDED_NET_AMT")) {
				Integer extNetAmt = (Integer) binds.get("EXTENDED_NET_AMT");
				if (extNetAmt != null) {
					binds.put("EXTENDED_NET_AMT", extNetAmt / 100);
				}
			}
			String sqlName = sqlNameByType.get(reader.getRecordType());
			SqlStatement sh = sqlStatements.getSqlStatement(sqlName);
			sh.setConnection(conn); // TODO set all of the sqlstatemet
			if (CUSTOMER_RECORD.equals(reader.getRecordType())) {
				TreeMap<String, Object> info = new TreeMap<>();
				for (Entry<String, Object> e : binds.entrySet()) {
					info.put(e.getKey(), e.getValue());
				}
				String hash = TreeHash.hash(info);
				binds.put("INFO_HASH", hash);
				long etlCustomerId = sequences.getSequence("etl_customer_id_seq");
				binds.put("ETL_CUSTOMER_ID",etlCustomerId);
			}
			if (SALES_RECORD.equals(reader.getRecordType())) {
				long etlSaleId = sequences.getSequence("etl_sale_id_seq");
				binds.put("ETL_SALE_ID",etlSaleId);
			}
			if (INVENTORY_RECORD.equals(reader.getRecordType())) {
				long etlInventoryId = sequences.getSequence("etl_inventory_id_seq");
				binds.put("ETL_INVENTORY_ID",etlInventoryId);
			}
			logger.debug("line #: {} binds: {}", reader.getLineNumber(), binds);
//			logger.debug("line#: {}\nline: {}\nsql:{}\nbinds: {}", reader.getLineNumber(), reader.getInputLine(),
//					sh.getSql(), binds);
			sh.execute(binds);
		}

		conn.commit();
		long endTime = System.nanoTime();
		long elapsedMillis = (endTime - startTime) / 1000000;
		int lines = reader.getLineNumber();
		reader.close();
		logger.info("filename " + filename + " elapsed " + elapsedMillis + " lines " + lines);
	}

	public void loadFiles(String dir, int loadCount, String distributorCode) {
		logger.info("loadFiles");
		Timer t = new Timer("loadFiles");
		String loadFileDir = "src/test/resources/dataloads";
		if (dir != null) {
			loadFileDir = dir;
		}
		File loadDirFile = new File(loadFileDir);
		FilenameFilter filter = this;

		File[] files = loadDirFile.listFiles(filter);
		Arrays.sort(files);

		int fileCount = 0;
		for (File f : files) {
			if (++fileCount > loadCount) {
				logger.info("fileCount " + fileCount);
				break;
			}
			try {
				process(f.getAbsolutePath(), connection, distributorCode, false);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			logger.info("fileCount " + fileCount);
		}

		t.logElapsed();
	}

	/**
	 * 
	 * @param binds
	 * @return
	 * @throws SQLException
	 */
	Long initialInsert(Binds binds) throws SQLException {
		if (sqlStatements == null) {
			throw new IllegalStateException("sqlStatements is null");
		}
		SqlStatement sh = sqlStatements.getSqlStatement("org_sql");
		sh.setConnection(connection);
		ListOfNameValue lonv = sh.getListOfNameValue(binds);
		if (lonv.size() == 0) {
			throw new IllegalArgumentException("no such org" + binds);
		}

		SqlStatement etlFileIdSeq = sqlStatements.getSqlStatement("etl_file_id_seq");
		etlFileIdSeq.setConnection(connection); // TODO set all of the connections in SqlStatements
		long etlFileId = etlFileIdSeq.nextval();
		binds.put("etl_file_id", etlFileId);

		sh = sqlStatements.getSqlStatement("etl_file_initial_insert");
		sh.setConnection(connection);

		sh.execute(binds);
		return etlFileId;
	}

	public void loadFiles() throws SQLException, ParseException, IOException {
		logger.info("loadFiles");

		String loadFileDir = "src/test/resources";
		File loadDirFile = new File(loadFileDir);
		FilenameFilter filter = this;
		File[] files = loadDirFile.listFiles(filter);
		Arrays.sort(files);

		int fileCount = 0;
		for (File f : files) {
			logger.info("processing file " + f.getAbsolutePath());
			process(f.getAbsolutePath(), connection, "EXOTICTX", false);
			if (fileCount > 10) {
				break;
			}
		}
		fileCount++;
	}

	@Override
	public boolean accept(File dir, String fileName) {
		return fileName.endsWith(".cds");
	}

	public void dispose() {
		sqlStatements.close();
	}

//	public static void main(String [] args) throws FileNotFoundException, SQLException, ParseException, IOException, PropertyVetoException {
//		new CdsDataLoader(getConnection()).loadFiles();
//	}

	// TODO put in SqlStatements a nice format
	// TODO this should be in SqlStatements 
	String getSqlStatementsToString() {
		return sqlStatements.toString();
	}

	public void infoStatements() {
		logger.info("sqlStatements:\n{}", getSqlStatementsToString());
	}
}
