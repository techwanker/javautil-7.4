package org.javautil.core.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javautil.core.json.JsonSerializer;
import org.javautil.core.json.JsonSerializerGson;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DatasetCrosstabber;
import org.javautil.dataset.DatasetMetadataImpl;
import org.javautil.dataset.ListOfNameValueDataset;
import org.javautil.dataset.MatrixDataset;
import org.javautil.document.crosstab.CrosstabColumns;
import org.javautil.util.ListOfLists;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

// TODO convert StatementHelper and SqlStatement
// (?!\\B'[^']*)(:\\w+)(?![^']*'\\B)
// TODO need a NameValueNoCheck that doesn't look to  see if too many

/**
 * SqlStatement simplifies SQL by obviating the need to deal with JDBC.
 * 
 * Features:
 * <ul>
 *   <li> Support named bind variables for JDBC implementations that don't
 *   <li> Queries may be returned in numerous formats
 *        <ul>
 *        	<li>NameValue a single row returned as a map
 *          <li>ListOfNameValue an array list of maps
 *          <li>A dataset
 *        </ul>
 *    <li>Upon an error the SQL is displayed along with the error before
 *        the SQLexception is reraised
 *    <li>sequence abstraction
 *    <li>Metadata
 *        <ul>
 *           <li>description of the statement
 *           <li>name
 *           <li>narrative, a long description of the statement and its purpose
 *    </ul>
 * </ul>
 * 
 * Sample usage
 * <pre>
 *    SqlStatement ss = new SqlStatement(connection,
 *      "select distinct cust_id from etl_sale where etl_file_id = :etl_file_id";
 *    Bind binds = new Binds();
 *    binds.put("etl_file_id", 1l);
 *    ListOfNameValue rows = ss.getListOfNameValue(binds);
 * </pre>
 *      
 *       etl_sale
 * @author jjs
 *
 */
public class SqlStatement {

	private static final Logger    logger                      = LoggerFactory.getLogger(SqlStatement.class);
	private static final String    pythonRegex                 = "%\\(([^)]*)\\)s";
	private static final String    colonRegex                  = ":([a-zA-Z_0-9]*)";
	private static final Pattern   pythonBindPattern           = Pattern.compile(pythonRegex);
	private static final Pattern   colonBindPattern            = Pattern.compile(colonRegex);
	private String                 name;
	private String                 sql;
	private String                 sqlOrig;
	private String                 preparedSql;
	private String                 description;
	private String                 narrative;
	private boolean                sequence;
	private Connection             conn;
	private PreparedStatement      preparedStatement           = null;
	private boolean                trace                       = false;
	private boolean                batching                    = false;
	private boolean                showSql                     = false;
	private boolean                showStack                   = false;
	/**
	 *
	 */
	private Dialect                dialect;
	private boolean                showError;
	private boolean                needsResultSetMetaDataCache = false;
	private ResultSetMetaDataCache resultSetMetaDataCache;
	private JsonSerializer dillon  = new JsonSerializerGson();

	private transient int batchSize = 100;
	private transient int batchCount = 0;

	public SqlStatement() {
		super();
	}

	public SqlStatement(SqlStatement stmt) {
		name = stmt.name;
		sql = stmt.sql;
		sqlOrig = stmt.sqlOrig;
		description = stmt.description;
		narrative = stmt.narrative;
		conn = stmt.conn;
	}

	public SqlStatement(String sql) {
		super();
		setSql(sql);
	}

	public SqlStatement(String name, String sql) {
		super();
		this.name = name;
		setSql(sql);
	}

	// TODO should move to NameValue
	public SqlStatement(Map<String, Object> map) {
		logger.debug("SqlStatement(Map<String,Object>) loading map: {}", map);
		setName((String) map.get("name"));
		setSql((String) map.get("sql"));
		setNarrative((String) map.get("narrative"));
		setDescription((String) map.get("description"));
		Object sequenceObject = map.get("sequence");
		if (sequenceObject != null) {
			logger.debug("sequenceObject class: {}", sequenceObject.getClass().getCanonicalName());
			setSequence((boolean) sequenceObject);
		}
	}

