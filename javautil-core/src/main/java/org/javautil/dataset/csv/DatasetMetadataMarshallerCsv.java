package org.javautil.dataset.csv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.ColumnMetadataSerializerCsv;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetMetadata;
import org.javautil.dataset.DatasetMetadataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */
public class DatasetMetadataMarshallerCsv {

	private static Logger logger = LoggerFactory.getLogger(DatasetMetadataMarshallerCsv.class);

	DatasetMetadata getMetadata(final InputStream inputStream) throws IOException {
		List<ColumnMetadata> columns = null;
		final ColumnMetadataSerializerCsv marshaller = new ColumnMetadataSerializerCsv(inputStream);
		columns = marshaller.readAll();
		final DatasetMetadataImpl meta = new DatasetMetadataImpl(columns);
		return meta;
	}

	public static void write(final Dataset ds, final OutputStream os, final OutputStream metaOutputStream)
	    throws IOException {
		// List<ColumnMetadata> columns = ds.getMetadata().getColumnMetadata();
		// ColumnMetadataCsvMarshaller marshaller = new
		// ColumnMetadataCsvMarshaller(os);
		// marshaller.write(metaOutputStream, columns);
		// TODO FIX
		// throw new UnsupportedOperationException();
		DatasetCsvMarshaller.write(ds, os);
		ColumnMetadataSerializerCsv.write(metaOutputStream, ds.getMetadata().getColumnMetadata());

	}

	public static void write(final OutputStream metaOutputStream, final DatasetMetadata meta) throws IOException {
		logger.info("about to write meta " + meta.toString());
		ColumnMetadataSerializerCsv.write(metaOutputStream, meta.getColumnMetadata());
	}

	public static void write(final OutputStream metaOutputStream, final Dataset ds) throws IOException {

		ColumnMetadataSerializerCsv.write(metaOutputStream, ds.getMetadata().getColumnMetadata());

	}

	public static void writeToFileName(final Dataset ds, final String CSVFileName, final String metaFileName)
	    throws IOException {
		final FileOutputStream csvFos = new FileOutputStream(CSVFileName);
		final FileOutputStream metaOs = new FileOutputStream(metaFileName);
		write(ds, csvFos, metaOs);
		csvFos.close();
		metaOs.close();
	}

}
