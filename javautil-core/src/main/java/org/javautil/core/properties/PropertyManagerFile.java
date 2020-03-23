package org.javautil.core.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.javautil.core.text.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>PropertyManagerFile</code> class represents a set of properties.
 *
 * Each key and its corresponding value in the property list is a string.
 * <p>
 * A property list can contain another property list as its "defaults"; this
 * second property list is searched if the property key is not found in the
 * original property list.
 * <p>
 * 
 * @author Jim Schmidt
 * @version 0.1, 12/2002
 */
public class PropertyManagerFile {

	// private static final String className =
	// "com.javautil.property.PropertyManagerFile";
	private static final Logger           logger             = LoggerFactory
	    .getLogger(PropertyManagerFile.class.getName());
	private BufferedReader                in;
	private int                           physicalLineNumber = 0;
	/**
	 * parsed lines
	 */
	private ArrayList<PropertyLineParser> parsedLines        = new ArrayList<PropertyLineParser>();
	private ArrayList<PropertyLineParser> directiveLines     = null;
	private ArrayList<PropertyLineParser> definitions        = null;
	private ArrayList<PropertyValue>      propertyValues     = null;
	private PropertyLineParser[]          directives;
	private PropertyLineParser[]          parsedLineArray    = null;
	private File                          file;

	/**
	 */
	/**
	 * Strip all trailing spaces tabs and the line continuation character from a
	 * string.
	 * 
	 * If the string ends in a <code>\</code> followed by any number of spaces and
	 * tabs in any order, the continuation character and the following tabs and
	 * spaces are trimmed from the string.
	 * 
	 * @param line input
	 * @return chopped line
	 */
	public static String chopLine(String line) {
		String rc;
		int slashCount = 0;
		int index = line.length() - 1;
		int saveIndex;
		while ((index >= 0) && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
			index--;
		}
		saveIndex = index;
		while ((index >= 0) && (line.charAt(index--) == '\\')) {
			slashCount++;
		}
		if (slashCount % 2 == 1) {
			index = saveIndex;
		}
		rc = line.substring(0, index);
		return rc;
	}

	/**
	 * Returns true if the given line ends in a line continuation character
	 * <code>\</code>. Should strip trailing white characters !!!
	 * 
	 * @param line The line of text
	 * @return The line should be continued
	 */
	private static boolean continueLine(String line) {
		boolean rc;
		int slashCount = 0;
		int index = line.length() - 1;
		while ((index >= 0) && (line.charAt(index--) == '\\'))
			slashCount++;
		rc = slashCount % 2 == 1;
		return rc;
	}

	public PropertyManagerFile() {
	}

	/**
	 * @param fileName The input file
	 * @throws java.io.IOException Rarely
	 */
	public PropertyManagerFile(String fileName) throws java.io.IOException {

		file = new File(fileName);
		try {
			FileInputStream in = new FileInputStream(fileName);
			read(in);
			in.close();
			// writeAsHtml();
		} catch (java.io.IOException i) {
			throw new java.io.IOException("Unable to read " + fileName + "\n" + i.getMessage());
		}
	}

	/**
	 * @return the definitions
	 */
	public PropertyDefinition[] getDefinitions() {
		PropertyLineParser parser = null;
		PropertyDefinition[] rc = null;
		if (definitions == null) {
			definitions = new ArrayList<PropertyLineParser>();
			Iterator<PropertyLineParser> parsedIt = parsedLines.iterator();
			while (parsedIt.hasNext()) {
				PropertyLineParser line = parsedIt.next();
				if (line.lineType == PropertyLineParser.DEFINITION) {
					definitions.add(line);
				}
			}
		}
		rc = new PropertyDefinition[definitions.size()];
		//
		Iterator<PropertyLineParser> it = definitions.iterator();
		for (int i = 0; it.hasNext(); i++) {
			parser = it.next();
			PropertyDefinition definition = parser.getDefinition();
			rc[i] = definition;
		}
		return rc;
	}

	/**
	 * @return the parser
	 */
	public PropertyLineParser[] getDirectives() {
		if (directiveLines == null) {
			directiveLines = new ArrayList<PropertyLineParser>();
			for (PropertyLineParser plp : parsedLines) {
				directiveLines.add(plp);
			}
			directives = new PropertyLineParser[directiveLines.size()];
			Iterator<PropertyLineParser> dirIt = directiveLines.iterator();
			int i = 0;
			while (dirIt.hasNext()) {
				directives[i] = dirIt.next();
			}
		}
		return directives;
	}

	public PropertyLineParser[] getParsedLines() {
		int n = parsedLines.size();
		if (parsedLineArray == null) {
			parsedLineArray = new PropertyLineParser[n];
			Iterator<PropertyLineParser> parsedIt = parsedLines.iterator();
			for (int i = 0; parsedIt.hasNext(); i++) {
				parsedLineArray[i] = parsedIt.next();
			}
		}
		return parsedLineArray;
	}

