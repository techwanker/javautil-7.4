package org.javautil.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.javautil.core.misc.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReflectUtils {
	private static final transient Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
	protected static final String TAB     = "\t";

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

	// TODO should call listPropertiesAsArray and then allow an injected
	// formatter
	public static String listPropertiesAsString(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		final StringBuilder buff = new StringBuilder();

		final Class<? extends Object> clazz = object.getClass();

		// try {
		// clazz = Class.forName(object.getClass().getName());
		// } catch (ClassNotFoundException e) {
		// throw new IllegalStateException(e);
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
	//	 * If the class is not present it returns null, we also have // todo jjs
	//	 * some lo
	//	 * 
	//	 * @param _clazz
	//	 * @param fieldName
	//	 * @return
	//	 * @throws SecurityException
	//	 * @throws NoSuchFieldException
	//	 */
	public static Field getField(final Class<? extends Object> _clazz, final String fieldName)
			throws SecurityException, NoSuchFieldException {
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

	// TODO jjs what is the the difference between this and getFieldByName?
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
		} catch (final NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
		return f;
	}

	//	/**
	//	 * Get the specified field name in the class. TODO jjs JFC what is the the
	//	 * difference between this and getFieldByName and getField?
	//	 * 
	//	 * @param _clazz
	//	 * @param fieldName
	//	 * @return
	//	 * @throws SecurityException
	//	 * @throws NoSuchFieldException
	//	 */
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

	public static Map<Class<? extends Object>, Collection<Field>> getFieldsInClass(final Class<? extends Object> _clazz) {
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

	// TODO should add is type for Boolean but not need 
	public static  Method getGetter(Map<String,Method> methods, Field field) {
		String getterMethodName = getGetterMethodName(field.getName());
		Method retval = methods.get(getterMethodName);
		return retval;	
	}
	public  static Method getGetter(Class clazz, Field field) {
		Method retval = null;
		String getterMethodName = getGetterMethodName(field.getName());

		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(getterMethodName)) {
				retval = method;
				break;
			}
		}

		return retval;
	}

	@Deprecated // use getGetterMethodName(Field field)
	public static String getGetterMethodName(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	public static Set<String> getDateFieldNames(Object anObject) {
		Timer t = new Timer();
		Set<String> retval = getDateFieldNames(getDateFields(anObject));
		logger.trace("getDataFieldNames elapsed millis " + t.getElapsedMillis());
		return retval;
	}
	public static Set<String> getDateFieldNames(List<Field> dateFields) {
		Set<String> dateFieldNames = new HashSet<String>();
		for (Field dateField : dateFields) {
			dateFieldNames.add(dateField.getName());
		}
		return dateFieldNames;
	}

	public static List<Field> getDateFields(Object anObject) {
		List <Field> dateFields = new ArrayList<Field>();

		Class current = anObject.getClass();
		int fieldCount = current.getDeclaredFields().length;
		Map<String, Method> methodMap = ReflectUtils.getGetters(anObject.getClass());
		do {
			for (Field field : current.getDeclaredFields()) {
				Class fieldType = field.getType();
				if (java.util.Date.class.isAssignableFrom(fieldType)) {
					dateFields.add(field);
				} 
			}
			current = current.getSuperclass();
		} while (current.getSuperclass() != null);
		return dateFields;
	}


	public static Map<String,Method> getGetters(Class clazz) {
		TreeMap <String,Method> methods = new TreeMap<String,Method>();

		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("is") || methodName.startsWith("get")) {
				// todo should check that it doesn't return void and takes no arguments
				methods.put(methodName, method);

			}
		}
		return methods;
	}

}
