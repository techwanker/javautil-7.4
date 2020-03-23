package org.javautil.core.csv;

import java.util.List;

import org.junit.BeforeClass;

public class TabTokenizerTest extends CSVTokenizerTest {

	static CSVTokenizer csvTokenizer = new CSVTokenizer();

	// to write some tests
	List<String> got(final String in) {
		final String a = in.replace("~", "\"");
		final String b = a.replace("\"", "\t");
		return csvTokenizer.parse(b);
	}

	@BeforeClass()
	public static void beforeClass() {
		csvTokenizer.setDelimiter('\t');
	}

}