	/**
	 * @return the definitions entries.
	 */
	public PropertyValue[] getPropertyValues() {
		populatePropertyValues();
		PropertyValue[] rc = new PropertyValue[propertyValues.size()];
		//
		Iterator<PropertyValue> it = propertyValues.iterator();
		for (int i = 0; it.hasNext(); i++) {
			rc[i] = it.next();
		}
		return rc;
	}

	/**
	 * Reads and processes a property file.
	 *
	 * The file is broken down into logical lines.
	 * <p>
	 * Every property occupies one line of the input stream. Each line is terminated
	 * by a line terminator (<code>\n</code> or <code>\r</code> or
	 * <code>\r\n</code>). Lines from the input stream are processed until end of
	 * file is reached on the input stream.
	 * <p>
	 *
	 * Line types are defined by <code>PropertyLineParser</code>.
	 * 
	 * @param inStream this input
	 * @throws IOException hopefully not
	 */
	public synchronized void read(InputStream inStream) throws IOException {
		int logicalLineNbr = 1;

		in = new BufferedReader(new InputStreamReader(inStream, "8859_1"));
		while (true) {
			// Get next line
			String line = getLine();
			if (line == null) {
				break;
			}
			PropertyLineParser parser = new PropertyLineParser(line, logicalLineNbr++);
			parsedLines.add(parser);

		}
		// logger.info("exiting read ");
	}

	/**
	 * Get a logical line from the input stream, accommodating line continuation.
	 */
	private String getLine() throws java.io.IOException {
		StringBuffer buff = new StringBuffer();
		physicalLineNumber++;
		String line = in.readLine();

		if (line == null) // end of file
			return null;

		if (line.length() > 0) {
			// Continue lines that end in slashes if they are not comments
			char firstChar = line.charAt(0);
			if ((firstChar != '#') && (firstChar != '!')) {
				while (continueLine(line)) {
					buff.append(chopLine(line));
					line = in.readLine();
					if (line == null)
						line = "";
					// Advance beyond whitespace on new line
					int firstNonWhite = StringUtils.firstNotIn(line, " \t");
					if (firstNonWhite > -1) {
						line = line.substring(firstNonWhite);
					}
				}
			}
		}
		buff.append(line);
		return new String(buff);
	}

	private void populatePropertyValues() {
		if (propertyValues == null) {
			propertyValues = new ArrayList<PropertyValue>();
			Iterator<PropertyLineParser> parsedIt = parsedLines.iterator();
			while (parsedIt.hasNext()) {
				PropertyLineParser line = parsedIt.next();
				if (line.lineType == PropertyLineParser.PROPERTY_VALUE) {
					propertyValues.add(line.getPropertyValue());
				}
			}
		}
	}

	String toHtml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<table>\n");
		buff.append("<tr>");
		buff.append("<th>File Name</th>");
		buff.append("<td>");
		buff.append(file.getName());
		buff.append("</td>");
		buff.append("</tr>\n");
		//
		buff.append("<tr>");
		buff.append("<th>Canonical File Name</th>");
		buff.append("<td>");
		try {
			buff.append(file.getCanonicalPath());
		} catch (java.io.IOException e) {
			buff.append(e.getMessage());
		}
		buff.append("</td>");
		buff.append("</tr>");
		buff.append("</table>\n");

		buff.append("<table>");
		buff.append("<tr>");
		buff.append("<th>Logical<br>Line #</th>");
		buff.append("<th>Line type</th>");
		buff.append("<th>key</th>");
		buff.append("<th>value</th>");
		buff.append("</tr>\n");
		for (int i = 0; i < parsedLineArray.length; i++) {
			PropertyLineParser line = parsedLineArray[i];
			buff.append("<tr>");
			//
			buff.append("<td>");
			buff.append(line.getLogicalLineNbr());
			buff.append("</td>");
			//
			buff.append("<td>");
			buff.append(line.getLineType());
			buff.append("</td>");
			//
			buff.append("<td>");
			switch (line.getLineTypeNbr()) {
			case PropertyLineParser.DEFINITION:
				buff.append(line.getDefinition().getPropertyName());
				break;
			case PropertyLineParser.DESCRIPTION:
				buff.append(line.getDescription().getPropertyName());
				break;
			case PropertyLineParser.PROPERTY_VALUE:
				buff.append(line.getPropertyValue().getName());
				break;
			default:
				buff.append(line.getKey() == null ? "&nbsp;" : line.getKey());
				break;
			}
			buff.append("</td>");
			//
			buff.append("<td>");

			switch (line.getLineTypeNbr()) {
			case PropertyLineParser.DEFINITION:
				buff.append(line.getDefinition().describe());
				break;
			case PropertyLineParser.DESCRIPTION:
				buff.append(line.getDescription().getDescription());
				break;
			case PropertyLineParser.PROPERTY_VALUE:
				buff.append(line.getPropertyValue().getValue());
				break;
			default:
				buff.append(line.getValue() == null ? "&nbsp;" : line.getValue());
				break;
			}

			buff.append("</td>");
			//
			buff.append("</tr>\n");
		}
		buff.append("</table>\n");
		return new String(buff);
	}
}
