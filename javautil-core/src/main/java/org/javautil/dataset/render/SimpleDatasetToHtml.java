package org.javautil.dataset.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.javautil.dataset.Dataset;

public class SimpleDatasetToHtml {

	private Dataset dataset;

	public SimpleDatasetToHtml(Dataset dataset) {
		this.dataset = dataset;
	}

	public String toString(Document document) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos);
		XMLWriter writer = new XMLWriter(osw, format);
		try {
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toString();
	}

	public String toHtml() {
		Document doc = DocumentFactory.getInstance().createDocument();
		Element tableElement = doc.addElement("table");
		Element theadElement = tableElement.addElement("thead");
		Element trElement = theadElement.addElement("tr");
		return toString(doc);
	}

}
