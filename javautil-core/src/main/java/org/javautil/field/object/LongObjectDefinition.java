package org.javautil.field.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongObjectDefinition extends AbstractObjectDefinition {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public LongObjectDefinition() {
	}

	// TODO add ranges for validation
	@Override
	public Long getObject(final String externalRepresentation) throws ObjectParseException {
		try {
			return new Long(externalRepresentation);
		} catch (final Exception e) {
			throw new ObjectParseException(externalRepresentation, e);
		}
	}

}
