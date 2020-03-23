package org.javautil.field.object;

import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingStringObjectDefinition extends AbstractObjectDefinition implements ObjectDefinition {

	private final Logger                logger = LoggerFactory.getLogger(getClass());

	private WeakHashMap<String, String> map    = new WeakHashMap<String, String>();

	public CachingStringObjectDefinition() {
	}

	@Override
	public String getObject(String externalRepresentation) throws ObjectParseException {
		String cached = map.get(externalRepresentation);
		if (cached == null) {
			cached = externalRepresentation;
			map.put(externalRepresentation, externalRepresentation);
		}
		return cached;
	}

}
