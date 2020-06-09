package com.pacificdataservices.pdssr;

import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import javax.sql.DataSource;

import org.javautil.core.sql.TestDataSource;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlSplitterException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSchemaTest  {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Dialect dialect = Dialect.ORACLE;
    
    @Test
    public void ahum() {
    	
    }
 	@Test
 	public void test1() throws SqlSplitterException, Exception {
 	 	DataSource ds = TestDataSource.getDataSource(Dialect.ORACLE);
 		Connection conn = ds.getConnection(); 
 		CreateSchema cs = new CreateSchema(conn,dialect);
 		cs.process();
 		conn.close();
 		((Closeable) ds).close();
 	}
}