	public SqlStatement(String name, String sql, String description, String narrative) {
		super();
		this.name = name;
		setSql(sql);
		this.description = description;
		this.narrative = narrative;

	}

	public SqlStatement(String sql, Connection connection) throws SQLException {
		setSql(sql);
		setConnection(connection);
	}

	public SqlStatement(Connection connection, String sql) throws SQLException {
		setSql(sql);
		setConnection(connection);
	}

	/**
	 * 
	 * @param inSql The sql with colon binds
	 * @param binds the bind variables
	 * @return statement with colon binds replaced with question marks
	 * 
	 */
	public static String colonBindsToQuestionMark(String inSql, Binds binds) {
		String outSql = inSql;
		for (final String k : binds.keySet()) {
			outSql = outSql.replaceAll(':' + k, "?");
		}
		return outSql;
	}

	/**
	 * Replaces %(BIND_NAME)s with :BIND_NAME :param sql: :return: The sql with all
	 * of the binds in :bind format
	 *
	 * @param sql the SQL statement with %(bindname)s format bind variables
	 * @return binds with colons
	 **/
	public static String toColonBinds(String sql) {

		final List<String> binds = findBinds(sql, pythonBindPattern);
		String newSql = sql;
		for (final String bind : binds) {
			final String pythonBind = "%(" + bind + ")s";
			final String colonBind = ":" + bind;
			newSql = newSql.replace(pythonBind, colonBind); //
		}
		return newSql;
	}

	public TreeSet<String> getColonBindNames() {
		TreeSet<String> bindNames = new TreeSet<String>();
		List<String> names = findColonBinds(sql); // leave this here in case we want to check for dupes
		bindNames.addAll(names);
		return bindNames;
	}

	private List<String> findColonBinds(String sql2) {
		return findBinds(sql, colonBindPattern);
	}

	public static List<String> findPythonBinds(String sql) {
		return findBinds(sql, pythonBindPattern);
	}

	/**
	 * TODO doesn't work with : in quoted strings as in to_char (current_timestamp,
	 * 'YYYY-MM-DD HH24:MI:SSXFF') || ',"' ||
	 * 
	 * @param sql     the sql
	 * @param pattern PatternMatcher
	 * @return list of bind names
	 */
	public static List<String> findBinds(String sql, Pattern pattern) {
		if (sql == null) {
			throw new IllegalArgumentException("sql is null");
		}
		final List<String> retval = new ArrayList<>();

		final Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			final String grp = matcher.group(1);
			logger.debug("bind '" + grp + "'");
			if (grp.trim().length() > 0) {
				retval.add(grp); // parsing plsql find := length 0
			}
		}
		logger.debug("found binds " + retval);

