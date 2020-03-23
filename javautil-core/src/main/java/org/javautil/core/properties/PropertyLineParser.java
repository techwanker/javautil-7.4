package org.javautil.core.properties;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

class PropertyLineParser {
	// values for line type
	public static final int UNKNOWN        = -1;
	public static final int EMPTY          = 0;
	public static final int COMMENT        = 1;
	public static final int DIRECTIVE      = 2;
	public static final int PROPERTY       = 3;
	public static final int DEFINITION     = 4;
	public static final int DESCRIPTION    = 5;
	public static final int PROPERTY_VALUE = 6;

	/**
	 * The logical line number from the input file.
	 * 
	 * Continuation line characters are treated specially in this calculation. The
	 * logical line number could be significantly less than the physical line
	 * number.
	 */

	private int             logicalLineNbr;

	/** what does this contain ? */
	private String          line;
	private String          text;
	private Object          payLoad        = null;
	int                     len;
	String                  directive;
	String                  key;
	String                  value;
	char                    firstChar;

	int                     lineType       = UNKNOWN;

	//
	//
	/**
	 * Determine the type of line.
	 * <ul>
	 * <li>Comment - line that begins with '#'</li>
	 * <li>Directive or Definition - line that begins with $</li>
	 * </ul>
	 *
	 * A line that contains only whitespace or whose first non-whitespace character
	 * is an ASCII <code>#</code> or <code>!</code> is a comment. (thus,
	 * <code>#</code> or <code>!</code> indicate comment lines).
	 *
	 * <p>
	 *
	 * A line first non-whitespace is a <code>$</code> is a directive.
	 *
	 *
	 * Line types are defined by <code>PropertyLineParser</code> Every line other
	 * than a blank line or a comment line describes one property to be added to the
	 * table (except that if a line ends with \, then the following line, if it
	 * exists, is treated as a continuation line, as described below). The key
	 * consists of all the characters in the line starting with the first
	 * non-whitespace character and up to, but not including, the first ASCII
	 * <code>=</code>, <code>:</code>, or whitespace character. All of the key
	 * termination characters may be included in the key by preceding them with a \.
	 *
	 * Any whitespace after the key is skipped; if the first non-whitespace
	 * character after the key is <code>=</code> or <code>:</code>, then it is
	 * ignored and any whitespace characters after it are also skipped.
	 *
	 * All remaining characters on the line become part of the associated element
	 * string. Within the element string, the ASCII escape sequences
	 * <code>\t</code>, <code>\n</code>, <code>\r</code>, <code>\\</code>,
	 * <code>\"</code>, <code>\'</code>, <code>\ &#32;</code> &#32;(a backslash and
	 * a space), and <code>&#92;u</code><i>xxxx</i> are recognized and converted to
	 * single characters. Moreover, if the last character on the line is
	 * <code>\</code>, then the next line is treated as a continuation of the
	 * current line; the <code>\</code> and line terminator are simply discarded,
	 * and any leading whitespace characters on the continuation line are also
	 * discarded and are not part of the element string.
	 *
	 * <p>
	 * As an example, each of the following four lines specifies the key
	 * <code>"Truth"</code> and the associated element value <code>"Beauty"</code>:
	 * <p>
	 * 
	 * <pre>
	 * Truth = Beauty
	 *   Truth:Beauty
	 * Truth         :Beauty
	 * </pre>
	 * 
	 * As another example, the following three lines specify a single property:
	 * <p>
	 * 
	 * <pre>
	 * fruits           apple, banana, pear, \
	 *                                  cantaloupe, watermelon, \
	 *                                  kiwi, mango
	 * </pre>
	 * 
	 * The key is <code>"fruits"</code> and the associated element is:
	 * <p>
	 * 
	 * <pre>
	 * "apple, banana, pear, cantaloupe, watermelon, kiwi, mango"
	 * </pre>
	 * 
	 * Note that a space appears before each <code>\</code> so that a space will
	 * appear after each comma in the final result; the <code>\</code>, line
	 * terminator, and leading whitespace on the continuation line are merely
	 * discarded and are <i>not</i> replaced by one or more other characters.
	 * <p>
	 * As a third example, the line:
	 * <p>
	 * 
	 * <pre>
	 * cheeses
	 * </pre>
	 * 
	 * specifies that the key is <code>"cheeses"</code> and the associated element
	 * is the empty string.
	 * <p>
	 * 
	 * @exception IOException if an error occurred when reading from the input
	 *                        stream.
	 */

	PropertyLineParser(String text, int logicalLineNbr) throws java.io.IOException {
		// local variables
		// last token lexed
		int tokenType; // type of token, types defined in StreamTokenizer
		// String wordToken; // the token if it is a word
		// double numberToken; // the token if it is a number
		// store arguments
		this.text = text;
		// this.line = line;
		this.logicalLineNbr = logicalLineNbr;
		// this.physicalLineNbr = physicalLineNbr;

		// construct tokenizer
		StringReader reader = new StringReader(text);
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
		tokenizer.quoteChar('"');
		//

		// get the first word
		tokenType = tokenizer.nextToken();
		String tokenWord = tokenizer.sval;
		switch (tokenType) {
		case '#':
			lineType = COMMENT;
			value = line;
			break;

		case StreamTokenizer.TT_WORD:
			if (tokenWord.equals("define")) {
				lineType = DEFINITION;
				payLoad = new PropertyDefinition(text);
				break;
			}
			if (tokenWord.equals("describe")) {
				lineType = DESCRIPTION;
				payLoad = new Description(text);
				break;
			}

			// get property name
			tokenizer.nextToken(); // throw it away
			key = tokenWord = tokenizer.sval;

			// get separator
			tokenizer.nextToken(); // separator
			tokenWord = tokenizer.sval;

			// get value
			tokenizer.nextToken();
			value = tokenWord = tokenizer.sval;

			payLoad = new PropertyValue(this);
			lineType = PROPERTY_VALUE;

			break;
		case StreamTokenizer.TT_EOF:
			lineType = EMPTY;
			break;
		default:
		}
	}

	/**
	 * Returns the key if this is a property.
	 * 
	 * @return property key.
	 */
	public String getKey() {
		return key;
	}

	public String getLineType() {
		String rc = null;
		switch (lineType) {
		case UNKNOWN:
			rc = "Unknown";
			break;
		case EMPTY:
			rc = "Empty";
			break;
		case COMMENT:
			rc = "Comment";
			break;
		case DIRECTIVE:
			rc = "Directive";
			break;
		case DEFINITION:
			rc = "Definition";
			break;
		case DESCRIPTION:
			rc = "Description";
			break;
		case PROPERTY:
			rc = "Property";
			break;
		default:
			rc = "????";
			break;
		}
		return rc;
	}

	public int getLineTypeNbr() {
		return lineType;
	}

	public int getLogicalLineNbr() {
		return logicalLineNbr;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}

	public boolean isDirective() {
		boolean rc = false;
		if (lineType == DIRECTIVE) {
			rc = true;
		}
		return rc;
	}

	PropertyDefinition getDefinition() {
		if (lineType != DEFINITION) {
			throw new java.lang.IllegalStateException("This line does not contain a definition");
		}
		return (PropertyDefinition) payLoad;
	}

	Description getDescription() {
		if (lineType != DESCRIPTION) {
			throw new java.lang.IllegalStateException("This line does not contain a description");
		}
		return (Description) payLoad;
	}

	PropertyValue getPropertyValue() {
		if (lineType != PROPERTY_VALUE) {
			throw new java.lang.IllegalStateException("This line does not contain a property value");
		}
		return (PropertyValue) payLoad;
	}
}
