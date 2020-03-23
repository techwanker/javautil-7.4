package org.javautil.dataset;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javautil.core.csv.CSVTokenizer;
import org.javautil.core.csv.CsvWriter;
import org.javautil.core.text.AsString;
import org.javautil.document.style.HorizontalAlignment;
import org.javautil.lang.Coerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnMetadataSerializerCsv implements ColumnMetadataSerializer {

	private BufferedReader             reader;

	private CSVTokenizer               tokenizer;

	private boolean                    serializer;


	private final static Logger        logger   = LoggerFactory.getLogger(ColumnMetadataSerializerCsv.class);

	private static final String        newline  = System.getProperty("line.separator");

	private final AsString             asString = new AsString();

	private Collection<ColumnMetadata> columnMetadata;

	public ColumnMetadataSerializerCsv(Collection<ColumnMetadata> columnMetadata) {
		this.columnMetadata = columnMetadata;
	}

	public ColumnMetadataSerializerCsv(final Reader reader) {
		this.reader = new BufferedReader(reader);
		tokenizer = new CSVTokenizer();
	}

	// @Deprecated
	public ColumnMetadataSerializerCsv(final InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
		tokenizer = new CSVTokenizer();
	}

	public static void writeToFile(final File file, final Collection<ColumnMetadata> meta) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file is null");
		}
		final OutputStream os = new FileOutputStream(file);
		final ColumnMetadataSerializerCsv marshaller = new ColumnMetadataSerializerCsv(meta);
		marshaller.write(os);
		os.close();
	}

	public ColumnMetadata readLine() throws IOException {
		if (reader == null) {
			throw new IllegalStateException(
			    "was not constructed with ColumnMetadataCsvMarshaller(InputStream is) or ColumnMetadataCsvMarshaller(Reader reader) and attempt to read was made");
		}
		final String line = reader.readLine();
		ColumnMetadata retval = null;
		if (line != null) {
			final List<String> tokens = tokenizer.parse(line);

			for (int padLength = 16 - tokens.size(); padLength > 0; padLength--) {
				tokens.add(null);
			}
			retval = parse(tokens);
		}
		return retval;
	}

	@Override
	public ArrayList<ColumnMetadata> readAll() throws IOException {
		final ArrayList<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();
		ColumnMetadata column;
		while ((column = readLine()) != null) {
			columns.add(column);
		}
		return columns;
	}


	public Integer getInteger(String in) {
		return in == null ? null : Integer.parseInt(in);
	}

	// TODO Isn't there another function that does this
	public ColumnMetadata parse(final List<String> tokens) {
		final ColumnMetadata cm = new ColumnMetadata();
		int i = 0;
		cm.setColumnName(tokens.get(i++));
		cm.setColumnIndex(getInteger(tokens.get(i++)));
		cm.setDataTypeName(tokens.get(i++));
		cm.setHeading(tokens.get(i++));
		cm.setLabel(tokens.get(i++));
		cm.setPrecision(getInteger(tokens.get(i++)));
		cm.setScale(getInteger(tokens.get(i++)));
		cm.setColumnDisplaySize(getInteger(tokens.get(i++)));
		cm.setComments(tokens.get(i++));
		cm.setExternalFlag(Coerce.asBoolean(tokens.get(i++)));
		cm.setAttributeName(tokens.get(i++));
		cm.setWorkbookFormula(tokens.get(i++));
		cm.setExcelFormat(tokens.get(i++));
		cm.setJavaFormat(tokens.get(i++));
		cm.setHorizontalAlignment(HorizontalAlignment.valueOf(tokens.get(i++)));
		cm.setAggregateFunction(tokens.get(i++));
		cm.setGroupName(tokens.get(i++));
		return cm;
	}

	public static void write(final OutputStream os, final Collection<ColumnMetadata> columnMetadata) throws IOException {
		final ColumnMetadataSerializerCsv marshaller = new ColumnMetadataSerializerCsv(columnMetadata);
		marshaller.write(os);
	}

	@Override
	public void write(OutputStream os) throws IOException {
		Writer writer = new OutputStreamWriter(os);
		write(writer);
	}

	@Override
	public void write(Writer writer) throws IOException {
		CsvWriter csvWriter = new CsvWriter(writer);
		for (final ColumnMetadata meta : columnMetadata) {
			ArrayList<Object> col = meta.toObjectList();
			logger.debug("col is:\n{}", col);
			csvWriter.writeList(col);
		}
		writer.flush();
	}

	@Override
	public String asString() {
		if (!serializer) {
			throw new RuntimeException("is not a serializer, instantiate with the metadata");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			write(baos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toString();
	}
}
