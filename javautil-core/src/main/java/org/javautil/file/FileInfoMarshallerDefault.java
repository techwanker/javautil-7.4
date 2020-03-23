package org.javautil.file;

import java.io.IOException;
import java.io.Writer;
import java.sql.Date;

import org.javautil.core.collections.ListHelper;
import org.javautil.core.csv.CsvWriter;
import org.javautil.core.text.CommonDateFormat;
import org.javautil.core.text.SimpleDateFormatter;
import org.javautil.lang.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInfoMarshallerDefault implements FileInfoListener, FileInfoMarshaller {
	private final Logger        logger        = LoggerFactory.getLogger(getClass());
	private final CsvWriter     csv           = new CsvWriter();
	private final Writer        writer;
	private SimpleDateFormatter dateFormatter = new SimpleDateFormatter(CommonDateFormat.ISO_DATE);
	private SimpleDateFormatter timeFormatter = new SimpleDateFormatter(CommonDateFormat.SECONDS);
	private String              sourceName    = null;

	public FileInfoMarshallerDefault(final Writer w) {
		if (w == null) {
			throw new IllegalArgumentException("writer is null");
		}
		this.writer = w;
		final SimpleDateFormatter sdf = new SimpleDateFormatter(CommonDateFormat.ISO_SECOND);
		// final SimpleDateFormat sdf2 = new
		// SimpleDateFormat(CommonDateFormat.ISO_SECOND);
		csv.setSimpleDateFormatter(sdf);
		csv.setSuppressTrailingNulls(false);
	}

	/**
	 * @see org.javautil.file.FileInfoMarshaller#processFileInfo(org.javautil.file.FileInfo)
	 **/
	@Override
	public void processFileInfo(final FileInfo fileInfo) throws IOException {
		final String rec = csv.asString(ListHelper.toList(sourceName, fileInfo.getDirectoryName(), fileInfo.getFileName(),
		    fileInfo.getFileBaseName(), fileInfo.getExtension(), dateFormatter.format(new Date(fileInfo.getLastModTime())),
		    timeFormatter.format(new Date(fileInfo.getLastModTime())), fileInfo.getLength(),
		    fileInfo.getMD5DigestAsString()));
		writer.write(rec);
		writer.write(SystemProperties.newline);

	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @param sourceName the sourceName to set
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * @return the fieldSeparator
	 */
	public String getFieldSeparator() {
		return csv.getFieldSeparator();
	}

	/**
	 * @param fieldSeparator the fieldSeparator to set
	 */
	public void setFieldSeparator(String fieldSeparator) {

		csv.setFieldSeparator(fieldSeparator);
	}
}
