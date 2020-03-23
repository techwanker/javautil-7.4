package org.javautil.core.sql;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.javautil.io.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ApplicationPropertiesDataSource {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationPropertiesDataSource.class);

	public ApplicationPropertiesDataSource() {
		super();
	}

	private static String getNotNull(Properties properties, String propertyName) {
		final String retval = properties.getProperty(propertyName);
		if (retval == null) {
			throw new IllegalArgumentException("No such property: " + propertyName);
		}
		return retval;
	}

	public DataSource getDataSource(File file) throws IOException {
		final Properties properties = new Properties();
		InputStream input = new FileInputStream(file);
		properties.load(input);
		input.close();
		return getDataSource(properties);
	}

	public DataSource getDataSource(Object invoker, String resourceName) throws IOException {
		final Properties properties = new Properties();
		InputStream input = ResourceHelper.getResourceAsInputStream(invoker, resourceName);
		properties.load(input);
		input.close();
		return getDataSource(properties);
	}

	public static DataSource getDataSource(Properties properties) {
		final HikariConfig config = new HikariConfig();
		try {
			config.setJdbcUrl(getNotNull(properties, "spring.datasource.url"));
			config.setUsername(getNotNull(properties, "spring.datasource.username"));
			config.setPassword(getNotNull(properties, "spring.datasource.password"));
			String maximumPoolSizeString = properties.getProperty("maximum_pool_size");
			if (maximumPoolSizeString != null) {
				int maximumPoolSize = Integer.parseInt(maximumPoolSizeString);
				logger.info("setting maximumPoolSize to " + maximumPoolSize);
				config.setMaximumPoolSize(maximumPoolSize);
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			properties.list(ps);
			ps.flush();

			String message = String.format("error: %s\n%s", e.getMessage(), baos.toString());
			throw new RuntimeException(message);
		}
		config.setAutoCommit(false);
		return new HikariDataSource(config);
	}

	public Properties getApplicationProperties() throws IOException {
		InputStream input = null;

		final Properties properties = new Properties();
		try {
			input = ResourceHelper.getResourceAsInputStream(this, "application.properties");
			properties.load(input);
		} catch (final IOException e) {
			if (input != null) {
				input.close();
			}
			throw new RuntimeException(e);
		}

		getNotNull(properties, "spring.datasource.driver-class-name");
		getNotNull(properties, "spring.datasource.url");
		getNotNull(properties, "spring.datasource.username");
		getNotNull(properties, "spring.datasource.password");
		input.close();
		return properties;
	}

	public DataSource getDataSource() throws IOException {
		return getDataSource(getApplicationProperties());
	}
}
