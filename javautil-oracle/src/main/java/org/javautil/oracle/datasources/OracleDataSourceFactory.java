package org.javautil.oracle.datasources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.sql.DataSource;

import org.javautil.core.text.StringBuilderHelper;
import org.javautil.jdbc.datasources.DataSourceInstantiationException;
import org.javautil.properties.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.jdbc.pool.OracleDataSource;

/**
 * @author jjs@dbexperts.com
 * 
 *         todo jjs where did all of the unit tests go? todo create an
 *         OracleDataSourceWrapperArguments class
 * 
 *         look at package
 *         com.dbexperts.oracle.datasource.OracleDataSourceWrapperValues
 */
@SuppressWarnings("boxing")
public class OracleDataSourceFactory

{
	public static final String AbandonedConnectionTimeout = "AbandonedConnectionTimeout";

	public static final String ConnectionWaitTimeout = "ConnectionWaitTimeout";

	public static final String InitialLimit = "InitialLimit";

	public static final String InactivityTimeout = "InactivityTimeout";

	public static final String LowerThresholdLimit = "LowerThresholdLimit";

	public static final String MaxLimit = "MaxLimit";

	public static final String MaxStatementsLimit = "MaxStatementsLimit";

	public static final String MinLimit = "MinLimit";

	public static final String PropertyCheckInterval = "PropertyCheckInterval";

	public static final String TimeToLiveTimeout = "TimeToLiveTimeout";

	public static final String ValidateConnection = "ValidateConnection";

	// following properties are set through setters and getters
	public static final String ServerName = "ServerName";

	public static final String PortNumber = "PortNumber";

	public static final String User = "User";

	public static final String ConnectionCachingEnabled = "ConnectionCachingEnabled";

	public static final String ServiceName = "ServiceName";

	public static final String LoginTimeout = "LoginTimeout";
	private static String newline = System.getProperty("line.separator");

	private Properties props;

	private static final String PORT_NUMBER_MESSAGE = "Port Number must be a valid IP port, is usually 1521 or 1526";

	private static final TreeMap<String, Boolean> cachePropertyType = new TreeMap<String, Boolean>() {
		{
			put(AbandonedConnectionTimeout, true);
			put(ConnectionWaitTimeout, true);
			put(InitialLimit, true);
			put(InactivityTimeout, true);
			put(LowerThresholdLimit, true);
			put(MaxLimit, true);
			put(MaxStatementsLimit, true);
			put(MinLimit, true);
			put(PropertyCheckInterval, true);
			put(TimeToLiveTimeout, true);
			put(ValidateConnection, true);
			put(ConnectionCachingEnabled, false);
			put(LoginTimeout, true);
		}
	};

	// todo document every one of these fields in one location
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private OracleDataSource ds;

	// todo wrap this with an AuthenticatingDataSource
	// private final WeakHashMap<String, String> useridByTicket = new
	// WeakHashMap<String, String>();

	private Object lock = null;
	private final Properties connectionProperties = new Properties();

	private final Properties cacheProperties = new Properties();

	private Class<OracleConnectionCacheCallback> callback;

	// private boolean allowUnauthenticatedConnectionRequests = true;

	private String name;

	private File configurationFile;

	private OracleDataSourceWrapperArguments arguments;

	//
	//
	//

	/**
	 * 
	 * 
	 */

	public OracleDataSourceFactory() throws SQLException {
		ds = new OracleDataSource();
		ds.setDriverType("thin");
		ds.setConnectionProperties(connectionProperties);
	}

	// TODO need the ability to validate a set Argument bean other than
	public void setArguments(OracleDataSourceWrapperArguments arguments) throws SQLException {
		this.arguments = arguments;
		setAbandonedConnectionTimeout(arguments.getAbandonedConnectionTimeout());
		setConnectionCacheName(arguments.getConnectionCacheName());
		setConnectionCachingEnabled(arguments.getConnectionCachingEnabled());
		setConnectionWaitTimeout(arguments.getConnectionWaitTimeout());
		setInactivityTimeout(arguments.getInactivityTimeout());
		if (arguments.getInitialLimit() != null) {
			setInitialLimit(arguments.getInitialLimit());
		}
		if (arguments.getLoginTimeout() != null) {
			setLoginTimeout(arguments.getLoginTimeout());
		}
		if (arguments.getLowerThresholdLimit() != null) {
			setLowerThresholdLimit(arguments.getLowerThresholdLimit());
		}
		if (arguments.getMaxLimit() != null) {
			setMaxLimit(arguments.getMaxLimit());
		}
		setMaxLimit(arguments.getMaxLimit());
		if (arguments.getMinLimit() != null) {
			setMinLimit(arguments.getMinLimit());
		}
		setPassword(arguments.getPassword());
		setPortNumber(arguments.getPortNumber());
		setServerName(arguments.getServerName());
		setServiceName(arguments.getServiceName());
		setTimeToLiveTimeout(arguments.getTimeToLiveTimeout());
		setUser(arguments.getUser());
	}

