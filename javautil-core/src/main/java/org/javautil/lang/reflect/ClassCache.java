package org.javautil.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassCache {

	private final Logger                             logger              = LoggerFactory.getLogger(getClass());
	private final FieldCache                         fields              = new FieldCache();
	private final Class<?>                           clazz;
	private Method[]                                 methods;
	private final HashMap<String, ArrayList<Method>> methodsByName       = new HashMap<>();
	private final HashMap<String, ArrayList<Method>> setterMethodsByName = new HashMap<>();
	private final HashMap<String, Method>            getterMethodsByName = new HashMap<>();

	public ClassCache(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("o is null");
		}
		this.clazz = o.getClass();
		populateMethods();
		populateFields();
	}

	public ClassCache(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz is null");
		}
		this.clazz = clazz;
		populateMethods();
		populateFields();
	}

	/**
	 * Populate with any method that starts with "set" and takes one argument or
	 * starts with "get" and takes no arguments.
	 * 
	 * It is possible that an entry is placed which is not an accessor as there
	 * might be no such field.
	 */
	private void populateMethods() {
		methods = clazz.getMethods();
		for (Method meth : methods) {
			logger.debug("processing method: {}", meth);
			String name = meth.getName();
			String prefix = name.substring(0, 3);
			int parmCount = meth.getParameterCount();
			// getters
			if (("get".equals(prefix) && parmCount == 0)) {
				logger.debug("adding getter: {}", meth);
				getterMethodsByName.put(name, meth);
			}
			// setters
			if ("set".equals(prefix) && parmCount == 1) {
				ArrayList<Method> meths = setterMethodsByName.get(name);
				if (meths == null) {
					meths = new ArrayList<Method>();
				}
				logger.debug("adding setter: '{}'", meth);
				meths.add(meth);
				setterMethodsByName.put(name, meths);
			}
			// all
			ArrayList<Method> meths = methodsByName.get(name);
			if (meths == null) {
				meths = new ArrayList<Method>();
			}
			meths.add(meth);
			methodsByName.put(name, meths);
		}
	}

	/**
	 * populate fields
	 */
	public void populateFields() {
		Field[] clazzFields = clazz.getDeclaredFields();
		if (logger.isDebugEnabled()) {
			logger.debug("clazzFields size " + clazzFields.length);
		}
		for (Field field : clazzFields) {
			if (logger.isDebugEnabled()) {
				logger.debug("found field " + field);
			}
			String fieldName = field.getName();
			FieldCacheEntry entry = new FieldCacheEntry(this, field);
			fields.put(fieldName, entry);
		}
	}

	/**
	 * @return the fields
	 */
	public FieldCache getFields() {
		return fields;
	}

	/**
	 * 
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * @return the methods
	 */
	public Method[] getMethods() {
		return methods;
	}

	/**
	 * @return the methodsByName
	 */
	public HashMap<String, ArrayList<Method>> getMethodsByName() {
		return methodsByName;
	}

	/**
	 * @return the setterMethodsByName
	 */
	public HashMap<String, ArrayList<Method>> getSetterMethodsByName() {
		return setterMethodsByName;
	}

	/**
	 * @return the getterMethodsByName
	 */
	public HashMap<String, Method> getGetterMethodsByName() {
		return getterMethodsByName;
	}
}
