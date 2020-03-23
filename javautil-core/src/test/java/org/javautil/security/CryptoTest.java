package org.javautil.security;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 */
public class CryptoTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String cipher = "PA$$WORD";

	@Test
	public void testCrypto() {
		// TODO new term
		final String encryptThis = "FOCUS10";

		logger.debug("Attempting to encrypt '" + encryptThis + "' to hex with the cipher " + cipher);

		final String encryptedValue = Crypto.encryptToHex(encryptThis, cipher);

		logger.debug("Encrypted value: " + encryptedValue);

		logger.debug("Decrypting with the cipher " + cipher);
		final String decrypted = Crypto.decryptFromHex(encryptedValue, cipher);
		logger.debug("Decrypted value: " + decrypted);
	}

}
