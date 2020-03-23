package org.javautil.core.jdbc.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.javautil.core.text.StringBuilderHelper;

public class DatabaseMetadata {

	private boolean autoCommitFailureClosesAllResultSets;
	private boolean doesMaxRowSizeIncludeBlobs;
	private boolean generatedKeyAlwaysReturned;

//	private int databaseMajorVersion;
//	private int driverMinorVersion;
//	private String databaseProductName;
//	private int defaultTransactionIsolation;
//	private int databaseMinorVersion;
//	private String databaseProductVersion;
//	private int driverMajorVersion;
//	private String userName;
//	private String driverName;

	/*
	 * Retrieves whether the current user can call all the procedures returned by
	 * the method getProcedures.
	 */
	private boolean allProceduresAreCallable;
	/*
	 * Retrieves whether the current user can use all the tables returned by the
	 * method getTables in a SELECT statement.
	 */
	private boolean allTablesAreSelectable;
	/*
	 * Retrieves whether a data definition statement within a transaction forces the
	 * transaction to commit.
	 */
	private boolean dataDefinitionCausesTransactionCommit;
	/*
	 * Retrieves whether this database ignores a data definition statement within a
	 * transaction.
	 */
	private boolean dataDefinitionIgnoredInTransactions;
	// /* Retrieves whether or not a visible row delete can be detected by
	// calling the method ResultSet.rowDeleted. */
	// private boolean deletesAreDetected(int type)
	/*
	 * Retrieves whether the return value for the method getmaxRowSize includes the
	 * SQL data types LONGVARCHAR and LONGVARBINARY.
	 */
	private boolean doesmaxRowSizeIncludeBlobs;

	/* catalog names available in this database. */
	// ResultSet getCatalogs;
	/*
	 * String that this database uses as the separator between a catalog and table
	 * name.
	 */
	private String  catalogSeparator;
	/* database vendor's preferred term for "catalog". */
	private String  catalogTerm;
	/* major version number of the underlying database. */
	private int     databaseMajorVersion;
	/* minor version number of the underlying database. */
	private int     databaseMinorVersion;
	/* name of this database product. */
	private String  databaseProductName;
	/* version number of this database product. */
	private String  databaseProductVersion;
	/* Retrieves this database's default transactionolation level. */
	private int     defaultTransactionIsolation;

	/* Retrieves this JDBC driver's major version number. */
	private int     driverMajorVersion;
	/* Retrieves this JDBC driver's minor version number. */
	private int     driverMinorVersion;
	/* name of this JDBC driver. */
	private String  driverName;
	/* version number of this JDBC driver as a String. */
	private String  driverVersion;
	/*
	 * ll the "extra" characters that can be used in unquoted identifier names
	 * (those beyond a-z, A-Z, 0-9 and _).
	 */
	private String  extraNameCharacters;
	/* string used to quote SQL identifiers. */
	private String  identifierQuoteString;

