package org.javautil.xml;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.io.DocumentSource;

public class TransformerHelper {
	private static TransformerFactory factory     = TransformerFactory.newInstance();

	private Transformer               transformer = null;

	private DocumentSource            documentSource;

	public void setTransformer(File xslFile) throws TransformerConfigurationException {
		if (xslFile == null) {
			throw new IllegalArgumentException("xslFile is null");
		}
		if (!xslFile.exists()) {
			throw new IllegalArgumentException(xslFile.getAbsolutePath() + " does not exist");
		}
		if (!xslFile.canRead()) {
			throw new IllegalArgumentException("can't read " + xslFile.getAbsolutePath());
		}
		StreamSource transformerStreamSource = new StreamSource(xslFile.getAbsolutePath());
		transformer = factory.newTransformer(transformerStreamSource);

	}

	public void setDocumentSource(Document doc) {
		documentSource = new DocumentSource(doc);
	}

	public void setXslParameters(HashMap<String, String> parameters) {
		if (transformer == null) {
			throw new IllegalStateException("transformer is null, precede with all to setTransformer");
		}
		for (Entry<String, String> param : parameters.entrySet()) {
			String name = param.getKey();
			String value = param.getValue();
			transformer.setParameter(name, value);
		}
	}

	/*
	 * Use if previously called setDocumentSource
	 * 
	 * @return
	 * 
	 * @throws TransformerException
	 */
	public String getTransformedXml() throws TransformerException {
		if (documentSource == null) {
			throw new IllegalStateException("documentSource has not been called");
		}
		if (transformer == null) {
			throw new IllegalStateException(" transformer is null should have called setTransformer");
		}
		StringWriter writer = new StringWriter();
		StreamResult output = new StreamResult(writer);
		transformer.transform(documentSource, output);
		return writer.toString();
	}

	/*
	 * Use if previously called setDocumentSource
	 * 
	 * @throws TransformerException
	 */
	public void writeTransformedXml(Writer writer) throws TransformerException {
		if (documentSource == null) {
			throw new IllegalStateException("documentSource has not been called");
		}
		if (transformer == null) {
			throw new IllegalStateException(" transformer is null should have called setTransformer");
		}

		StreamResult output = new StreamResult(writer);
		transformer.transform(documentSource, output);

	}
}