		return retval;
	}

	/**
	 * Convert :BIND_NAME to ?
	 *
	 * @param sql the sql
	 * @return the sql with binds replaced with question marks
	 */
	public String toQuestionBinds(String sql) {
		/**
		 * Replaces %(BIND_NAME)s with :BIND_NAME :param sql: :return: The sql with all
		 * of the binds in :bind format
		 **/
		final List<String> colonBinds = findBinds(sql, colonBindPattern);
		String newSql = sql;
		for (final String bind : colonBinds) {
			final String colonBind = ":" + bind;
			newSql = newSql.replace(colonBind, "?"); //
		}
		final List<String> pythonBinds = findBinds(sql, pythonBindPattern);
		for (final String bind : pythonBinds) {
			final String pythonBind = "%(" + bind + ")s";
			newSql = newSql.replace(pythonBind, "?"); //
		}
		if (trace) {
			logger.debug(String.format("toQuestionBinds: replaced\n%s\nwith:\n%s", sql, newSql));
		}
		return newSql;
	}

	public void setConnection(Connection conn) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		this.conn = conn;
		this.dialect = Dialect.getDialect(conn);
	}

	public PreparedStatement bind(Binds binds) throws SQLException {
		return bind(binds, sql);
	}

	public PreparedStatement bind(Binds binds, String sql) throws SQLException {
		if (binds == null) {
			throw new IllegalArgumentException("binds is null");
		}
		prepare();
		final List<String> colonBindList = findBinds(sql, colonBindPattern);
		final List<String> pythonBindList = findBinds(sql, pythonBindPattern);
		List<String> bindList = new ArrayList<>();
		final boolean hasColonBinds = colonBindList.size() > 0;
		final boolean hasPythonBinds = pythonBindList.size() > 0;
		if (hasColonBinds && hasPythonBinds) {
			throw new SQLException("statement has two types of binds ");
		}
		if (hasColonBinds) {
			bindList = colonBindList;
		}
		if (hasPythonBinds) {
			bindList = pythonBindList;
		}
		logger.debug("binds: {}, sql: {}", binds, sql);
		logger.debug("bindList {}",bindList);
		int i = 1;
		for (final String bindName : bindList) {
			try {
				final Object value = binds.get(bindName);
				String className = null;
				if (value != null) {
					className = value.getClass().getName();
				}
				logger.debug("binding index " + i + " bindName: " + bindName + " value " + binds.get(bindName) + " class " + className);
				if (value == null) {
					logger.debug("calling setNull for index " + i);
					Integer type = binds.getType(bindName);
					if (type == null) {
						String message = String.format("bind %s is null type not specified",bindName);
						logger.error(message);
						throw new IllegalStateException(message);
					}
					preparedStatement.setNull(i, type); // TODO WTF
				} else if (value instanceof java.sql.Timestamp) {
					java.sql.Timestamp ts = (java.sql.Timestamp) value;
					preparedStatement.setTimestamp(i, ts);
				} else if (value instanceof java.util.Date) {
					final java.util.Date dtin = (java.util.Date) value;
					final java.sql.Date bindDate = new java.sql.Date(dtin.getTime());
					preparedStatement.setDate(i, bindDate);
				} else {
					preparedStatement.setObject(i, binds.get(bindName));
				}
			} catch (final SQLException e) {
				final String message = this.toString() + "\n" + binds + " for bind " + bindName +  "\nexception: \n" + e.getMessage();
				logger.error(message);
				throw new SQLException(message);
			} catch (final IllegalArgumentException e) {
				final String message = this.toString() + "\n" + binds + "\nexception: \n" + e.getMessage();
				logger.error(message);
				throw new IllegalArgumentException(message, e);
			}
			i++;
		}
		return preparedStatement;
	}

	void execute() throws SQLException {
		// TODO can this be reused
		final Statement stmt = getConn().createStatement();
		stmt.execute(sql);
		// TODO is this a leak? TODO test
		// stmt.close();
	}

	public void execute(Binds binds) throws SQLException {
		// TODO can this be reused
		// final Statement stmt = getConn().createStatement();
		bind(binds);
		if (showSql) {
			logger.info("execute: sql:\n{} binds: binds{}", sql, binds);
		}
		try {
			preparedStatement.execute();
		} catch (SQLException sqe) {
			logger.error("sql:\n{}prepared:\n{}binds:\n{}",
					sql, preparedSql, binds.toString());
			throw sqe;
		}
		// TODO do we always want to close?
	}

	public ResultSet executeQuery(Binds binds) throws SQLException {
		bind(binds);

		ResultSet resultSet;

		try {
			resultSet = preparedStatement.executeQuery();
			createResultSetMetaDataCache(resultSet);
		} catch (final SQLException e) {
			final String message = toString() + "\nbinds: " + binds + "\n" + e.getMessage();
			logger.error(message);
			throw new SQLException(message);
		}
		return resultSet;
	}

	public PreparedStatement prepare() throws SQLException {
		if (sql == null || sql.trim().length() == 0) {
			throw new IllegalArgumentException("sql is: '" + sql + "'");
		}
		if ((dialect == null) || dialect.useQuestionBinds()) {
			preparedSql = toQuestionBinds(sql);
			logger.debug("prepare() after toQuestionBinds:\nsql:\n%s\npreparedSql:\n{}", sql);
		} else {
			preparedSql = sql;
		}
		logger.debug("prepare(): sql:\n{}\nisPrepared:{}\npreparedSql:\n{}", sql, isPrepared(), preparedSql);

		if (!isPrepared()) {
			if (conn == null) {
				throw new IllegalStateException("conn is null");
			}
			try {
				logger.debug("prepare(): connection.prepareStatement:\n{}", preparedSql);
				preparedStatement = conn.prepareStatement(preparedSql);
			} catch (final SQLException sqe) {
				final String message = String.format("While preparing %s\n%s", sql, sqe.getMessage());
				logger.error(message);
				throw sqe;
			}
		}
		return preparedStatement;
	}

	public void close() throws SQLException {
		if (preparedStatement != null) {
			preparedStatement.close();
		}
	}

	public SqlStatement useColonBind() {
		sql = toColonBinds(sqlOrig);
		return this;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		if (sql == null) {
			throw new IllegalArgumentException("sql may not be null");
		}
		this.sql = sql;
		this.sqlOrig = sql;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String format() {
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format("SqlStatement name: %s\n", getName()));
		sb.append(String.format("sql:\n%s\n", getSql()));
		if (getDescription() != null) {
			sb.append(String.format("description:\n%s\n", getDescription()));
		}
		return sb.toString();
	}

	boolean isPrepared() {
		return preparedStatement != null;
	}

	public MappedResultSetIterator iterator(Binds binds) throws SQLException {
		ResultSet rset = executeQuery(binds);
		createResultSetMetaDataCache(rset);
		return new MappedResultSetIterator(rset);
	}

	@SuppressWarnings("resource")
	public ListOfLists getListOfLists(Binds binds) throws SQLException {
		final ResultSet rset = executeQuery(binds);
		createResultSetMetaDataCache(rset);
		return ResultSetHelper.toListOfLists(rset);
	}

	public void checkConnectionArgument(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException("Connection is null");
		}
	}

	public void checkConnection() {
		if (conn == null) {
			throw new IllegalStateException("setConnection not yet called");
		}
	}

	// TODO might be single use and preparing is a waste of time
	// if being passed a connection TODO!!! should reset the prepared Statement or
	// check if the connection is different
	// allow mixing if so declared as in "setMixedConnections"?
	// TODO consolidate with executeUpdate
	@Deprecated // should use binds and not use connection
	public int executeUpdateNoError(Connection conn, Binds binds) throws SQLException {
		int retval = -1;
		if (trace) {
			// logger.debug("executeUpdate tracing " + trace);
			String message = String.format("executeUpdate sql:\n%s\nbinds:\n%s", sql, binds);
			logger.debug(message);
		}
		try {
			checkConnectionArgument(conn);
			setConnection(conn);
			prepare();
			if (binds != null) {
				bind(binds);
			}
			retval = preparedStatement.executeUpdate();
		} catch (final SQLException sqe) {
			final String message = String.format("While processing:\nsql:\n'%s'\nbinds:\n%s\n%s", sql, binds,
					sqe.getMessage());
			logger.debug(message);

		}
		return retval;
	}

	// TODO might be single use and preparing is a waste of time
	// if being passed a connection TODO!!! should reset the prepared Statement or
	// check if the connection is different
	// allow mixing if so declared as in "setMixedConnections"?
	@Deprecated // should use binds and not use connection
	public int executeUpdate(Connection conn, Binds binds) throws SQLException {
		int retval = -1;
		if (trace) {
			String message = String.format("executeUpdate sql:\n%s\nbinds:\n%s", sql, binds);
			logger.debug(message);
		}
		try {
			checkConnectionArgument(conn);
			setConnection(conn);
			prepare();
			if (binds != null) {
				bind(binds);
			}

			retval = preparedStatement.executeUpdate();
		} catch (final Exception sqe) {
			final String message = String.format("While processing:\nsql:\n'%s'\nbinds:\n%s\n%s", sql, binds,
					sqe.getMessage());
			if (showStack) {
				sqe.printStackTrace();
			}
			logger.error(message);
			throw new SQLException(message);
			//			} else {
			//				logger.debug("showError is false so no exception no message {}", message);
			//			}
		}
		return retval;
	}

	@Deprecated // should use binds and not use connection
	public int executeUpdate(Connection conn) throws SQLException {
		checkConnectionArgument(conn);
		try {
			return conn.createStatement().executeUpdate(sql);
		} catch (final SQLException e) {
			logger.error(String.format("While executing:\n %s\nencountered\n%s\n", sql, e.getMessage()));
			throw e;
		}
	}

	//	@Deprecated // should use binds and not use connection
	//	public ListOfNameValue getListOfNameValue(Connection conn, boolean toLowerCase) throws SQLException {
	//		// TODO can this be a prepared statement
	//		checkConnectionArgument(conn);
	//		final Statement s = conn.createStatement();
	//		final ResultSet rset = s.executeQuery(sql);
	//		ListOfNameValue retval = ResultSetHelper.getListOfNameValue(rset, toLowerCase);
	//		rset.close();
	//		s.close();
	//		return retval;
	//	}

	/**
	 * Adds a batch
	 * 
	 * @param binds the bind values
	 * @throws SQLException If you did bad things.
	 */
	public void addBatch(Binds binds) throws SQLException {

		if (trace) {
			String message = String.format("executeUpdate sql:\n%s\nbinds:\n%s", sql, binds);
			logger.debug(message);
		}
		try {
			prepare();
			if (binds != null) {
				bind(binds);
			}

			batching = true;
			preparedStatement.addBatch();
			batchCount++;
			if (batchCount >= batchSize) {
				executeBatch();
				batchCount = 0;
			}
		} catch (final SQLException sqe) {
			final String message = String.format("While processing:\nsql:\n'%s'\nbinds:\n%s\n%s", sql, binds,
					sqe.getMessage());
			logger.error(message);
			throw new SQLException(message);
		}
	}

	/**
	 * Executes the batch
	 * 
	 * @throws SQLException Probably won't happen
	 */
	public void executeBatch() throws SQLException {
		if (batching) {
			preparedStatement.executeBatch();
			batching = false;
		}
	}

	/**
	 * Clears the batch
	 * 
	 * @throws SQLException If you did bad things
	 */
	public void clearBatch() throws SQLException {
		if (batching) {
			preparedStatement.clearBatch();
			batching = false;
		}
	}

	public ListOfNameValue executeUpdateGetGeneratedKeysTemp(Binds binds) throws SQLException {
		PreparedStatement ps = prepare();
		bind(binds);
		ps.executeUpdate();
		ResultSet rset = ps.getGeneratedKeys();
		createResultSetMetaDataCache(rset);
		int rownum = 0;
		while (rset.next()) {
			logger.info("row {}", rownum++);
		}
		return ResultSetHelper.getListOfNameValue(rset);
	}

	public ListOfNameValue executeUpdateGetGeneratedKeys(Binds binds) throws SQLException {
		PreparedStatement ps = prepare();
		bind(binds);
		ps.executeUpdate();
		ResultSet rset = ps.getGeneratedKeys();
		createResultSetMetaDataCache(rset);
		return ResultSetHelper.getListOfNameValue(rset);

	}

	public int executeUpdate(Binds b) throws SQLException {
		/* nasty hack for statements with : in 'yy:mm:dd' type strings */

		return executeUpdate(conn, b);
	}

	//	/**
	//	 * Returns a single row
	//	 * 
	//	 * The ResultSet must return exactly one row or there is an error.
	//	 * 
	//	 * @param binds     the binds
	//	 * @param lowerCase convert name value to lowercase keys
	//	 * @return a NameValue
	//	 * @throws SQLException          If you did bad things.
	//	 * @throws DataNotFoundException If the query returned no rows.
	//	 */
	//	public NameValue getNameValue(Binds binds, boolean lowerCase) throws SQLException {
	//		final ResultSet rset = executeQuery(binds);
	//		NameValue retval = null;
	//		createResultSetMetaDataCache(rset);
	//		try {
	//			rset.next();
	//		} catch (final SQLException sqe) {
	//			final String message = String.format("getNameValue for %s\nbinds: %s returned no rows", sql, binds);
	//			logger.error(message);
	//			rset.close();
	//			throw new DataNotFoundException(message, sqe);
	//		}
	//		try {
	//			retval = ResultSetHelper.getNameValue(rset, lowerCase);
	//		} 
	//		catch (TooManyRowsException too) {
	//			throw too;
	//		}
	//		catch (final SQLException sqe) {
	//			final String message = String.format("getNameValue for %s\nbinds: %s returned no rows", sql, binds);
	//			logger.error(message);
	//			throw new DataNotFoundException(message, sqe);
	//		} 
	//		
	//		
	//		finally {
	//			rset.close();
	//		}
	//		return retval;
	//}i
	/**
	 * @param lowerCase convert name value to lowercase keys
	 * @return a NameValue
	 * @throws SQLException          If you did bad things.
	 * @throws DataNotFoundException If the query returned no rows.
	 */
	public NameValue getNameValue(Binds binds, boolean lowerCase) throws SQLException {
		final ResultSet rset = executeQuery(binds);
		createResultSetMetaDataCache(rset);
		return ResultSetHelper.getNameValue(rset, lowerCase);
	}

	public ListOfNameValue getListOfNameValue(Binds binds, boolean toLowerCase) throws SQLException {
		ResultSet rset = executeQuery(binds);
		// createResultSetMetaDataCache(rset);
		return ResultSetHelper.getListOfNameValue(rset, toLowerCase);
	}

	public ListOfNameValue getListOfNameValue() throws SQLException {
		Binds binds = new Binds();
		return getListOfNameValue(binds, true);
	}

	public ListOfNameValue getListOfNameValue(Binds binds) throws SQLException {
		return getListOfNameValue(binds, true);
	}

	public NameValue getNameValue() throws SQLException {
		final boolean namesToLower = true;
		return getNameValue(new Binds(), namesToLower);
	}

	//	@Deprecated
	//	public Object executeReturning(Binds binds) throws SQLException {
	//		Object retval;
	//		prepare();
	//		ResultSet rset;
	//		switch (dialect) {
	//
	//		case SQLITE:
	//			execute(binds);
	//			rset = conn.createStatement().executeQuery("select last_insert_rowid");
	//			rset.next();
	//			retval = rset.getObject(1);
	//		case POSTGRES:
	//		case H2:
	//			ResultSet rsetH2;
	//			try {
	//				execute(binds);
	//				rsetH2 = preparedStatement.getGeneratedKeys();
	//				rsetH2.next();
	//				retval = rsetH2.getObject(1);
	//			} catch (SQLException e) {
	//				logger.error(this.toString() + " " + binds + e.getMessage());
	//				throw e;
	//			}
	//
	//			break;
	//		default:
	//			throw new UnsupportedOperationException("Unhandled dialect " + dialect);
	//		}
	//		return retval;
	//	}

	private Connection getConn() {
		if (conn == null) {
			throw new IllegalStateException("conn is null");
		}
		return conn;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;

	}

	/**
	 * @return the sequence
	 */
	public boolean isSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(boolean sequence) {
		this.sequence = sequence;
	}

	// TOODO merge with SequenceHelper
	public long nextval() throws SQLException {
		if (conn == null) {
			throw new IllegalStateException("connection is null");
		}
		if (dialect == null) {
			throw new IllegalStateException("dialect is null");
		}
		long retval;
		if (sequence) {
			switch (dialect) {
			case POSTGRES:
			case H2:
				preparedSql = "select nextval( '" + sql + "')";
				break;
			case ORACLE:
				preparedSql = "select " + sql + ".nextval from dual";
				break;
			case SQLITE: // TODO
				break;
			case UNSPECIFIED:
				break;
			default:
				break;
			}
		} else {
			throw new IllegalOperationException("not a sequence");
		}
		if (preparedStatement == null) {
			preparedStatement = conn.prepareStatement(preparedSql);
		}
		ResultSet rset;
		try {
			rset = preparedStatement.executeQuery();
			rset.next();
			retval = rset.getLong(1);
			rset.close();
		} catch (SQLException sqe) {
			String message = String.format("While processing %s message %s", preparedSql, sqe.getMessage());
			logger.error(message);
			throw new SQLException(message, sqe.getCause());
		}
		return retval;
	}

	private void createResultSetMetaDataCache(ResultSet rset) throws SQLException {
		logger.debug("createResultSetMetaDataCache before needsResultSetMetaDataCache {} {}", needsResultSetMetaDataCache,
				resultSetMetaDataCache);
		if (needsResultSetMetaDataCache && resultSetMetaDataCache == null) {
			resultSetMetaDataCache = new ResultSetMetaDataCache(rset);
			logger.debug(resultSetMetaDataCache.toString());
		}
		logger.debug("createResultSetMetaDataCache before needsResultSetMetaDataCache {} {}", needsResultSetMetaDataCache,
				resultSetMetaDataCache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SqlStatement [name=");
		builder.append(name);
		builder.append(", sql=\n");
		builder.append(sql);
		// builder.append(", sqlOrig=\n");
		// builder.append(sqlOrig);
		builder.append("\n, preparedSql=\n");
		builder.append(preparedSql);
		builder.append("\n, description=");
		builder.append(description);
		builder.append(", narrative=");
		builder.append(narrative);
		builder.append(", sequence=");
		builder.append(sequence);
		builder.append(", trace=");
		builder.append(trace);
		builder.append(", batching=");
		builder.append(batching);
		builder.append(", dialect=");
		builder.append(dialect);
		builder.append(", showError=");
		builder.append(showError);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the showSql
	 */
	public boolean isShowSql() {
		return showSql;
	}

	/**
	 * @param showSql the showSql to set
	 */
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	/**
	 * @return the preparedSql
	 */
	public String getPreparedSql() {
		return preparedSql;
	}

	/**
	 * @return the dialect
	 */
	public Dialect getDialect() {
		return dialect;
	}

	/**
	 * @return the showError
	 */
	public boolean isShowError() {
		return showError;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public ResultSetMetaDataCache getResultSetMetaDataCache() {
		if (!needsResultSetMetaDataCache) {
			throw new RuntimeException("needsResultSetMetaDataCache " + needsResultSetMetaDataCache);
		}
		if (resultSetMetaDataCache == null) {
			throw new IllegalStateException("resultSetMetaDataCache is null");
		}
		return resultSetMetaDataCache;
	}

	/**
	 * @return the needsResultSetMetaDataCache
	 */
	public boolean isNeedsResultSetMetaDataCache() {
		return needsResultSetMetaDataCache;
	}

	/**
	 * @param needsResultSetMetaDataCache the needsResultSetMetaDataCache to set
	 */
	public void setNeedsResultSetMetaDataCache(boolean needsResultSetMetaDataCache) {
		this.needsResultSetMetaDataCache = needsResultSetMetaDataCache;
	}

	public ListOfNameValueDataset getListOfNameValueDataSet(Binds binds, Map<String, ColumnMetadata> enhanceMeta)
			throws SQLException {
		setNeedsResultSetMetaDataCache(true);
		ListOfNameValue lonv = getListOfNameValue(binds, true);
		ResultSetMetaDataCache cache = getResultSetMetaDataCache();
		DatasetMetadataImpl dsMeta = new DatasetMetadataImpl(cache);
		dsMeta.enhance(enhanceMeta);
		ListOfNameValueDataset dataset = new ListOfNameValueDataset(lonv, dsMeta);
		return dataset;
	}

	public ListOfNameValueDataset getListOfNameValueDataSet(Binds binds) throws SQLException {
		setNeedsResultSetMetaDataCache(true);
		ListOfNameValue lonv = getListOfNameValue(binds, true);
		ResultSetMetaDataCache cache = getResultSetMetaDataCache();
		DatasetMetadataImpl dsMeta = new DatasetMetadataImpl(cache);
		ListOfNameValueDataset dataset = new ListOfNameValueDataset(lonv, dsMeta);
		return dataset;
	}

	public Object getConnection() {
		return conn;
	}

	public MatrixDataset getCrosstabbedDataset(Binds binds, CrosstabColumns crosstabColumns) throws SQLException {
		ListOfNameValueDataset dataset = getListOfNameValueDataSet(binds);
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		coke.setCrosstabColumns(crosstabColumns);
		coke.setDataSet(dataset);
		final MatrixDataset ds = coke.getDataSet();
		return ds;
	}

	public String getAsListOfNameValueAsJson(Binds binds) throws SQLException {
		ListOfNameValue lonv = getListOfNameValue(binds, true);
		String retval = dillon.toJson(lonv);
		return retval;
	}
}