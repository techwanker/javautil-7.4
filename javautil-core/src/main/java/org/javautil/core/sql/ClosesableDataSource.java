package org.javautil.core.sql;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

public class ClosesableDataSource {

	public static void close(DataSource dataSource) {
		try {
			((Closeable) dataSource).close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}