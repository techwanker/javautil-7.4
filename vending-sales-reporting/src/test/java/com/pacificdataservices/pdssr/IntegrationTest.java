package com.pacificdataservices.pdssr;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import org.javautil.conditionidentification.DropUtConditionDatabaseObjects;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.misc.Timer;
import org.javautil.util.NameValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTest implements FilenameFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private DbTest dbTest = null;
	// private DbTest dbTest = new DbTest(Dialect.POSTGRES);

	private Dialect dialect; // = dbTest.getDialect();

	private Connection conn;

	private int loadCount = 10;

	public IntegrationTest() throws FileNotFoundException, PropertyVetoException, SQLException {
	}
	@Test
	public void fullOracleDialectTest() throws SQLException, IOException, ParseException, PropertyVetoException, InvalidLoadFileException {
		dialect = Dialect.ORACLE;
		dbTest = new DbTest(Dialect.ORACLE);
		conn = getConnection(Dialect.ORACLE);
		fullTest(Dialect.ORACLE);
	}
	
	//@Test
	public void fullDialectTest() throws SQLException, IOException, ParseException, PropertyVetoException, InvalidLoadFileException {
		dialect = Dialect.POSTGRES;
		dbTest = new DbTest(Dialect.POSTGRES);
		conn = getConnection(Dialect.POSTGRES);
		fullTest(Dialect.POSTGRES);
	}

	public void fullTest(Dialect dialect) throws SQLException, IOException, ParseException, PropertyVetoException, InvalidLoadFileException {
		logger.info("dialect is " + dialect);
		Timer t = new Timer("fullTest " + dialect);
		logger.info("fullTest");
		if (dialect.equals(Dialect.POSTGRES)) {
			dropSchema();
		}
		createSchema();
		seedDatabase();
		loadFiles(null);
		runConditionsAll();
		prepostAll();
		postAll();
//		unloadAll();
		t.logElapsed();
		conn.commit();
		conn.close();

	}

	private void dropSchema() throws SQLException, IOException, PropertyVetoException {
		Timer t = new Timer("dropSchema");
		DropUtConditionDatabaseObjects dropper = new DropUtConditionDatabaseObjects(conn, dialect, true);
		dropper.process();
		t.logElapsed();

		DropSchema dt = new DropSchema(conn, dialect);
		dt.process();
		conn.rollback(); // maybe some objects didn't exist

	}

	private void createSchema() throws SQLException, IOException {
		Timer t = new Timer("createSchema");
		logger.info("creating schema");
//		SqlStatement ss = new SqlStatement(conn,"create schema integration_test");
//		//ss.prepare(conn, dialect);
//		ss.execute(new Binds());
//		logger.info("about to create schema conn: " + conn + " dialect: " + dialect);
		CreateSchema cs = new CreateSchema(conn, dialect);
		cs.process();
		conn.commit();
		t.logElapsed();
		logger.info("schema created");
	}

	private void seedDatabase() throws SQLException, IOException {
		logger.info("seedDatabase");
		Timer t = new Timer("seedDatabase");
		SeedSalesReportingDatabase seeder = new SeedSalesReportingDatabase(conn);
		seeder.process();
		t.logElapsed();
	}

	public void loadFiles(String dir) throws SQLException, ParseException, IOException {
		logger.info("loadFiles");
		Timer t = new Timer("loadFiles");
		String loadFileDir = "src/test/resources/dataloads";
		if (dir != null) {
			loadFileDir = dir;
		}
		File loadDirFile = new File(loadFileDir);
		FilenameFilter filter = this;
		CdsDataLoader loader = new CdsDataLoader(conn, dialect);
		loader.infoStatements();
		File[] files = loadDirFile.listFiles(filter);
		// TODO 
//		logger.info("files:\n" + files);
		Arrays.sort(files);

		int fileCount = 0;
		for (File f : files) {
			if (++fileCount > loadCount) {
				break;
			}
			loader.process(f.getAbsolutePath(), conn, "EXOTICTX", false);
			logger.info("fileCount " + fileCount);
		}
		loader.dispose();

		t.logElapsed();
	}

	private void runConditionsAll() throws FileNotFoundException, SQLException {
		Timer t = new Timer("runConditionsAll");
		logger.info("runConditionsAll");
		LoadConditionIdentification lci = new LoadConditionIdentification(conn, dialect);
		SqlStatement loads = new SqlStatement("select etl_file_id from etl_file");
		loads.setConnection(conn);

		int fileCount = 0;
		for (NameValue nv : loads.iterator(new Binds())) {
			logger.info("####\n####\n####" + fileCount);
	    Binds binds = new Binds();		
			binds.put("ETL_FILE_ID",nv.getLong("ETL_FILE_ID"));
			if (++fileCount > loadCount) {
				break;
			} else {
				lci.process(binds, 3);
			}
		}
		t.logElapsed();
	}

	// TODO dedup from postall
	private void prepostAll() throws SQLException, IOException, InvalidLoadFileException {
		Timer t = new Timer("prepostAll");
		logger.info("prepostAll");
		Prepost prepost = new Prepost(conn, dialect, 5);
		SqlStatement loads = new SqlStatement(conn, "select etl_file_id from etl_file");
		int fileCount = 0;
		// TODO should just get a tuple
		for (NameValue binds : loads.iterator(new Binds())) {
			try {
				if (++fileCount > loadCount) {
					break;
				}
				prepost.process(binds.getLong("ETL_FILE_ID"));
			} catch (InvalidLoadFileException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
		t.logElapsed();
	}

	private void postAll() throws SQLException, IOException {
		Timer t = new Timer("postAll");
		logger.info("postAll");
		Post post = new Post(conn, dialect, 5);
		SqlStatement loads = new SqlStatement(conn, "select etl_file_id from etl_file");
		int fileCount = 0;
		for (NameValue binds : loads.iterator(new Binds())) {
			try {
				if (++fileCount > loadCount) {
					break;
				}
				post.process(binds.getLong("ETL_FILE_ID"));
			} catch (InvalidLoadFileException e) {
				logger.error(e.getMessage());
			}
		}
		t.logElapsed();
	}

	private void unloadAll() throws SQLException, IOException {
		Timer t = new Timer("unloadAll");
		SqlStatement loads = new SqlStatement("select etl_file_id from etl_file");
		String destDir = "/tmp/";
		CdsUnload unloader = new CdsUnload(conn, dialect, false);
		int fileCount = 0;
		// for (Binds binds : loads.iterator(conn, dialect, new Binds())) {
		for (NameValue binds : loads.iterator(new Binds())) {
			long etlFileId = binds.getLong("ETL_FILE_ID");
			String fileName = destDir + etlFileId + ".cds";
			unloader.process(etlFileId, fileName, false);
			if (++fileCount >= loadCount) {
				break;
			}
		}

		t.logElapsed();
	}

	@Override
	public boolean accept(File dir, String fileName) {
		return fileName.endsWith(".cds");
	}

	public Connection getConnection(Dialect dialect) throws SQLException, FileNotFoundException, PropertyVetoException {
		Connection retval;
		switch (dialect) {
		case POSTGRES:
			retval = dbTest.getPostgresConnection();
			break;
		case H2:
			retval = dbTest.getH2Connection();
			break;
		case ORACLE:
			retval = dbTest.getOracleConnection();
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect");
		}
		logger.info("connection is {}", retval);
		return retval;
	}

	
	public static void main(String[] args) throws PropertyVetoException, SQLException, IOException, ParseException, InvalidLoadFileException {
		IntegrationTest it = new IntegrationTest();
		it.fullDialectTest();
	}

}