	/* major JDBC version number for this driver. */
	private int     jdbcMajorVersion;
	/* minor JDBC version number for this driver. */
	private int     jdbcMinorVersion;
	/*
	 * maximum number of hex characters this database allows in an inline binary
	 * literal.
	 */
	private int     maxBinaryLiteralLength;
	/* maximum number of characters that this database allows in a catalog name. */
	private int     maxCatalogNameLength;
	/*
	 * maximum number of characters this database allows for a character literal.
	 */
	private int     maxCharLiteralLength;
	/* maximum number of characters this database allows for a column name. */
	private int     maxColumnNameLength;
	/* maximum number of columns this database allows in a GROUP BY clause. */
	private int     maxColumnsInGroupBy;
	/* maximum number of columns this database allows in an index. */
	private int     maxColumnsInIndex;
	/* maximum number of columns this database allows in an ORDER BY clause. */
	private int     maxColumnsInOrderBy;
	/* maximum number of columns this database allows in a SELECT list. */
	private int     maxColumnsInSelect;
	/* maximum number of columns this database allows in a table. */
	private int     maxColumnsInTable;
	/*
	 * maximum number of concurrent connections to this database that are possible.
	 */
	private int     maxConnections;
	/* maximum number of characters that this database allows in a cursor name. */
	private int     maxCursorNameLength;
	/*
	 * maximum number of bytes this database allows for an index, including all of
	 * the parts of the index.
	 */
	private int     maxIndexLength;
	/*
	 * maximum number of characters that this database allows in a procedure name.
	 */
	private int     maxProcedureNameLength;
	/* maximum number of bytes this database allows in a single row. */
	private int     maxRowSize;
	/* maximum number of characters that this database allows in a schema name. */
	private int     maxSchemaNameLength;
	/* maximum number of characters this database allows in an SQL statement. */
	private int     maxStatementLength;
	/*
	 * maximum number of active statements to this database that can be open at the
	 * same time.
	 */
	private int     maxStatements;
	/* maximum number of characters this database allows in a table name. */
	private int     maxTableNameLength;
	/* maximum number of tables this database allows in a SELECT statement. */
	private int     maxTablesInSelect;
	/* maximum number of characters this database allows in a user name. */
	private int     maxUserNameLength;
	/* comma-separated list of math functions available with this database. */
	private String  numericFunctions;
	/*
	 * description of the given catalog's stored procedure parameter and result
	 * columns.
	 */
	/* database vendor's preferred term for "procedure". */
	private String  procedureTerm;
	/* default holdability of this ResultSet object. */
	private int     resultSetHoldability;
	// TODO /* schema names available in this database. */
	/* database vendor's preferred term for "schema". */
	private String  schemaTerm;
	/* string that can be used to escape wildcard characters. */
	private String  searchStringEscape;
	/*
	 * comma-separated list of all of this database's SQL keywords that are NOT also
	 * SQL92 keywords.
	 */
	private String  sqlKeywords;
	// Indicates whether the SQLSTATE returned by SQLException.SQLState X/Open
	// (now known as Open Group) SQL CLI or SQL99.
	// int SQLStateType;
	/* comma-separated list of string functions available with this database. */
	private String  stringFunctions;
	/* comma-separated list of system functions available with this database. */
	private String  systemFunctions;
	/*
	 * comma-separated list of the time and date functions available with this
	 * database.
	 */
	private String  timeDateFunctions;
	/* URL for this DBMS. */
	private String  URL;

	/* user name as known to this database. */
	private String  UserName;
	// /* Retrieves whether or not a visible row insert can be detected by
	// calling the method ResultSet.rowInserted. */
	// private boolean insertsAreDetected(int type)
	/*
	 * Retrieves whether a catalog appears at the start of a fully qualified table
	 * name.
	 */
	private boolean isCatalogAtStart;
	/* Retrieves whether this database in read-only mode. */
	private boolean isReadOnly;
	/*
	 * Indicates whether updates made to a LOB are made on a copy or directly to the
	 * LOB. private boolean locatorsUpdateCopy; /* Retrieves whether this database
	 * supports concatenations between NULL and non-NULL values being NULL.
	 */
	private boolean nullPlusNonNullIsNull;
	/*
	 * Retrieves whether NULL values are sorted at the end regardless of sort order.
	 */
	private boolean nullsAreSortedAtEnd;
	/*
	 * Retrieves whether NULL values are sorted at the start regardless of sort
	 * order.
	 */
	private boolean nullsAreSortedAtStart;
	/* Retrieves whether NULL values are sorted high. */
	private boolean nullsAreSortedHigh;
	/* Retrieves whether NULL values are sorted low. */
	private boolean nullsAreSortedLow;
	/* Retrieves whether deletes made by others are visible. */
	// private boolean othersDeletesAreVisible(int type)
	// /* Retrieves whether inserts made by others are visible. */
	// private boolean othersInsertsAreVisible(int type)
	// /* Retrieves whether updates made by others are visible. */
	// private boolean othersUpdatesAreVisible(int type)
	// /* Retrieves whether a result set's own deletes are visible. */
	// private boolean ownDeletesAreVisible(int type)
	// /* Retrieves whether a result set's own inserts are visible. */
	// private boolean ownInsertsAreVisible(int type)
	// /* Retrieves whether for the given type of ResultSet object, the result
	// set's own updates are visible. */
	// private boolean ownUpdatesAreVisible(int type)
	/*
	 * Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case insensitive and stores them in lower case.
	 */
	private boolean storesLowerCaseIdentifiers;
	/*
	 * Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case insensitive and stores them in lower case.
	 */
	private boolean storesLowerCaseQuotedIdentifiers;
	/*
	 * Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case insensitive and stores them in mixed case.
	 */
	private boolean storesMixedCaseIdentifiers;
	/*
	 * Rhis database treats mixed case unquoted SQL identifiers as case insensitive
	 * and stores them in upper case.
	 */
	private boolean storesMixedCaseQuotedIdentifiers;
	/*
	 * Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case insensitive and stores them in upper case.
	 */
	private boolean storesUpperCaseIdentifiers;
	/* Retrieves whether this database supports ALTER TABLE with add column. */
	private boolean supportsAlterTableWithAddColumn;
	/* Retrieves whether this database supports ALTER TABLE with drop column. */
	private boolean supportsAlterTableWithDropColumn;
	/*
	 * Retrieves whether this database supports the ANSI92 entry level SQL grammar.
	 */
	private boolean supportsANSI92EntryLevelSQL;
	/*
	 * Retrieves whether this database supports the ANSI92 full SQL grammar
	 * supported.
	 */
	private boolean supportsANSI92FullSQL;
	/*
	 * Retrieves whether this database supports the ANSI92 intermediate SQL grammar
	 * supported.
	 */
	private boolean supportsANSI92IntermediateSQL;
	/* Retrieves whether this database supports batch updates. */
	private boolean supportsBatchUpdates;

