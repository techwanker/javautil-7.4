package com.pacificdataservices.pdssr;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadConditionIdentification2Test extends BaseTest {
	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void test() throws FileNotFoundException, SQLException, PropertyVetoException {
		Dialect dialect = Dialect.POSTGRES;
		Connection conn = getConnection(dialect);
		LoadConditionIdentification lci = new LoadConditionIdentification(conn, dialect);
		Binds binds = new Binds();
		binds.put("ETL_FILE_ID",1l);
		lci.process(binds,3);
		logger.info("done");
		conn.commit();
	}
}
