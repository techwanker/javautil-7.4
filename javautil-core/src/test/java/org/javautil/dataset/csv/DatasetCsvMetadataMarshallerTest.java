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
import org.javautil.dataset.DatasetMarshaller;
import org.javautil.dataset.ExtendedSalesDataset;
import org.javautil.dataset.testdata.TicketsDataset;
import org.javautil.file.FileHelper;
import org.javautil.io.FileUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetCsvMetadataMarshallerTest extends BaseTest {
	private static final String expectedMeta = "./src/test/resources/org/javautil/dataset/ExtendedSales.meta.csv";

	private final Logger        logger       = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 * Checks to make sure that marshalling is as expected.
	 * 
	 * @throws IOException
	 */

	@Test
	public void testMarshalling() throws IOException {
		final ExtendedSalesDataset salesData = new ExtendedSalesDataset();
		final DatasetMarshaller marshaller = new DatasetMarshaller();
		final Dataset ds = salesData.getDataset();
		ds.getMetadata();
		final File metaFile = new File(tmpDir, "ExtendedSales.meta.csv");
		final File dataFile = new File(tmpDir, "ExtendedSales.data.csv");
		final FileOutputStream dataExpected = new FileOutputStream(dataFile);
		final FileOutputStream metaExpected = new FileOutputStream(metaFile);
		logger.debug("metaFile " + metaFile);
		String expectedMetaString = FileUtil.getAsString(metaFile.getPath());
		logger.info("expected:\n{}", expectedMetaString);
		marshaller.writeMetadata(ds, metaExpected);
		dataExpected.close();
		metaExpected.close();
		int contentsMatch = FileHelper.fileContentsMatch(new File(expectedMeta), metaFile);
		if (!(contentsMatch == 0)) {
			logger.error(
			    String.format("expected: %s %s", new File(expectedMeta).getAbsolutePath(), metaFile.getAbsolutePath()));
		}
		checkMatch(new File(expectedMeta), metaFile);
		// checkMatch(new File(expectedMeta) ,metaFile);

	}

	/**
	 * 
	 * Checks to make sure that marshalling is as expected.
	 * 
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void testMetadataMarshalling() throws IOException {
		final TicketsDataset td = new TicketsDataset();
		final Dataset ds = td.getDataset();

		final File metaFile = new File(tmpDir + "/Tickets.meta.csv");
		final FileOutputStream meta = new FileOutputStream(metaFile);

		ColumnMetadataSerializerCsv.writeToFile(metaFile, ds.getMetadata().getColumnMetadata());
		meta.close();
		assertEquals(0, FileHelper.fileContentsMatch(metaFile, new File(expectedMeta)));
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