	/*
	 * Retrieves whether a catalog name can be used in a data manipulation
	 * statement.
	 */
	private boolean supportsCatalogsInDataManipulation;
	/*
	 * Retrieves whether a catalog name can be used in an index definition
	 * statement.
	 */
	private boolean supportsCatalogsInIndexDefinitions;
	/*
	 * Retrieves whether a catalog name can be used in a privilege definition
	 * statement.
	 */
	private boolean supportsCatalogsInPrivilegeDefinitions;
	/*
	 * Retrieves whether a catalog name can be used in a procedure call statement.
	 */
	private boolean supportsCatalogsInProcedureCalls;
	/*
	 * Retrieves whether a catalog name can be used in a table definition statement.
	 */
	private boolean supportsCatalogsInTableDefinitions;
	/* Retrieves whether this database supports column aliasing. */
	private boolean supportsColumnAliasing;
	/*
	 * Retrieves whether this database supports the CONVERT function between SQL
	 * types.
	 */
	private boolean supportsConvert;
	// /* Retrieves whether this database supports the CONVERT for two given SQL
	// types. */
	// private boolean supportsConvert(int fromType, private int toType)
	/* Retrieves whether this database supports the ODBC Core SQL grammar. */
	private boolean supportsCoreSQLGrammar;
	/* Retrieves whether this database supports correlated subqueries. */
	private boolean supportsCorrelatedSubqueries;
	/*
	 * Retrieves whether this database supports both data definition and data
	 * manipulation statements within a transaction.
	 */
	private boolean supportsDataDefinitionAndDataManipulationTransactions;
	/*
	 * Retrieves whether this database supports only data manipulation statements
	 * within a transaction.
	 */
	private boolean supportsDataManipulationTransactionsOnly;
	/*
	 * Retrieves whether, when table correlation names are supported, they are
	 * restricted to being different from the names of the tables.
	 */
	private boolean supportsDifferentTableCorrelationNames;
	/* Retrieves whether this database supports expressions in ORDER BY lists. */
	private boolean supportsExpressionsInOrderBy;
	/* Retrieves whether this database supports the ODBC Extended SQL grammar. */
	private boolean supportsExtendedSQLGrammar;
	/* Retrieves whether this database supports full nested outer joins. */
	private boolean supportsFullOuterJoins;
	/*
	 * Retrieves whether auto-generated keys can be retrieved after a statement has
	 * been executed.
	 */
	private boolean supportsGetGeneratedKeys;
	/* Retrieves whether this database supports some form of GROUP BY clause. */
	private boolean supportsGroupBy;
	/*
	 * Retrieves whether this database supports using columns not included in the
	 * SELECT statement in a GROUP BY clause provided that all of the columns in the
	 * SELECT statement are included in the GROUP BY clause.
	 */
	private boolean supportsGroupByBeyondSelect;
	/*
	 * Retrieves whether this database supports using a column that not in the
	 * SELECT statement in a GROUP BY clause.
	 */
	private boolean supportsGroupByUnrelated;
	/*
	 * Retrieves whether this database supports the SQL Integrity Enhancement
	 * Facility.
	 */
	private boolean supportsIntegrityEnhancementFacility;
	/* Retrieves whether this database supports specifying a LIKE escape clause. */
	private boolean supportsLikeEscapeClause;
	/* Retrieves whether this database provides limited support for outer joins. */
	private boolean supportsLimitedOuterJoins;
	/* Retrieves whether this database supports the ODBC Minimum SQL grammar. */
	private boolean supportsMinimumSQLGrammar;
	/*
	 * Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case sensitive and as a result stores them in mixed case.
	 */
	private boolean supportsMixedCaseIdentifiers;
	/*
	 * Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case sensitive and as a result stores them in mixed case.
	 */
	private boolean supportsMixedCaseQuotedIdentifiers;
	/*
	 * Retrieves whether it possible to have multiple ResultSet objects returned
	 * from a CallableStatement object simultaneously.
	 */
	private boolean supportsMultipleOpenResults;
	/*
	 * Retrieves whether this database supports getting multiple ResultSet objects
	 * from a single call to the method execute.
	 */
	private boolean supportsMultipleResultSets;
	/*
	 * Retrieves whether this database allows having multiple transactions open at
	 * once (on different connections).
	 */
	private boolean supportsMultipleTransactions;
	/*
	 * Retrieves whether this database supports named parameters to callable
	 * statements.
	 */
	private boolean supportsNamedParameters;
	/*
	 * Retrieves whether columns in this database may be defined as non-nullable.
	 */
	private boolean supportsNonNullableColumns;
	/*
	 * Retrieves whether this database supports keeping cursors open across commits.
	 */
	private boolean supportsOpenCursorsAcrossCommit;
	/*
	 * Retrieves whether this database supports keeping cursors open across
	 * rollbacks.
	 */
	private boolean supportsOpenCursorsAcrossRollback;
	/*
	 * Retrieves whether this database supports keeping statements open across
	 * commits.
	 */
	private boolean supportsOpenStatementsAcrossCommit;
	/*
	 * Retrieves whether this database supports keeping statements open across
	 * rollbacks.
	 */
	private boolean supportsOpenStatementsAcrossRollback;
	/*
	 * Retrieves whether this database supports using a column that not in the
	 * SELECT statement in an ORDER BY clause.
	 */
	private boolean supportsOrderByUnrelated;
	/* Retrieves whether this database supports some form of outer join. */
	private boolean supportsOuterJoins;
	/* Retrieves whether this database supports positioned DELETE statements. */
	private boolean supportsPositionedDelete;
	/* Retrieves whether this database supports positioned UPDATE statements. */
	private boolean supportsPositionedUpdate;
	// /* Retrieves whether this database supports the given concurrency type in
	// combination with the given result set type. */
	// private boolean supportsResultSetConcurrency(int type, private int
	// concurrency)
	// /* Retrieves whether this database supports the given result set
	// holdability. */
	// private boolean supportsResultSetHoldability(int holdability)
	// /* Retrieves whether this database supports the given result set type. */
	// private boolean supportsResultSetType(int type)
	/* Retrieves whether this database supports savepoints. */
	private boolean supportsSavepoints;
	/*
	 * Retrieves whether a schema name can be used in a data manipulation statement.
	 */
	private boolean supportsSchemasInDataManipulation;
	/*
	 * Retrieves whether a schema name can be used in an index definition statement.
	 */
	private boolean supportsSchemasInIndexDefinitions;
	/*
	 * Retrieves whether a schema name can be used in a privilege definition
	 * statement.
	 */
	private boolean supportsSchemasInPrivilegeDefinitions;
	/*
	 * Retrieves whether a schema name can be used in a procedure call statement.
	 */
	private boolean supportsSchemasInProcedureCalls;
	/*
	 * Retrieves whether a schema name can be used in a table definition statement.
	 */
	private boolean supportsSchemasInTableDefinitions;
	/* Retrieves whether this database supports SELECT FOR UPDATE statements. */
	private boolean supportsSelectForUpdate;
	/* Retrieves whether this database supports statement pooling. */
	private boolean supportsStatementPooling;
	/*
	 * Retrieves whether this database supports stored procedure calls that use the
	 * stored procedure escape syntax.
	 */
	private boolean supportsStoredProcedures;
	/*
	 * Retrieves whether this database supports subqueries in comparison
	 * expressions.
	 */
	private boolean supportsSubqueriesInComparisons;
	/*
	 * Retrieves whether this database supports subqueries in EXISTS expressions.
	 */
	private boolean supportsSubqueriesInExists;
	/* Retrieves whether this database supports subqueries in IN statements. */
	private boolean supportsSubqueriesInIns;
	/*
	 * Retrieves whether this database supports subqueries in quantified
	 * expressions.
	 */
	private boolean supportsSubqueriesInQuantifieds;
	/* Retrieves whether this database supports table correlation names. */
	private boolean supportsTableCorrelationNames;
	// /* Retrieves whether this database supports the given transactionolation
	// level. */
	// private boolean supportsTransactionIsolationLevel(int level)
	/* Retrieves whether this database supports transactions. */
	private boolean supportsTransactions;
	/* Retrieves whether this database supports SQL UNION. */
	private boolean supportsUnion;
	/* Retrieves whether this database supports SQL UNION ALL. */
	private boolean supportsUnionAll;
	// /* Retrieves whether or not a visible row update can be detected by
	// calling the method ResultSet.rowUpdated. */
	// private boolean updatesAreDetected(int type)
	/* Retrieves whether this database uses a file for each table. */
	private boolean usesLocalFilePerTable;
	private boolean usesLocalFiles;
	private String  userName;

