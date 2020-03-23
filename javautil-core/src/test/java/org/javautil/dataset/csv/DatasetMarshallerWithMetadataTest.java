package org.javautil.dataset.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.ColumnMetadataSerializerCsv;
import org.javautil.dataset.Dataset;
import org.javautil.dataset.DatasetMetadata;
import org.javautil.dataset.testdata.TicketsDataset;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetMarshallerWithMetadataTest extends BaseTest {
	private final Logger        logger       = LoggerFactory.getLogger(getClass());
	private static final String expectedMeta = "src/test/resources/org/javautil/dataset/tickets.meta.csv";
	private static final String expectedData = "src/test/resources/org/javautil/dataset/tickets.data.csv";

	/**
	 * 
	 * Checks to make sure that marshalling is as expected.
	 * 
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void testMarshalling() throws IOException {
		final TicketsDataset td = new TicketsDataset();
		final Dataset ds = td.getDataset();
		final File metaFile = File.createTempFile("Tickets", ".meta.csv");
		final File dataFile = File.createTempFile("Tickets", ".data.csv");
		final FileOutputStream data = new FileOutputStream(dataFile);
		final FileOutputStream meta = new FileOutputStream(metaFile);
		DatasetMetadataMarshallerCsv.write(ds, data, meta);
		data.close();
		meta.close();
		checkMatch(new File(expectedData), dataFile);
	}

	/**
	 * TODO Checks to make sure that marshalling is as expected.
	 * 
	 * @throws IOException
	 */

	@Test
	public void testMetadataMarshalling() throws IOException {
		final TicketsDataset td = new TicketsDataset();
		final Dataset ds = td.getDataset();

		final File metaFile = new File(tmpDir + "/Tickets.meta.csv");
		final File expectedFile = new File(expectedMeta);
		final DatasetMetadata metadata = ds.getMetadata();
		ColumnMetadataSerializerCsv.writeToFile(metaFile, metadata.getColumnMetadata());
		logger.info("testMetadataMarshalling");
		checkMatch(expectedFile, metaFile);
	}

	@Ignore
	@Test
	public void testUnMarshall() throws IOException {
		final TicketsDataset td = new TicketsDataset();
		td.getDataset();

		final InputStream in = new FileInputStream(expectedMeta);
		final ColumnMetadataSerializerCsv unmarshaller = new ColumnMetadataSerializerCsv(in);
		final List<ColumnMetadata> meta = unmarshaller.readAll();
		assertEquals(4, meta.size());
		final ColumnMetadata column = meta.get(0);
		assertEquals("STATE", column.getColumnName());
		assertEquals(Integer.valueOf(0), column.getColumnIndex());
		assertEquals("STRING", column.getDataType());
		assertFalse(column.isExternalFlag());

		in.close();
		// now we need some tests
	}
}
