package org.javautil.core.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.javautil.core.jdbc.metadata.DatabaseMetadata;
import org.javautil.core.json.JsonSerializer;
import org.javautil.core.json.JsonSerializerGson;
import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SimpleResultSetIterator;
import org.javautil.core.sql.SqlStatement;
import org.javautil.core.text.CommonDateFormat;
import org.javautil.core.text.SimpleDateFormats;
import org.javautil.core.text.SimpleDateFormatter;
import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DatasetMetadataFactory;
import org.javautil.dataset.MutableDatasetMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SqlCsvExporter {

	// TODO add number of rows and column metadata
	@JsonIgnore
	private Date                                    startTime               = new Date();
	@JsonIgnore
	private Date                                    endTime;

	private String                                  exportDateFormatString  = SimpleDateFormats.ISO8601_date_pretty;
	private String                                  dateTimeFormatString    = SimpleDateFormats.ISO8601_datetime_timezone_pretty;
	@JsonIgnore
	transient private SimpleDateFormat              exportDateFormat;
	@JsonIgnore
	transient private SimpleDateFormat              exportDateTimeFormat;
	@JsonIgnore
	transient private SimpleDateFormatter           exportDateTimeFormatter = new SimpleDateFormatter(
	    CommonDateFormat.SECONDS);
	private String                                  sql;
	@JsonIgnore
	private Binds                                   binds;
	transient private LinkedHashMap<String, Object> databaseMetaData;
	private List<ColumnMetadata>                    columnMeta;

	/**
	 * Non marshalled
	 */
	@JsonIgnore
	transient private Connection                    connection;

	@JsonIgnore
	transient private SqlStatement                  sqlStatement;
	@JsonIgnore
	transient private ResultSet                     rset;
	@JsonIgnore
	transient private MutableDatasetMetadata        metadata;
	private File                                    file;
	@JsonIgnore
	transient private OutputStream                  outputStream;
	@JsonIgnore
	private ArrayList<String>                       columnNames;
	@JsonIgnore
	transient private CsvWriter                     writer;

	@JsonIgnore
	transient private Logger                        logger                  = LoggerFactory.getLogger(getClass());

	public SqlCsvExporter(String sql, Connection connection, Binds binds) {
		super();

		this.sql = sql;
		this.connection = connection;
		this.binds = binds;
	}

	public SqlCsvExporter(Connection connection, String sql) {
		super();

		this.sql = sql;
		this.connection = connection;
		this.binds = new Binds();
	}

	public SqlCsvExporter() {
		// TODO Auto-generated constructor stub
	}

	private void initialize() throws SQLException, ParseException {
		databaseMetaData = new DatabaseMetadata(connection).asMap();

		Date now = new Date();
		this.exportDateTimeFormat = new SimpleDateFormat(dateTimeFormatString);
		// test the formatter before we do too much work
		String fmt = exportDateTimeFormat.format(now);
		Date d = exportDateTimeFormat.parse(fmt);
		//
		sqlStatement = new SqlStatement(connection, sql);
		rset = sqlStatement.executeQuery(binds);
		ResultSetMetaData rsetMeta = rset.getMetaData();

		metadata = DatasetMetadataFactory.getInstance(rsetMeta);
		columnMeta = metadata.getColumnMetadata();
		columnNames = metadata.getColumnNames();
	}

	public void write(File file) throws SQLException, IOException, ParseException {
		this.file = file;
		FileWriter fw = new FileWriter(file);
		write(fw);
		fw.close();

	}

	public void write(Writer w) throws SQLException, IOException, ParseException {
		initialize();
		writer = new CsvWriter(w);
		writer.setSimpleDateFormatter(exportDateTimeFormatter);
		writer.writeList(columnNames);
		SimpleResultSetIterator rsi = new SimpleResultSetIterator(rset);
		while (rsi.hasNext()) {
			writer.writeList(rsi.next());
		}
	}

	public void setOutputStream(OutputStream baos) {
		this.outputStream = baos;

	}

	public String toJson() throws SQLException, JsonProcessingException {
		JsonSerializer omu = new JsonSerializerGson();
		return omu.toJsonPretty(this);
	}

	public static SqlCsvExporter fromJson(String json) {
		JsonSerializer dillon = new JsonSerializerGson();
		return (SqlCsvExporter) dillon.toObjectFromJson(json, SqlCsvExporter.class);
	}

	public String getExportDateFormatString() {
		return exportDateFormatString;
	}

	public String getDateTimeFormatString() {
		return dateTimeFormatString;
	}

	public SimpleDateFormat getExportDateTimeFormat() {
		return exportDateTimeFormat;
	}

	public String getSql() {
		return sql;
	}

	public Binds getBinds() {
		return binds;
	}

	public SqlStatement getSqlStatement() {
		return sqlStatement;
	}

	public File getFile() {
		return file;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public List<ColumnMetadata> getColumnMeta() {
		return columnMeta;
	}

	public MutableDatasetMetadata getMetadata() {
		return metadata;
	}

	/**
	 * @return the databaseMetaData
	 */
	public LinkedHashMap<String, Object> getDatabaseMetaData() {
		return databaseMetaData;
	}

	public void setExportDateFormatString(String exportDateFormatString) {
		this.exportDateFormatString = exportDateFormatString;
	}

	public void setDateTimeFormatString(String dateTimeFormatString) {
		this.dateTimeFormatString = dateTimeFormatString;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setBinds(Binds binds) {
		this.binds = binds;
	}

	public void setColumnMeta(List<ColumnMetadata> columnMeta) {
		this.columnMeta = columnMeta;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setWriter(CsvWriter writer) {
		this.writer = writer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((binds == null) ? 0 : binds.hashCode());
		result = prime * result + ((columnMeta == null) ? 0 : columnMeta.hashCode());
		result = prime * result + ((columnNames == null) ? 0 : columnNames.hashCode());
		result = prime * result + ((databaseMetaData == null) ? 0 : databaseMetaData.hashCode());
		result = prime * result + ((dateTimeFormatString == null) ? 0 : dateTimeFormatString.hashCode());
		result = prime * result + ((exportDateFormat == null) ? 0 : exportDateFormat.hashCode());
		result = prime * result + ((exportDateFormatString == null) ? 0 : exportDateFormatString.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SqlCsvExporter other = (SqlCsvExporter) obj;
		if (binds == null) {
			if (other.binds != null)
				return false;
		} else if (!binds.equals(other.binds))
			return false;
		if (columnMeta == null) {
			if (other.columnMeta != null)
				return false;
		} else if (!columnMeta.equals(other.columnMeta))
			return false;
		if (columnNames == null) {
			if (other.columnNames != null)
				return false;
		} else if (!columnNames.equals(other.columnNames))
			return false;
		if (databaseMetaData == null) {
			if (other.databaseMetaData != null)
				return false;
		} else if (!databaseMetaData.equals(other.databaseMetaData))
			return false;
		if (dateTimeFormatString == null) {
			if (other.dateTimeFormatString != null)
				return false;
		} else if (!dateTimeFormatString.equals(other.dateTimeFormatString))
			return false;
		if (exportDateFormat == null) {
			if (other.exportDateFormat != null)
				return false;
		} else if (!exportDateFormat.equals(other.exportDateFormat))
			return false;
		if (exportDateFormatString == null) {
			if (other.exportDateFormatString != null)
				return false;
		} else if (!exportDateFormatString.equals(other.exportDateFormatString))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (sql == null) {
			if (other.sql != null)
				return false;
		} else if (!sql.equals(other.sql))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	void run(SqlCsvExporterArguments args) throws SQLException, IOException, ParseException {
		String[] upw = args.getUserNamePassword().split("/");
		DataSource ds = DataSourceFactory.getDataSource(args.getJDBCURL(), upw[0], upw[1]);

		this.connection = ds.getConnection();
		this.setSql(args.getSelectStatement());
		this.write(args.getOutFile());
	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {

		SqlCsvExporter invocation = new SqlCsvExporter();
		SqlCsvExporterArguments arguments = new SqlCsvExporterArguments();
		arguments.parseArguments(args);
		invocation.run(arguments);
	}

}
