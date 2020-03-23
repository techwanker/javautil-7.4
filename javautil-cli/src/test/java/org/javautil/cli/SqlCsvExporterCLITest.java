package org.javautil.cli;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;

public class SqlCsvExporterCLITest {
	
	@Test
	public void testCustomers() throws SQLException, IOException, ParseException {
		String user = System.getenv("USER");
				
		String[] args  = {"--outfile", "target/etl_customer.csv",
				"--select", "select distinct ship_to_cust_id, cust_nm from etl_customer",
				"--JDBCURL","jdbc:postgresql://localhost/sales_reporting_db",
				"--user", user + "/jjs"};
				
					
		SqlCsvExporterCLI.main(args);
		// TODO check outputk
	}
	

}
