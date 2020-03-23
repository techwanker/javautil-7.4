package org.javautil.field.object;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateObjectDefinitionTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testDate() throws ObjectParseException {
		DateObjectDefinition def = new DateObjectDefinition("yyyy/MM/dd");
		Date date = def.getObject("2010/04/28");
		logger.debug(date.toString());
	}
}
