package com.pacificdataservices.pdssr;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.javautil.core.sql.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest extends DbTest {
	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	public Connection getConnection(Dialect dialect) throws SQLException, FileNotFoundException, PropertyVetoException {
		Connection retval;
		switch (dialect) {
		case POSTGRES:
			retval = getPostgresConnection();
			break;
		case H2:
			retval = getH2Connection();
			break;
		default:
			throw new IllegalArgumentException("unsupported dialect");
		}
		logger.info("connection is {}", retval);
		return retval;
	}

}
