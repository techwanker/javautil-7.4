package com.pacificdataservices.pdssr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javautil.conditionidentification.ConditionIdentification;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.MappedResultSetIterator;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.sql.SqlStatements;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class LoadConditionIdentification {

	private final Logger              logger           = LoggerFactory.getLogger(this.getClass());
	private ConditionIdentification   condi;
	private Connection                connection;
	private List<Map<String, Object>> conditionRules;
	private Dialect                   dialect;
	private SqlStatements             sqlStatements;
	String dmlFileName = "src/main/resources/condition-identification/UtConditionDML.yaml";
	public LoadConditionIdentification(Connection conn, Dialect dialect) throws FileNotFoundException, SQLException {
		this.connection = conn;
		connection.setAutoCommit(false);
		this.dialect = dialect;
		logger.info("LoadConditionIdentification dialect " + dialect);
		loadRules();
		condi = new ConditionIdentification(conn, conditionRules, false, dialect, 5);
		sqlStatements = new SqlStatements(new File(dmlFileName),connection);
	}

	@SuppressWarnings("unchecked")
	private void loadRules() throws FileNotFoundException {
		Yaml yaml = new Yaml();
		String yamlName = "src/main/resources/condition-identification/CdsDataloadConditions.yaml";
		File yamlFile = new File(yamlName);
		if (!yamlFile.canRead()) {
			logger.error("unable to read " + yamlName);
		}
		InputStream ios = new FileInputStream(yamlName);
		conditionRules = (List<Map<String, Object>>) yaml.load(ios);
		logger.info("loadRules: {}", conditionRules);
	}
	

	/**
	 * 
	 * @param binds should contain ETL_FILE_ID
	 * @param verbosity nonsense
	 * @throws SQLException hopefully not
	 */
	public void process(Binds binds, int verbosity) throws SQLException {
		long etlFileId = binds.getLong("ETL_FILE_ID");
		logger.info("processing for etl_file_id " + binds.get("ETL_FILE_ID"));
		Long runId = getUtConditionRunId(etlFileId);
		if (runId != null) {
			logger.info("deleting run " + runId);
			deleteRun(runId);
		} else {
			logger.info("no run to delete " + etlFileId);
		}
		condi.process(binds, verbosity);
	}
	

//	public void process(long etlFileId, int verbosity) throws SQLException {
//		logger.info("processing for etl_file_id " + etlFileId);
//		Binds binds = new Binds();
//		binds.put("ETL_FILE_ID", etlFileId);
//		Long runId = getUtConditionRunId(etlFileId);
//		if (runId != null) {
//			logger.info("deleting run " + runId);
//			deleteRun(runId);
//		} else {
//			logger.info("no run to delete " + etlFileId);
//		}
//		condi.process(binds, verbosity);
//	}

	public Long getUtConditionRunId(long etlFileId) throws SQLException {
		SqlStatement sel = new SqlStatement("select ut_condition_run_id \n " + "from ut_condition_run_parm \n"
		    + "where parm_nm = 'ETL_FILE_ID' " + "and parm_value_str = :ETL_FILE_ID --wtf ");
		sel.setConnection(connection);
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID", String.format("%d", etlFileId));
		ListOfNameValue selNv = sel.getListOfNameValue(binds);
		Long	retval = null;
		switch (selNv.size()) {
		case 0:
			break;
		case 1:
			retval = selNv.get(0).getLong("ut_condition_run_id");
			break;
		default:
			throw new IllegalStateException();
		}
		return retval;

	}

	// TODO should get get ListOfNameValues
	public void getStat(SqlStatement stmt) throws SQLException {
		List<NameValue> bindList = new ArrayList<NameValue>();

		Binds binds = new Binds();

		MappedResultSetIterator it = stmt.iterator(binds);
		for (NameValue b : it) {
			bindList.add(b);
		}
		logger.warn("condition row stats: " + stmt.getName() + " " + bindList);

	}

	public void getStats(String message) throws SQLException {
		logger.warn("getting stats " + message);
		getStat(sqlStatements.getSqlStatement("selectRow"));
		getStat(sqlStatements.getSqlStatement("selectStep"));
		getStat(sqlStatements.getSqlStatement("selectParm"));
		getStat(sqlStatements.getSqlStatement("selectRun"));
	}

	private void execute( String nm, Binds binds ) throws SQLException {
		SqlStatement stmt= 	sqlStatements.getSqlStatement(nm);
		stmt.setTrace(true);
		logger.info(stmt.getSql());
		stmt.execute(binds);
	}


	public void deleteRun(long utConditionRunId) throws SQLException {
		Binds binds = new Binds();
		binds.put("ut_condition_run_id", utConditionRunId);
		execute("deleteRow",binds);
		execute("deleteStep",binds);
		execute("deleteParm",binds);
		execute("deleteRun",binds);
	}

	public ArrayList<Long> processAll(int verbosity) throws SQLException {
		SqlStatement loads = new SqlStatement("select etl_file_id from etl_file order by etl_file_id");
		MappedResultSetIterator it = loads.iterator(new Binds());
		int loadCount = 0;
		ArrayList<Long> retval = new ArrayList<Long>();
		for (NameValue nv : it) {
			long etl_file_id = nv.getLong("etl_file_id");
			logger.info("processing " + etl_file_id);
			Binds binds = new Binds();
			binds.put("ETL_FILE_ID",etl_file_id);
			process(binds, verbosity);
			retval.add(etl_file_id);
			loadCount++;
		}
		logger.info("load count " + loadCount);
		return retval;
	}



}