	public DatabaseMetadata(Connection conn) throws SQLException {
		DatabaseMetaData dbmeta = conn.getMetaData();
		allProceduresAreCallable = dbmeta.allProceduresAreCallable();
		allTablesAreSelectable = dbmeta.allTablesAreSelectable();
		catalogSeparator = dbmeta.getCatalogSeparator();
		autoCommitFailureClosesAllResultSets = dbmeta.autoCommitFailureClosesAllResultSets();
		dataDefinitionCausesTransactionCommit = dbmeta.dataDefinitionCausesTransactionCommit();
		dataDefinitionIgnoredInTransactions = dbmeta.dataDefinitionIgnoredInTransactions();
		doesMaxRowSizeIncludeBlobs = dbmeta.doesMaxRowSizeIncludeBlobs();
		// generatedKeyAlwaysReturned = dbmeta.generatedKeyAlwaysReturned();
		databaseMajorVersion = dbmeta.getDatabaseMajorVersion();
		driverMinorVersion = dbmeta.getDriverMinorVersion();
		databaseProductName = dbmeta.getDatabaseProductName();
		defaultTransactionIsolation = dbmeta.getDefaultTransactionIsolation();
		databaseMinorVersion = dbmeta.getDatabaseMinorVersion();
		databaseProductVersion = dbmeta.getDatabaseProductVersion();
		driverMajorVersion = dbmeta.getDriverMajorVersion();
		driverName = dbmeta.getDriverName();
		URL = dbmeta.getURL();
		userName = dbmeta.getUserName();
		storesLowerCaseIdentifiers = dbmeta.storesLowerCaseIdentifiers();
		storesMixedCaseIdentifiers = dbmeta.storesMixedCaseIdentifiers();
		storesLowerCaseQuotedIdentifiers = dbmeta.storesLowerCaseQuotedIdentifiers();
		storesMixedCaseQuotedIdentifiers = dbmeta.storesMixedCaseQuotedIdentifiers();
		storesUpperCaseIdentifiers = dbmeta.storesUpperCaseIdentifiers();
		storesUpperCaseIdentifiers = dbmeta.storesUpperCaseIdentifiers();

	}

