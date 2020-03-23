package org.javautil.core.jdbc.metadata;

import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.hibernate.GenerateReveng;
import org.javautil.core.sql.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateRevengTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

//	@Test
	public void test() throws PropertyVetoException, IOException, SQLException {
		logger.info("starting test");
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDatasource("aerospace_oracle");
		Connection conn = ds.getConnection();
		// public GenerateReveng(Connection conn, String catalog, String schemaPattern,
		// String tablePattern) {
		GenerateReveng generator = new GenerateReveng(conn, null, "AEROSPACE", "%");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Writer writer = new OutputStreamWriter(baos);
		generator.write(writer);
		writer.flush();
		logger.info(baos.toString());

	}
}
