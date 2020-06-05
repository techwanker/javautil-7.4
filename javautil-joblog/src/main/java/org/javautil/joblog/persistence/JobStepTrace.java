package org.javautil.joblog.persistence;

import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.NamedSqlStatements;
import org.javautil.core.sql.SequenceHelper;
import org.javautil.core.sql.SqlStatement;
import org.javautil.lang.ThreadUtil;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobStepTrace  {
		public transient Logger logger = LoggerFactory.getLogger(getClass());
	
		private Connection connection ;

		private SqlStatement upsStatement;
		
		public JobStepTrace(Connection connection) {
			this.connection = connection;
		}

		
public void updateStep(String token) throws SQLException {
		
		String ups = "select tracefile_name from job_log " + "where job_token = :token";

		String upd = "update job_log " + "set tracefile_data =  ?, " + "    tracefile_json =  ? "
				+ "where job_log_id = ?";

		
		logger.info("updating job {}", token);
		if (upsStatement == null) {
			upsStatement = new SqlStatement(connection, ups);
		}
		Binds binds = new Binds();
		binds.put("job_token", token);
		NameValue upsRow = upsStatement.getNameValue(binds, true);
		//
		//		String traceFileName = upsRow.getString("tracefile_name");
		//		if (traceFileName == null) {
		//			logger.warn("tracefileName is null");
		//			//throw new IllegalStateException("traceFileName is null");
		//		} else {
		//			//
		//			//
		//			// TODO this is reading directly from the file 
		//			Clob clob = connection.createClob();
		//			String tracefileData = null;
		//			try {
		//				tracefileData = FileUtil.getAsString(traceFileName);
		//			} catch (IOException e) {
		//				logger.error(e.getMessage());
		//			}
		//			clob.setString(1, tracefileData);
		//			//
		//			Clob jsonClob = connection.createClob();


		//			OracleTraceProcessor tfr;
		//			try {
		//				tfr = new OracleTraceProcessor(connection, traceFileName);
		//				tfr.process();
		//				CursorsStats cursorStats = tfr.getCursors();
		//				String jsonString = cursorStats.toString();
		//				jsonClob.setString(1, jsonString);
		//
		//				PreparedStatement updateTraceFile = connection.prepareStatement(upd);
		//
		//				updateTraceFile.setClob(1, clob);
		//				updateTraceFile.setClob(2, jsonClob);
		//				updateTraceFile.setLong(3, jobId);
		//				int count = updateTraceFile.executeUpdate();
		//
		//				binds.put("tracefile_data", clob);
		//				if (count != 1) {
		//					throw new IllegalArgumentException("unable to update job_log_id " + jobId);
		//				}
		//				logger.info("updated {}", jobId);
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//				logger.error(e.getMessage());
		//			}
		//		}
	}



}
