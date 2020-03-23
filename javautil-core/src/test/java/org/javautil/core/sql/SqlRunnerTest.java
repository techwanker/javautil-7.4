package org.javautil.core.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;


//// This doesn't work
//look at package org.javautil.cli;
//public class SqlCsvExporterCLITest
//It works
//look at javautil-commandline/README.md



public class SqlRunnerTest {


	@Ignore
	@Test
	public void testMain() throws SQLException, IOException, ParseException {
			String[] args  = {"--datasource", "target/user_tab_col_comments.csv",
					"--statementsFile", "src/test/resources/h2/sales_reporting_ddl.sr.sql",
					"--JDBCURL","jdbc:h2:mem",
					"--user","sr/tutorial"};
					
						
			SqlRunner.main(args);
			// TODO check outputk
	}
}
