package org.javautil.document.renderer;

import javax.xml.transform.stream.StreamResult;

import org.javautil.dataset.Dataset;
import org.javautil.document.MimeType;

/**
 * 
 * @author jjs
 * 
 */
public interface RendererRequest {

	public MimeType getMimeType();

	public void setMimeType(final MimeType mimeType);

	public StreamResult getStreamResult();

	public void setStreamResult(StreamResult sr);

	public Dataset getDataset();

	/**
	 * 
	 * @param DateFormat a String compatible with SimpleDateFormat
	 */
	public void setDateFormat(String DateFormat);

	/**
	 * A String compatible with SimpleDateFormat
	 * 
	 * @return the SimpleDateFormat string
	 */
	public String getDateFormat();

}
