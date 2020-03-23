package org.javautil.commandline;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ReflectUtils {

	protected static final String TAB = "\t";

	protected static final String NEWLINE = System.getProperty("line.separator");

	public static String[] listPropertiesAsArray(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		String[] properties = null;

		final Class<? extends Object> clazz = object.getClass();

		final Field[] fields = clazz.getDeclaredFields();
		properties = new String[fields.length];
		int i = 0;
		for (final Field field : fields) {
			// todo jjs don't add if synthetic?
			if (!field.isSynthetic()) {
				field.setAccessible(true); // todo why is this being set
				// accessible?
				properties[i++] = field.getName();
			}
		}

		return properties;
	}

	public static String listPropertiesAsString(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		final StringBuilder buff = new StringBuilder();

		final Class<? extends Object> clazz = object.getClass();

		// }
		buff.append(clazz.getName());
		buff.append(" {");
		buff.append(NEWLINE);
		for (final Field field : clazz.getDeclaredFields()) {
			// todo why check synthetic
			if (!field.isSynthetic()) {
				field.setAccessible(true);
				buff.append(TAB);
				buff.append(field.getName() + ":");
				buff.append(TAB);
				try {
					buff.append(field.get(object));
				} catch (final IllegalAccessException e) {
					throw new IllegalStateException("can't access field " + field);
				}
				buff.append(NEWLINE);
			}
		}
		buff.append("}");
		buff.append(NEWLINE);

		return buff.toString();
	}

//	/**
//	 * Returns the Field with the specified field name from the specified class.
//	 * 
//	 * If the class is not present it returns null, we also have 
//	 * 
//	 * @param _clazz 
//	 *     The class to be processed
//	 * @param fieldName
//	 *     The name of the field
//	 * @return
//	 *     the field
//	 * @throws SecurityException
//	 */
	public static Field getField(final Class<? extends Object> _clazz, final String fieldName)
			throws SecurityException {
		Class<? extends Object> clazz = _clazz;
		Field field = null;
		while (field == null && clazz != null && !clazz.equals(Object.class)) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (final NoSuchFieldException e) {
				// todo jjs wtf?
				// this is okay, we intend to drill into the superclass next...
			}
			clazz = clazz.getSuperclass(); // todo jjs this is assigned but
			// never used
		}
		return field;
	}


	public static Field getFieldByName(final Class<? extends Object> _clazz, final String fieldName) {
		if (_clazz == null) {
			throw new IllegalArgumentException("_clazz is null");
		}
		if (fieldName == null) {
			throw new IllegalArgumentException("fieldname is null");
		}
		Field f = null;
		try {
			f = getField(_clazz, fieldName);
		} catch (final SecurityException e) {
			throw new IllegalStateException(e);
		}
		return f;
	}

	public static Field getClassField(final Class<? extends Object> _clazz, final String fieldName) {
		if (_clazz == null) {
			throw new IllegalArgumentException("_clazz is null");
		}
		if (fieldName == null) {
			throw new IllegalArgumentException("fieldName is null");
		}

		Field field = null;
		try {
			field = _clazz.getDeclaredField(fieldName);
		} catch (final NoSuchFieldException e) {
			throw new IllegalStateException("the field " + fieldName + " is not in the class " + _clazz.getName());
		}
		return field;

	}

	public static Map<Class<? extends Object>, Collection<Field>> getFieldsInClass( final Class<? extends Object> _clazz) {
		final Map<Class<? extends Object>, Collection<Field>> fieldsByClass = new HashMap<Class<? extends Object>, Collection<Field>>();
		Class<? extends Object> clazz = _clazz;
		// todo jjs come back and review this
		while (clazz != null && !clazz.equals(Object.class)) {
			final Collection<Field> fields = Arrays.asList(clazz.getDeclaredFields());
			fieldsByClass.put(clazz, fields);
			clazz = clazz.getSuperclass();
		}
		return fieldsByClass;
	}
}
