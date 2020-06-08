package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO ensure works with no databases up
public class SqlSplitterTestSqlSemi {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String expected_0 = 
			"--@name job_log_insert\n"
			+ "insert into job_log (\n" + 
			"	job_log_id,     job_token,\n" + 
			"	process_name,   thread_name,\n" + 
			"	status_msg,     start_ts,\n" + 
			"	classname,      module_name\n" + 
			") values (\n" + 
			"	:job_log_id,   :job_token,\n" + 
			"	:process_name, :thread_name,\n" + 
			"   :status_msg,    :start_ts,\n" + 
			"	:classname,    :module_name\n" + 
			")";
	
			


	
	@Test
	public void testSqlSemi() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/logjob_dml.ss.sql").setProceduresOnly(true);
		sr.process();
		List<String> texts = sr.getSqlTexts();
		assertNotNull(texts);
		logger.debug("texts.get(0) {}", texts.get(0));
		assertEquals(7,texts.size());
		logger.debug("expected\n{}\nactual\n{}",expected_0,texts.get(0));
		assertEquals(expected_0,texts.get(0));
	}
	
	
	@Test
	public void testSqlSemiFile() throws IOException, SqlSplitterException {
		SqlSplitter sr = new SqlSplitter(this, "testsr/joblog_drop.sr.sql").setProceduresOnly(true);
		sr.process();
		List<String> texts = sr.getSqlTexts();
		assertNotNull(texts);
		assertEquals(11,texts.size());
		String expected0 =  "drop sequence logger_settings_id_seq";
		logger.debug("texts.get(0) {}", expected0);
		logger.debug("expected\n{}\nactual\n{}",expected0,texts.get(0));
		assertEquals(expected0,texts.get(0));
	}
}

