package org.javautil.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShaHasherTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testBase64() {
		String hash = ShaHasher.hashAsBase64("wtf");
		logger.debug("len " + hash.length());
		assertEquals("GDe8LFRtRscFIEz5+Fe5Cx2//Sp5iEUWcBGZRbo5oQs=", hash);
	}

	@Test
	public void testHex() {
		String hash = ShaHasher.hashAsHex("wtf");

		assertEquals("1837bc2c546d46c705204cf9f857b90b1dbffd2a7988451670119945ba39a10b", hash);
	}

}