	public String getDatabaseDescription() {
		return getDatabaseProductName() + " " + getDatabaseProductVersion() + "." + getDatabaseMinorVersion();
	}

	public LinkedHashMap<String, Object> asMap() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("userName", userName);
		map.put("URL", URL);
		map.put("databaseProductName", databaseProductName);
		map.put("databaseProductVersion", databaseProductVersion);
		// map.put("databaseMinorVersion",databaseMinorVersion);
		map.put("driverName", driverName);
		return map;

	}

	public int getGetDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	public String getGetDatabaseProductName() {
		return databaseProductName;
	}

	public int getGetDatabaseMinorVersion() {
		return databaseMinorVersion;
	}

	public int getDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	public int getDatabaseMinorVersion() {
		return databaseMinorVersion;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public int getDriverMajorVersion() {
		return driverMajorVersion;
	}

	public int getDriverMinorVersion() {
		return driverMinorVersion;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public String getURL() {
		return URL;
	}

	public String getUserName() {
		return UserName;
	}

	@Override
	public String toString() {
		StringBuilderHelper sb = new StringBuilderHelper();
		sb.addNameValue("url", getURL());
		sb.addNameValue("user", getUserName());
		sb.addNameValue("databaseProductName", getDatabaseProductName());
		return sb.toString();

	}
}