	public ArrayList<String> getValidateMessages() {
		final ArrayList<String> messages = new ArrayList<String>();
		final String driverType = ds.getDriverType();
		if (!("thin".equals(driverType) || "oci".equals(driverType))) {
			messages.add("driverType: '" + driverType + "' is not 'thin' or 'oci'");
		}
		if (!(ds.getPortNumber() == 1521 || ds.getPortNumber() == 1526)) {
			messages.add("non standard port (1521,1526) : " + ds.getPortNumber());
		}
		if (ds.getServiceName() == null) {
			messages.add("serviceName is null");
		}
		if (ds.getTNSEntryName() == null && "oci".equals(driverType)) {
			messages.add("driverType is 'oci' and TNSEntryName is null");
		}
		if (ds.getServerName() == null && ds.getTNSEntryName() == null) {
			messages.add("serverName is null " + " for driverType " + driverType);
		}
		return messages;

	}

	/**
	 * @return the configurationFile
	 */
	public File getConfigurationFile() {
		return configurationFile;
	}

	public OracleDataSource getDataSource() {
		return ds;
	}

	public int getIntProperty(final String propertyName) throws SQLException {
		final Boolean isCacheProperty = cachePropertyType.get(propertyName);
		if (isCacheProperty == null) {
			throw new IllegalArgumentException("unknown  property  '" + propertyName + "'");
			// todo add known property dump
		}
		final Properties p = isCacheProperty ? ds.getConnectionCacheProperties() : ds.getConnectionProperties();

		String val;
		Integer retval = null;
		if (p != null) {
			val = p.getProperty(propertyName);
			retval = Integer.parseInt(val);
		}

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getLoginTimeout()
	 */

	/**
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getMaxLimit()
	 */
	public int getMaxLimit() throws SQLException {
		return getIntProperty(MaxLimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#getMaxStatementsLimit
	 * ()
	 */
	public int getMaxStatementsLimit() throws SQLException {
		return getIntProperty(MaxStatementsLimit);
	}

	/**
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getMinLimit()
	 */
	public int getMinLimit() throws SQLException {
		return getIntProperty("MinLimit");
	}

	public DataSource getOds() {
		return ds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#getOracleDataSource()
	 */
	public DataSource getOracleDataSource() {
		return ds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getPortNumber()
	 */
	public int getPortNumber() {
		return ds.getPortNumber();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#
	 * getPropertyCheckInterval ()
	 */
	public int getPropertyCheckInterval() throws SQLException {
		return getIntProperty(PropertyCheckInterval);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getServerName()
	 */
	public String getServerName() {
		return ds.getServerName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getServiceName()
	 */
	public String getServiceName() {
		return ds.getServiceName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#getTimeToLiveTimeout
	 * ()
	 */
	public int getTimeToLiveTimeout() throws SQLException {
		return getIntProperty(TimeToLiveTimeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#getUser()
	 */
	public String getUser() {
		return ds.getUser();
	}

	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return ds.isWrapperFor(iface);

	}

	public void setAbandonedConnectionTimeout(final Integer abandonedConnectionTimeout) throws SQLException {
		if (abandonedConnectionTimeout == null) {
			logger.warn("abandonedConnectionTimeout was null");
		} else {
			setProperty(AbandonedConnectionTimeout, abandonedConnectionTimeout.toString());
		}
	}

	// public void setAllowUnauthenticatedConnectionRequests(final Object
	// lockKey, final boolean value) {
	// validateLockKey(lockKey);
	// allowUnauthenticatedConnectionRequests = value;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setValidateConnection
	 * (boolean)
	 */

	public void setCallback(final String connectionCallbackClassname) throws DataSourceInstantiationException {
		final Class<OracleConnectionCacheCallback> callbackClass = null;
		try {
			final Class c = Class.forName(connectionCallbackClassname);
			final Object o = c.newInstance();
			if (!(o instanceof OracleConnectionCacheCallback)) {
				throw new DataSourceInstantiationException(o.getClass().getName()
						+ " is not an instance of oracle.jdbc.pool.OracleConnectionCacheCallback");
			}
			callback = callbackClass;
		} catch (final ClassNotFoundException e) {
			throw new DataSourceInstantiationException("cannot locate class " + connectionCallbackClassname);
		} catch (final Exception e) {
			throw new DataSourceInstantiationException(e.getMessage() + " on class " + connectionCallbackClassname);
		}

	}

	/**
	 * @param configurationFile
	 *            the configurationFile to set
	 */
	public void setConfigurationFile(final File configurationFile) {
		this.configurationFile = configurationFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setConnectionCacheName
	 * (java.lang.String)
	 */
	public void setConnectionCacheName(final String connectionCacheName) throws SQLException {
		ds.setConnectionCacheName(connectionCacheName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.javautil.oracle.datasource.ODSWrapperInterface#
	 * setConnectionCachingEnabled(boolean) todo why is this here?
	 */
	public void setConnectionCachingEnabled(final boolean connectionCachingEnabled) throws SQLException {
		ds.setConnectionCachingEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#
	 * setConnectionWaitTimeout (int)
	 */
	public void setConnectionWaitTimeout(final Integer connectionWaitTimeout) throws SQLException {
		setProperty(ConnectionWaitTimeout, connectionWaitTimeout);
	}

	public void setInactivityTimeout(final Integer inactivityTimeout) throws SQLException {
		setPropertyGreaterThanOrEqualToOne(InactivityTimeout, inactivityTimeout);

	}

	/**
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#setInitialLimit(java.lang.Integer)
	 */
	public void setInitialLimit(final Integer initialLimit) throws SQLException {
		if (initialLimit != null) {
			final int lim = initialLimit.intValue();
			if (lim < 0) {
				throw new IllegalArgumentException("initialLimit must be >= 0 not: " + lim);
			}
			setProperty(InitialLimit, lim);

		}
	}

	public void setLock(final Object _lock) {
		if (_lock == null) {
			throw new IllegalArgumentException("_lock is null");
		}
		if (lock != null && _lock != lock) {
			throw new IllegalArgumentException("lock is already set invalid key passed " + _lock + " expected " + lock);
		}
		lock = _lock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setLoginTimeout(int)
	 */
	public void setLoginTimeout(final int loginTimeout) {
		ds.setLoginTimeout(loginTimeout);
	}

	public void setLoginTimeout(final Integer loginTimeout) {
		ds.setLoginTimeout(loginTimeout);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.javautil.oracle.datasource.ODSWrapperInterface#
	 * setAbandonedConnectionTimeout(int)
	 */

	public void setLogWriter(final PrintWriter out) {
		ds.setLogWriter(out);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setLowerThresholdLimit
	 * (int)
	 */
	public void setLowerThresholdLimit(final int lowerLimit) throws SQLException {
		if (lowerLimit < 0) {
			throw new IllegalArgumentException("lowerThresholdLimit must be >0 not: " + lowerLimit);
		}
		setProperty(LowerThresholdLimit, lowerLimit);
	}

	public void setLowerThresholdLimit(final Integer lowerLimit) throws SQLException {
		setProperty(LowerThresholdLimit, lowerLimit);

	}

	public void setMaxLimit(final Integer maxLimit) throws SQLException {
		setPropertyGreaterThanOrEqualToOne(MaxLimit, maxLimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setMaxStatementsLimit
	 * (java.lang.Integer)
	 */
	public void setMaxStatementsLimit(final Integer maxStatementsLimit) throws SQLException {
		if (maxStatementsLimit != null) {
			final int val = maxStatementsLimit.intValue();
			if (val < 1) {
				throw new IllegalArgumentException("maxStatementLimit must be >= 1 not: " + val);
			}
			setProperty(MaxStatementsLimit, maxStatementsLimit);
		}

	}

	/**
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#setMinLimit(int)
	 */
	public void setMinLimit(final Integer minLimit) throws SQLException {
		if (minLimit != null) {
			final int val = minLimit.intValue();

			if (val < 0) {
				throw new IllegalArgumentException("minLimit must be >= 0 not: " + val);
			}
			setProperty(MinLimit, val);
		}
	}

	/**
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#setOds(oracle.jdbc.pool.OracleDataSource)
	 */
	public void setOds(final OracleDataSource ods) {
		this.ds = ods;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#setPassword(java.
	 * lang.String)
	 */
	public void setPassword(final String password) {
		ds.setPassword(password);
	}

	public void setPortNumber(final String portNumber) {
		int pn;

		try {
			pn = Integer.parseInt(portNumber);
		} catch (final NumberFormatException nfe) {
			throw new IllegalArgumentException(PORT_NUMBER_MESSAGE + " " + portNumber);
		}
		setPortNumber(pn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setPortNumber(int)
	 */
	public void setPortNumber(final int portNumber) {
		ds.setPortNumber(portNumber);
	}

	// public void setPortNumber(final Integer portNumber) {
	// if (portNumber == null) {
	// throw new IllegalArgumentException("portNumber may not be null");
	// }
	// ds.setPortNumber(portNumber.intValue());
	//
	// }

	private void setProperty(final String propertyName, final boolean value) throws SQLException {
		setProperty(propertyName, Boolean.toString(value));

	}

	private void setProperty(final String propertyName, final int value) throws SQLException {
		setProperty(propertyName, Integer.toString(value));

	}

	private void setProperty(final String propertyName, final Integer value) throws SQLException {
		if (value != null) {
			setProperty(propertyName, value.toString());
		}

	}

	private void setProperty(final String propertyName, final String value) throws SQLException {
		final Boolean isCacheProperty = cachePropertyType.get(propertyName);
		if (isCacheProperty == null) {
			throw new IllegalArgumentException("unmapped property type " + propertyName);
		}
		if (value != null) {

			if (isCacheProperty.booleanValue()) {
				cacheProperties.put(propertyName, value);
				ds.setConnectionCacheProperties(cacheProperties);
				logger.debug("setting connectionCacheProperty " + propertyName + " to " + value);
			} else {
				connectionProperties.put(propertyName, value);
				ds.setConnectionProperties(connectionProperties);
				logger.debug("setting connectionProperty " + propertyName + " to " + value);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#
	 * setPropertyCheckInterval (int)
	 */
	public void setPropertyCheckInterval(final Integer propertyCheckInterval) throws SQLException {
		setProperty(PropertyCheckInterval, propertyCheckInterval);
	}

	public void setPropertyGreaterThanOrEqualToOne(final String propertyName, final Integer value) throws SQLException {
		if (value != null) {
			final int val = value.intValue();
			if (val < 1) {
				throw new IllegalArgumentException("value must be >= 1 not: " + val);
			}
			setProperty(propertyName, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setServerName(java
	 * .lang.String)
	 */
	public void setServerName(final String serverName) {
		ds.setServerName(serverName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.oracle.datasource.ODSWrapperInterface#setServiceName(java
	 * .lang.String)
	 */
	public void setServiceName(final String serviceName) {
		ds.setServiceName(serviceName);
	}

	public void setTimeToLiveTimeout(final Integer value) throws SQLException {
		setPropertyGreaterThanOrEqualToOne(TimeToLiveTimeout, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.oracle.datasource.ODSWrapperInterface#setUser(java.lang
	 * .String)
	 */
	public void setUser(final String user) {
		ds.setUser(user);
	}

	// public void setName(final String name) {
	// ds.setName(name);
	// }

	public void setUseThinConnectionType(final boolean val) {
		ds.setDriverType(val ? "thin" : "oci");
	}

	// /**
	// * No facility is provided for using any other procedure as this would
	// allow somebody to pass a parm
	// * and bypass the DBA procedure by writing their own that authenticates
	// anybody, or records passwords.
	// * @param userName
	// * @param password
	// * @return
	// * @throws SQLException
	// */
	// // public String authenticateUser(String userName, String password)
	// throws SQLException {
	//
	// }

	public void setValidateConnection(final boolean validate) throws SQLException {
		setProperty(ValidateConnection, validate);
	}

	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return ds.unwrap(iface);

	}

	private void validateLockKey(final Object key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (lock == null) {
			throw new UnsupportedOperationException("not locked must be locked with call to setLock previously");
		}
		if (lock != key) {
			throw new IllegalArgumentException("key does not match lock");
		}
	}

	public String getUrl() throws NamingException {
		return getUrl(ds);
	}

	public static String getUrl(final OracleDataSource ds) {
		Reference reference;
		String retval;
		try {
			reference = ds.getReference();
			retval = reference.get("url").toString();
		} catch (final NamingException e) {
			retval = e.getMessage();
		}

		return retval;

	}

	public static String asString(final OracleDataSource ds) throws SQLException {
		final StringBuilder b = new StringBuilder();

		// todo should be text name value helper,where did I put that

		// todo reuse
		final StringBuilderHelper sbh = new StringBuilderHelper(b);
		b.append("Connection Values" + newline);
		sbh.addNameValue("DatabaseName", ds.getDatabaseName());
		sbh.addNameValue("DataSourceName", ds.getDataSourceName());
		sbh.addNameValue("DriverType", ds.getDriverType());
		sbh.addNameValue("ExplicitCachingEnabled", ds.getExplicitCachingEnabled());
		sbh.addNameValue("ImplicitCachingEnabled", ds.getImplicitCachingEnabled());
		sbh.addNameValue("MaxStatements", ds.getMaxStatements());
		sbh.addNameValue("NetworkProtocol", ds.getNetworkProtocol());
		sbh.addNameValue("PortNumber", ds.getPortNumber());
		sbh.addNameValue("User", ds.getUser());
		sbh.addNameValue("ServerName", ds.getServerName());
		sbh.addNameValue("TNSEntryName", ds.getTNSEntryName());
		b.append("Cache Values" + newline);
		sbh.addNameValue("ConnectionCacheName", ds.getConnectionCacheName());
		sbh.addNameValue("ConnectionCachingEnabled", ds.getConnectionCachingEnabled());
		sbh.addNameValue("Description", ds.getDescription());
		sbh.addNameValue("LogWriter", ds.getLogWriter() == null);
		sbh.addNameValue("ONSConfiguration", ds.getONSConfiguration());
		sbh.addNameValue("url: ", getUrl(ds));
		sbh.addNameValue("FastConnectionFailoverEnabled", ds.getFastConnectionFailoverEnabled());
		sbh.addNameValue("LoginTimeout", ds.getLoginTimeout());
		b.append(PropertiesHelper.asText("CacheProperties", ds.getConnectionCacheProperties()));
		b.append(PropertiesHelper.asText("ConnectionProperties", ds.getConnectionProperties()));
		return b.toString();

	}

	public void close() throws SQLException {
		ds.close();

	}

	void setPropertiesFromFilename(String fileName) throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		properties.load(fis);
		fis.close();

	}

	void initialize() {

		for (final Object propertyNameObject : props.keySet()) {
			final String propertyName = (String) propertyNameObject;
			final String value = (String) props.get(propertyName);

			final Boolean isCacheProperty = cachePropertyType.get(propertyName);
			if ("password".equalsIgnoreCase(propertyName)) {
				ds.setPassword(value);
			} else if ("user".equalsIgnoreCase(propertyName)) {
				ds.setUser(value);
			} else if ("PortNumber".equalsIgnoreCase(propertyName)) {
				setPortNumber(value);
			} else if ("ServerName".equalsIgnoreCase(propertyName)) {
				setServerName(value);
			} else if ("ServiceName".equalsIgnoreCase(propertyName)) {
				setServiceName(value);
			}

			else if (isCacheProperty == null) {

				throw new IllegalArgumentException("unknown property type " + propertyName);
			} else {
				if (value != null) {

					if (isCacheProperty.booleanValue()) {
						cacheProperties.put(propertyName, value);
						try {
							ds.setConnectionCacheProperties(cacheProperties);
						} catch (final SQLException e) {
							throw new RuntimeException(e);
						}
						// System.out.println("setting connectionCacheProperty "
						// + propertyName + " to " + value);
						logger.debug("setting connectionCacheProperty " + propertyName + " to " + value);
					} else {
						connectionProperties.put(propertyName, value);
						try {
							ds.setConnectionProperties(connectionProperties);
						} catch (final SQLException e) {
							throw new RuntimeException(e);
						}
						// System.out.println("setting connectionProperty "
						// + propertyName + " to " + value);
						logger.debug("setting connectionProperty " + propertyName + " to " + value);
					}
				} else {
					throw new IllegalArgumentException("no value for property '" + propertyName + "'");
				}
			}
		}
	}

	public void processArguments(final OracleDataSourceWrapperArguments args)
			throws SQLException, DataSourceInstantiationException {
		// setAbandonedConnectionCallback(args.g)
		setAbandonedConnectionTimeout(args.getAbandonedConnectionTimeout());
		setCallback(args.getCallbackClassName());
		setConnectionCacheName(args.getDatasourceName());
		setConnectionCachingEnabled(true);
		setConnectionWaitTimeout(args.getConnectionWaitTimeout());
		setInactivityTimeout(args.getInactivityTimeout());
		setInitialLimit(args.getInitialLimit());
		setLock(args);
		setLoginTimeout(args.getLoginTimeout());
		setLogWriter(args.getLogWriter());
		setLowerThresholdLimit(args.getLowerThresholdLimit());
		setMaxLimit(args.getMaxLimit());
		setMaxStatementsLimit(args.getMaxStatementsLimit());
		setMinLimit(args.getMinLimit());
		// setName(args.getDatasourceName());
		setPassword(args.getPassword());
		setPortNumber(args.getPortNumber());
		setServerName(args.getServiceName());
		setServiceName(args.getServiceName());

	}

}
