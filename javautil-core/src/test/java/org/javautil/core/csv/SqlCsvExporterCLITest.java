package org.javautil.core.csv;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class SqlCsvExporterCLITest {
	// @Test
	public void test() throws SQLException, IOException, ParseException {
		new File("target/tmp").mkdirs();
		String[] args = { "--outfile", "target/tmp/ic_item_mast.csv", "--select", "select * from ic_item_mast", "--JDBCURL",
		    "jdbc:oracle:thin:@localhost:1521/diamond_pdb", "--user", "topinterfast/diamondmine" };

		SqlCsvExporterCLI.main(args);

		// now to read in the file and metadata and log

	}

}
