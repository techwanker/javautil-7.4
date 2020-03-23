package org.javautil.core.sql;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.javautil.core.collections.PropertiesResolver;
import org.javautil.core.misc.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * . This is a JavaBean-style class with a public, no-arg constructor, but
 * before you use the DataSource, you'll have to be sure to set at least the
 * property
 */

public class DataSourceFactory {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);
	private Map<String, Map<String, Object>> dataSources;
	private String yamlFileName = null;

	public DataSourceFactory() {
		String homeDir = System.getProperty("user.home");
		String yamlName = homeDir + "/connections_java.yaml";
		this.yamlFileName = yamlName;
		try {
			loadDataSources(yamlName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public DataSourceFactory(String yamlName) throws FileNotFoundException {
		this.yamlFileName = yamlName;
		loadDataSources(yamlName);
	}

	@SuppressWarnings("unchecked")
	public DataSourceFactory(InputStream is) {
		Yaml yaml = new Yaml();
		dataSources = (Map<String, Map<String, Object>>) yaml.load(is);
	}

	@SuppressWarnings("unchecked")
	void loadDataSources(String yamlName) throws FileNotFoundException {
		Yaml yaml = new Yaml();
		InputStream ios = new FileInputStream(new File(yamlName));

		// TODO use standard YAML mechanism
		dataSources = (Map<String, Map<String, Object>>) yaml.load(ios);
	}

	public DataSource getDatasource(String dataSourceName) throws PropertyVetoException {
		if (dataSourceName == null) {
			throw new IllegalArgumentException("dataSourceName is null");
		}
		Map<String, Object> parms = dataSources.get(dataSourceName);
		if (parms == null) {
			String message = "no such dataSourceName: '" + dataSourceName + "' in " + this.yamlFileName;
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		return getDatasource(parms);
	}

	static String checkStringParm(String parmName, Map<String, Object> parms) {
		String retval = (String) parms.get(parmName);

		if (retval == null) {
			throw new IllegalArgumentException("required parm " + parmName + " is null in " + parms);
			// logger.warn("required parm " + parmName + " is null in " + parms);
		}
		return retval;
	}

	public DataSourceHelper getDataSourceHelper(DataSource datasource) throws PropertyVetoException {
		Map<String, Object> parms = new HashMap<String, Object>();
		DataSourceHelper dsh = new DataSourceHelper(datasource, null, parms);
		return dsh;
	}

	public DataSourceHelper getDataSourceHelper(String dataSourceName) throws PropertyVetoException {
		Map<String, Object> parms = dataSources.get(dataSourceName);
		DataSource ds = getDatasource(parms);
		DataSourceHelper dsh = new DataSourceHelper(ds, null, parms);
		return dsh;
	}

	public static DataSource getDatasource(Map<String, Object> params) {
		Timer timer = new Timer();
		if (params == null) {
			throw new IllegalArgumentException("parms is null");
		}
		Map<String, Object> resolvedParms = PropertiesResolver.resolveEnvironment(params);
		logger.warn("resolvedParms {}", resolvedParms);
		HikariConfig config = new HikariConfig();
		HikariDataSource ds;
		String driver_class = checkStringParm("driver_class", resolvedParms);
		String url = checkStringParm("url", resolvedParms);
		String user = checkStringParm("username", resolvedParms);
		String password = (String) resolvedParms.get("password");
		config.setAutoCommit(false);
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);
		// TODO don't know why this doesn't work on amazon
		if (url.startsWith("jdbc:postgresql:") && config.getConnectionInitSql() == null) {
			logger.debug("about to set ConnectionTestQuery");
			config.setConnectionTestQuery("select 'x'");
			logger.debug("set ConnectionTestQuery");
		}
		ds = new HikariDataSource(config);
		logger.info("getDatasource obtained millis: " + timer.getElapsedMillis() + " driver_class: " + driver_class
				+ " url: " + url + " user: " + user + " password:" + password);

		return ds;
	}

	public static DataSource getDataSource(String url, String username, String password) {
		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setAutoCommit(false);
		return new HikariDataSource(config);
	}

	@Deprecated
	/*
	 * Use this with caution, the closed connection is still in the connection pool
	 * So you can't assume it's fresh when reopened. If you want a Connection that
	 * destroys on close call H2InMemory.getConnection();
	 * 
	 * @return
	 */
	public static DataSource getInMemoryDataSource() {
		return getDataSource("jdbc:h2:mem:test", "sa", "tutorials");
	}

	public static DataSource getInMemoryDataSourceSingleton() {
		return getDataSource("jdbc:h2:mem", "sa", "tutorials");
	}

	/**
	 * Delayed Database Closing
	 * 
	 * Usually, a database is closed when the last connection to it is closed.
	 * 
	 * In some situations this slows down the application, for example when it is
	 * not possible to keep at least one connection open. The automatic closing of a
	 * database can be delayed or disabled with the SQL statement SET DB_CLOSE_DELAY
	 * seconds.
	 * 
	 * The parameter seconds specifies the number of seconds to keep a database open
	 * after the last connection to it was closed. The following statement will keep
	 * a database open for 10 seconds after the last connection was closed:
	 * 
	 * SET DB_CLOSE_DELAY 10
	 * 
	 * The value -1 means the database is not closed automatically.
	 * 
	 * The value 0 is the default and means the database is closed when the last
	 * connection is closed.
	 * 
	 * This setting is persistent and can be set by an administrator only. It is
	 * possible to set the value in the database URL:
	 * jdbc:h2:~/test;DB_CLOSE_DELAY=10. Don't Close a Database when the VM Exits
	 * 
	 * By default, a database is closed when the last connection is closed. However,
	 * if it is never closed, the database is closed when the virtual machine exits
	 * normally, using a shutdown hook. In some situations, the database should not
	 * be closed in this case, for example because the database is still used at
	 * virtual machine shutdown (to store the shutdown process in the database for
	 * example). For those cases, the automatic closing of the database can be
	 * disabled in the database URL. The first connection (the one that is opening
	 * the database) needs to set the option in the database URL (it is not possible
	 * to change the setting afterwards). The database URL to disable database
	 * closing on exit is:
	 * 
	 * https://h2database.com/html/features.html
	 * 
	 * @return the connection
	 * @throws SQLException an error occured
	 */
	public static Connection getInMemoryDataSourceSingletonConnection() throws SQLException {
		// String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
		// String url = "jdbc:h2:mem:DB_CLOSE_DELAY=0";
		String url = "jdbc:h2:mem:";
		return DriverManager.getConnection(url, "sa", "tutorials");
	}

	public static DataSource getDataSourceSpringProperties(Properties properties) {
		return ApplicationPropertiesDataSource.getDataSource(properties);
	}

	public static DataSource getDataSourceFromEnvironment() {
		return EnvironmentDataSource.getDataSource();
	}

	public static DataSource getH2Permanent(String path, String username, String password) {
		return getDataSource("jdbc:h2:" + path, username, password);
	}

}
