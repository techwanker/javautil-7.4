package org.javautil.joblog;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.joblog.installer.H2LoggerDataSource;
//import org.javautil.oracle.trace.formatter.LoggerModels;

public class LoggerModelsTest {

	// @Test Need populate with test data H2LoggerDataSource
	public void test() throws IOException, SQLException {
		DataSource ds = new H2LoggerDataSource().getPopulatedH2FromDbLoggerProperties();
		JoblogModels models = new JoblogModels(ds);
		String model = models.getJobStepInfoJson(1l);
		// TODO create a test with a known trace file
	}
}
