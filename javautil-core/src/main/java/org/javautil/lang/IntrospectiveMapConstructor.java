package org.javautil.lang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javautil.lang.reflect.ClassCache;
import org.javautil.lang.reflect.FieldCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntrospectiveMapConstructor {
	private transient final Logger logger                = LoggerFactory.getLogger(getClass());

	private ClassCache             cache;

	private Map<String, Object>    map;

	private Object                 target;

	private ArrayList<Field>       missingRequiredFields = new ArrayList<>();

	public IntrospectiveMapConstructor(Object target, Map<String, Object> map) {
		this.map = map;
		this.target = target;
		this.cache = new ClassCache(target);
	}

	public void populate() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (FieldCacheEntry field : cache.getFields().values()) {
			// TODO check to see if required
			Object value = map.get(field.getFieldName());
			if (value == null) {
				logger.info("no value for {}", field.toString());
			} else {
				logger.info("value is {} for {}", value, field.toString());
				Method setter = getSetter(field, value);
				setter.invoke(target, value);
			}
		}
	}

	Method getSetter(FieldCacheEntry field, Object value) {
		String message;
		Method setter = null;
		List<Method> setters = field.getSetterMethods();
		if (setters == null) {
			message = String.format("field '%s' has no setter", field.getFieldName());
			throw new IllegalArgumentException(message);
		}
		switch (setters.size()) {
		case 0:
			break;
		case 1:
			setter = setters.get(0);
			break;
		default:
			message = String.format("field %s has too many setters", field.getFieldName());
			throw new IllegalArgumentException(message);
		}
		return setter;
	}
}
