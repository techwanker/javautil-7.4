package org.javautil.core.properties;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**


*/
class PropertyValue {
	private Date                          lastReferenced      = null;
	private int                           referenceCount      = 0;
	private String                        name                = null;
	private String                        value               = null;
	private ArrayList<PropertyLineParser> preemptedProperties = new ArrayList<PropertyLineParser>();

	/**
	 */
	PropertyValue(PropertyLineParser parser) {
		addParser(parser);
	}

	PropertyValue(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Date getLastReferencedTime() {
		return lastReferenced;
	}

	public String getName() {
		return name;
	}

	public int getReferenceCount() {
		return referenceCount;
	}

	public String getValue() {
		lastReferenced = new Date();
		referenceCount++;
		return value;
	}

	/**
	 * 
	 * @param text
	 * @deprecated This is not really deprecated, but I should rewrite the damn
	 *             thing. jjs
	 */
	@Deprecated
	private void parse(String text) {
		StringReader reader = new StringReader(text);
		StreamTokenizer tokenizer = new StreamTokenizer(reader);

		tokenizer.resetSyntax();
		/* By recollection this won't work with EBCDIC, should check !!! */
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('@', '@');
		tokenizer.wordChars('/', '/');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars(':', ':');
		tokenizer.wordChars(',', ',');
		tokenizer.wordChars('%', '%');
		tokenizer.wordChars(128 + 32, 255);
		tokenizer.whitespaceChars(0, ' ');
		// tokenizer.commentChar('/');
		tokenizer.quoteChar('"');
		tokenizer.quoteChar('\'');
		tokenizer.wordChars('.', '.');
		tokenizer.wordChars('$', '$');
		tokenizer.wordChars('{', '{');
		tokenizer.wordChars('}', '}');
		String tokenWord;

		try {
			tokenizer.nextToken();
			tokenWord = tokenizer.sval;
			name = tokenWord;

			tokenizer.nextToken();

			tokenizer.nextToken();
			value = tokenizer.sval;

		} catch (java.io.IOException e) {
			// not gonna happen this is a StringReader
		}
	}

	void addParser(PropertyLineParser parser) {
		parse(parser.getText());
		preemptedProperties.add(parser);
	}

	void addParsers(PropertyLineParser[] parsers) {
		for (int i = 0; i < parsers.length; i++) {
			preemptedProperties.add(parsers[i]);
		}
	}

	PropertyLineParser[] getParsers() {
		PropertyLineParser[] parsers = new PropertyLineParser[preemptedProperties.size()];
		Iterator it = preemptedProperties.iterator();
		for (int i = 0; it.hasNext(); i++) {
			parsers[i] = (PropertyLineParser) it.next();
		}
		return parsers;
	}

//	void setDefinition(PropertyDefinition definition) {
//	}

}
