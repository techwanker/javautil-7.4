package org.javautil.core.sql;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.javautil.core.text.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtilTest

{

	private final Logger logger = LoggerFactory.getLogger(getClass());

	//	private String regex = "(.)([.]+)";
	//	private String regex = "(.*)(;?)\\s+";
	private String regex = "(.*);[\\s|\\n]*";
	private String stripTrailingWhitespace = "(.*)([\\s|\\n]+)";
	//	private String regex = "([\\s]*)([.n]+)";
	//	private String regex = "([\\s]*)([.]+)(;?)([\\s]*)";
	String charArrayInfo(String text) {
		char[] chars = text.toCharArray();
		StringBuilder sb =  new StringBuilder();
		int i = 0;
		for (char s : chars) {
		  int val = s;
		  sb.append(i++ + ":" + val + ":'" + s + "' "); }
		return sb.toString();
}
	String charArrayInfo(char[] chars) {
			StringBuilder sb =  new StringBuilder();
			int i = 0;
			for (char s : chars) {
			int val = s;
				sb.append(i++ + ":" + val + " "); }
			return sb.toString();
	}

//	<T> boolean collectionContains(Collection<T> coll, <? extends T> o) {
//	    boolean retval = false;
//		for  (Object co : c) {
//			logger.info("co: {} {} o: {} {}",co.getClass(),co,o.getClass(),o);
//			if (co.equals(o)) {
//				retval = true;
//				break;
//			}
//		}
//		logger.info("collection: {} contains ? {} : {}", c, retval, o );
//		return retval;
//	}
	

	String trimSql(String text) {
		logger.info("trimming '{}'",text);
		Character[] whiteCruft = {'\t','\n','\r',' '};
		
		String leading = StringUtils.stripTrailing(text,whiteCruft);
		logger.info("leading: '{}'", leading);
		
		Character[] semi = {';'};
		String noSemi = StringUtils.stripTrailing(leading, semi);
		logger.info("noSemi '{}'",noSemi);
		
		String trimmed = StringUtils.stripLeading(noSemi,whiteCruft);

		logger.info("trimmed '{}'",trimmed);
		return trimmed;

	}

	
	/**
	 * Strips leading blank lines
	 * 
	 * Returns the input without the trailing semicolon or following 
	 * whitespace and newlines
	 * 
	 * 
	 * @param input
	 * @return The input with trailing semicolon and all subsequent whitespace and newlines removed
	 */
	String strip(String input) {
		return trimSql(input);
		//return extract (input,regex);
	}

	@Test
	public void test_01() {
		String input = "select 'x' from dual; ";
		String expected = "select 'x' from dual";
		assertEquals(expected,strip(input));
	}

	@Test
	public void test_02() {
		String input =    "select 'y' from dual  \n;  \n";
		String expected = "select 'y' from dual  \n";
		String actual = strip(input);
		logger.info("expected '{}'",expected);
		logger.info("actual   '{}'",actual);
		assertEquals(expected,actual);
	}



}
