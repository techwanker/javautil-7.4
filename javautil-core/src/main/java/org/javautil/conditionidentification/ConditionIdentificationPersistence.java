package org.javautil.conditionidentification;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.sql.SqlStatements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionIdentificationPersistence {
	private final Logger  logger  = LoggerFactory.getLogger(this.getClass());

	/**
	 * Runs a series of sql statements that return as the first column, the primary
	 * key of a table being examined an 0 or more fields used in formatting a
	 * message.
	 * 
	 * https://dzone.com/articles/java-string-format-examples
	 */

	private Connection    connection;

	private SqlStatements sqlStatements;

	private boolean       showSql = true;

	public ConditionIdentificationPersistence() {

	}

	public ConditionIdentificationPersistence(final Connection conn, final boolean showSqlStatementsOnLoad,
	    final boolean showSql) {
		this.connection = conn;
		try {
			sqlStatements = loadDml();
			sqlStatements.setShowStatementsOnLoad(showSqlStatementsOnLoad);
			sqlStatements.setShowSqlAndBindsOnExecute(showSql);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SqlStatements loadDml() throws IOException {
		InputStream persistenceYamlStream = this.getClass().getResourceAsStream("UtConditionPersistenceDml.yaml");
		SqlStatements sqlStatements = new SqlStatements(persistenceYamlStream, connection);
		persistenceYamlStream.close();
		return sqlStatements;
	}

	/**
	 * 
	 * 
	 * @param binds
	 * @return utConditionRunId
	 * @throws SQLException
	 */
	long startRun(Binds binds) throws SQLException {
		java.sql.Date start_ts = new java.sql.Date(System.currentTimeMillis());
		binds.put("start_ts", start_ts);

		SqlStatement utConditionRunIdSeqSS = sqlStatements.getSqlStatement("ut_condition_run_id_seq");
		utConditionRunIdSeqSS.setConnection(connection);
		long utConditionRunId = utConditionRunIdSeqSS.nextval();
		binds.put("ut_condition_run_id", utConditionRunId);
		logger.debug("startRun binds: {}", binds);

		SqlStatement stmt = sqlStatements.getSqlStatement("ut_condition_run_insert");
		stmt.setConnection(connection); // TODO should in SqlStatements
		logger.debug("startRun about to execute:\n{}\nbinds: {}", stmt.getSql(), binds);
		stmt.execute(binds);

		logger.debug("inserted ut_condition_run sql:\n{}\nbinds: {}", stmt.getSql(), binds);

		runParmsInsert(utConditionRunId, binds);
		return utConditionRunId;
	}

	void runParmsInsert(long utConditionRunId, Binds binds) throws SQLException {
		SqlStatement sh = sqlStatements.getSqlStatement("ut_condition_run_parm_insert");
		sh.setConnection(connection);
		for (Entry<String, Object> e : binds.entrySet()) {
			Binds b = new Binds();
			b.put("UT_CONDITION_RUN_ID", utConditionRunId);
			b.put("PARM_NM", e.getKey());
			b.put("PARM_VALUE", e.getValue().toString());
			b.put("PARM_TYPE", e.getValue().getClass().getName());
			sh.executeUpdate(b);
			logger.info("Inserted parms sql: \n{}\nbinds: {}", sh.getSql(), binds);
		}
	}

	long utConditionRunStepInsert(long utConditionRunId, long beginNanos, ConditionRule rule) throws SQLException {
		logger.debug("utConditionRunStepInsert: utConditionRunId {} rule: {}", utConditionRunId, rule);
		Binds binds = new Binds();

		SqlStatement utConditionRunStepIdSs = sqlStatements.getSqlStatement("ut_condition_run_step_id_seq");
		utConditionRunStepIdSs.setConnection(connection);
		long utConditionRunStepId = utConditionRunStepIdSs.nextval();
		binds.put("UT_CONDITION_RUN_STEP_ID", utConditionRunStepId);

		SqlStatement utConditionRunStepInsertSs = sqlStatements.getSqlStatement("ut_condition_run_step_insert");
		utConditionRunStepInsertSs.setConnection(connection);

		long utConditionId = utConditionInsert(rule);

		binds.put("UT_CONDITION_ID", utConditionId);
		binds.put("UT_CONDITION_RUN_ID", utConditionRunId);

		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
		binds.put("START_TS", now);

		logger.debug("utConditionRunStepInsert: sql:\n{}\nbinds: {}", utConditionRunStepInsertSs.getSql(), binds);
		utConditionRunStepInsertSs.execute(binds);
		return utConditionRunStepId;
	}

	long utConditionInsert(ConditionRule rule) throws SQLException {
		// get the id from the sequence
		SqlStatement utConditionIdSeqSs = sqlStatements.getSqlStatement("ut_condition_id_seq");
		utConditionIdSeqSs.setConnection(connection);
		long utConditionId = utConditionIdSeqSs.nextval();

		// query
		SqlStatement sel = sqlStatements.getSqlStatement("ut_condition_select");
		sel.setConnection(connection);
		ResultSet rset = sel.executeQuery(rule.getBinds());
		// insert
		SqlStatement utConditionInsertSs = sqlStatements.getSqlStatement("ut_condition_insert");
		utConditionInsertSs.setConnection(connection);

		Binds binds = rule.getBinds();
		binds.put("ut_condition_id", utConditionId);
		utConditionInsertSs.execute(binds);
		// TODO add a means to log this in SqlStatement such as logSql
		logger.debug("utConditionInsert: sql:\n{}\nbinds: {}", utConditionInsertSs.getSql(), binds);

		return utConditionId;
	}

	void utConditionRowMsgInsert(long runStepId, long pk, String formattedString) throws SQLException {
		SqlStatement sh = sqlStatements.getSqlStatement("ut_condition_row_msg_insert");
		sh.setConnection(connection);
		Binds binds = new Binds();
		binds.put("UT_CONDITION_RUN_STEP_ID", runStepId);
		binds.put("PRIMARY_KEY", pk);
		binds.put("MSG", formattedString);
		sh.executeUpdate(binds);

	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
		if (sqlStatements != null) {
			sqlStatements.setShowSqlAndBindsOnExecute(showSql);
		}
	}
}