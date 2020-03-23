package org.javautil.core.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test() {
		assertEquals("icBin", StringUtils.asAttributeName("ic_bin"));
	}

	@Test
	public void oneLineNoNewline() {
		final String text = "text";
		assertEquals(0, StringUtils.newlineCount(text));
		assertEquals(1, StringUtils.lineCount(text));
	}

	@Test
	public void oneLineNewline() {
		final String text = "text with newline\n";
		assertEquals(1, StringUtils.newlineCount(text));
		assertEquals(1, StringUtils.lineCount(text));
	}

	@Test
	public void oneNewLine() {
		final String text = "\n";
		assertEquals(1, StringUtils.newlineCount(text));
		assertEquals(1, StringUtils.lineCount(text));
	}

	@Test
	public void oneCRNewLine() {
		final String text = "\r\n";
		assertEquals(1, StringUtils.newlineCount(text));
		assertEquals(1, StringUtils.lineCount(text));
	}

	@Test
	public void trailingNewLines() {
		final String text = "line1\n\n\n";
		assertEquals(3, StringUtils.newlineCount(text));
		assertEquals(3, StringUtils.lineCount(text));
	}

	@Test
	public void intermediateNewLinesNoFinalNewLine() {
		final String text = "line1\n\n\nLine 4";
		assertEquals(3, StringUtils.newlineCount(text));
		assertEquals(4, StringUtils.lineCount(text));
	}

	@Test
	public void blankLinesNoNewLineAtEnd() {
		final String text = "line 1\n\n\nline 4";
		assertEquals(3, StringUtils.newlineCount(text));
		assertEquals(4, StringUtils.lineCount(text));
	}

	@Test
	public void testPrettyName() {
		assertEquals("Cust Cd", StringUtils.toPrettyName("cust_cd"));
		assertEquals("Cust Cd", StringUtils.toPrettyName("cust_cd"));
	}
}
