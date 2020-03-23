package org.javautil.core.jdbc.metadata.dao;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.core.json.JsonSerializerJackson;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaDaoJdbcTest {

	private Connection            conn;
	private DataSourceFactory     dsf    = new DataSourceFactory();
	private DataSource            ds;
	private SchemaDaoJdbc         schema;
	private Logger                logger = LoggerFactory.getLogger(getClass());
	// private ModelGsonMarshaller dillon = new ModelGsonMarshaller();
	private JsonSerializerJackson dillon = new JsonSerializerJackson();

	// @Test
	public void test() throws PropertyVetoException, SQLException, IOException {
		ds = dsf.getDatasource("aerodemo-ora");
		conn = ds.getConnection();
		schema = new SchemaDaoJdbc(conn, "AERODEMO", "%", "%");
		schema.populateTables();
		logger.info("schema is: {}", schema);
		String json = dillon.toJsonPretty(schema);
		logger.info("\n{}", json);
		IOUtils.writeString(new File("target/tmp/SchemaDocJdbcTest.json"), json);
		// schema.writeJsonSchema(new File("target/tmp/SchemaDocJdbcTest2.json"));
		conn.close();

	}

	// @Test
	public void test2() throws PropertyVetoException, SQLException, IOException {
		ds = dsf.getDatasource("aerodemo-ora");
		conn = ds.getConnection();
		schema = new SchemaDaoJdbc(conn, "AERODEMO", "%", "%");
		schema.populateTables();
		// IOUtils.writeString(new File("target/tmp/SchemaDocJdbcTest.json"),json);
		schema.writeJsonSchema(new File("target/tmp/SchemaDocJdbcTest2.json"));
		conn.close();

	}
}
