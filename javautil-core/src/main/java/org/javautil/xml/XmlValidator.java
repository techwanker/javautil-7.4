package org.javautil.xml;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*
 * About caching the parsed schema
 * http://xerces.apache.org/xerces2-j/faq-grammars.html
 * http://xerces.apache.org/xerces2-j/features.html
 * 
 * http://www.dom4j.org/apidocs/org/dom4j/Document.html void
 * setEntityResolver(EntityResolver entityResolver) Sets the EntityResolver used
 * to find resolve URIs such as for DTDs, or XML Schema documents
 */
public class XmlValidator {
	private Document xdoc;

	/*
	 * Parsing the schema takes a whole lot longer than parsing the document for
	 * compliance with the schema.
	 * 
	 * @throws DocumentException
	 * 
	 */
	public void validateFile(final File xmlFile) throws DocumentException {
		final String featureName = "http://apache.org/xml/features/validation/schema";
		final long startMillis = System.currentTimeMillis();
		// turn validation on
		final SAXReader reader = new SAXReader(true);
		// request XML Schema validation
		try {
			reader.setFeature(featureName, true);
		} catch (final SAXException e) {
			throw new IllegalArgumentException("problem with setFeature " + featureName);
		}
		final long midMillis = System.currentTimeMillis();
		reader.setEntityResolver(new DbexpertsEntityResolver());
		xdoc = reader.read(xmlFile);
		final long endMillis = System.currentTimeMillis();
		xdoc = reader.read(xmlFile);
		final long secondParse = System.currentTimeMillis();
	}

	public class SimpleErrorHandler implements ErrorHandler {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		@Override
		public void warning(final SAXParseException e) throws SAXException {
			logger.error(e.getMessage());
			throw e;
		}

		@Override
		public void error(final SAXParseException e) throws SAXException {
			logger.error(e.getMessage());
			throw e;
		}

		@Override
		public void fatalError(final SAXParseException e) throws SAXException {
			logger.error(e.getMessage());
			throw e;
		}
	}
}
