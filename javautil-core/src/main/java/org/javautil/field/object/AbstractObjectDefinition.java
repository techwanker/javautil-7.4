package org.javautil.field.object;

/**
 * Defines a single field in a fixed length octet array
 * 
 * @author jjs
 * 
 */
public abstract class AbstractObjectDefinition implements ObjectDefinition {
	// TODO add ranges for validation
	@Override
	public abstract Object getObject(final String externalRepresentation) throws ObjectParseException;

	@Override
	public boolean validate(final String externalRepresentation) {
		boolean retval = true;
		try {
			getObject(externalRepresentation);
		} catch (final ObjectParseException ope) {
			retval = false;
		}
		return retval;
	}
}
