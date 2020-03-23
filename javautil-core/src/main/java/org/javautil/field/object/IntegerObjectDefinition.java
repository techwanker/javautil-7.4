package org.javautil.field.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerObjectDefinition extends AbstractObjectDefinition {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public IntegerObjectDefinition() {
	}

	// TODO add ranges for validation
	@Override
	public Integer getObject(final String externalRepresentation) throws ObjectParseException {
		try {
			return new Integer(externalRepresentation);
		} catch (final Exception e) {
			throw new ObjectParseException(externalRepresentation, e);
		}
	}

}
