package org.javautil.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 */
public class FieldCacheEntry {

	private transient final Logger  logger        = LoggerFactory.getLogger(getClass());

	/**
	 * The name of the inspected field on the bean.
	 */
	private final String            fieldName;

	/**
	 * The class of the inspected field on the bean.
	 */
	private Class<? extends Object> fieldClass    = null;

	/**
	 * The method used to "get" the value of the field.
	 */
	private Method                  getterMethod  = null;

	/**
	 * The method used to "set" the value of the field.
	 */
	private ArrayList<Method>       setterMethods = null;

	/**
	 * When true, an exception will be thrown when a getter is missing.
	 */
	private boolean                 requireGetter = false;

	/**
	 * When true, an exception will be thrown when a setter is missing.
	 */
	private boolean                 requireSetter = false;

	private final Field             field;

	private final ClassCache        classInfo;

	/**
	 * @param classInfo the class that contains the Field
	 * @param field     the field
	 */
	public FieldCacheEntry(ClassCache classInfo, final Field field) {
		if (classInfo == null) {
			throw new IllegalArgumentException("containingClass is null");
		}
		if (field == null) {
			throw new IllegalArgumentException("field is null");
		}
		this.fieldName = field.getName();
		this.field = field;
		this.classInfo = classInfo;
		setSetters();
		setGetters();
	}

	/**
	 * True when the getter method exists.
	 * 
	 * @return True if the method has a a getter
	 */
	public boolean hasGetter() {
		preparePropertyAccess();
		return getterMethod != null;
	}

	/**
	 * 
	 * @return True when the setter method exists.
	 */
	public boolean hasSetter() {
		return setterMethods != null;
	}

	/**
	 * Resolve the getter and setter methods for the field.
	 * 
	 * Uses standard Bean conventions the setter is "set" + ObjectName
	 * 
	 * The getter is "get" + ObjectName (first letter uppercase) and "is" +
	 * ObjectName (first letter uppercase) for booleans
	 * 
	 * Complains if there is not both a setter and a getter, but this might be an
	 * immutable bean with only getters.
	 * 
	 */
	protected void preparePropertyAccess() {
	}

	void setGetters() {
		final String capitalizedPropertyName = StringUtils.capitalize(fieldName);
		String getterMethodName = "get" + capitalizedPropertyName;
		HashMap<String, Method> methods = classInfo.getGetterMethodsByName();
		this.fieldClass = field.getType();
		if (this.fieldClass.equals(Boolean.TYPE)) {
			getterMethodName = "is" + capitalizedPropertyName;
		}
		getterMethod = methods.get(getterMethodName);
		if (getterMethod != null) {
			getterMethod.setAccessible(true);
			fieldClass = getterMethod.getReturnType();
		}

		final boolean getterFailure = requireGetter && getterMethod == null;
		if (getterFailure) {
			throw new IllegalArgumentException("Property '" + fieldName + "' does not have a getter method" + " '"
			    + getterMethodName + "()'" + " on the bean " + classInfo.getClazz().getName());
		}
	}

	void setSetters() {
		final String capitalizedPropertyName = StringUtils.capitalize(fieldName);
		String setterMethodName = "set" + capitalizedPropertyName;
		HashMap<String, Method> methods = classInfo.getGetterMethodsByName();

		setterMethods = classInfo.getSetterMethodsByName().get(setterMethodName);

		if (setterMethods == null) {
			logger.warn("field {} has no setter {}", fieldName, setterMethodName);
		}
		if (setterMethods != null) {
			for (Method method : setterMethods) {
				method.setAccessible(true);
			}
		}
		final boolean setterFailure = requireSetter && setterMethods == null;
		if (setterFailure) {
			throw new IllegalArgumentException(
			    "Property '" + fieldName + "' does not have a setter method" + " '" + setterMethodName + "("
			        + fieldClass.getSimpleName() + " " + fieldName + ")'" + " on the bean " + classInfo.getClazz().getName());
		}
	}

	public String getPropertyName() {
		return fieldName;
	}

	public Class<? extends Object> getPropertyType() {
		preparePropertyAccess();
		return fieldClass;
	}

	public void setPropertyType(final Class<? extends Object> propertyType) {
		this.fieldClass = propertyType;
	}

	public boolean isRequireGetter() {
		return requireGetter;
	}

	public void setRequireGetter(final boolean requireGetter) {
		this.requireGetter = requireGetter;
	}

	public boolean isRequireSetter() {
		return requireSetter;
	}

	public void setRequireSetter(final boolean requireSetter) {
		this.requireSetter = requireSetter;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<? extends Object> getFieldClass() {
		return fieldClass;
	}

	public Method getGetterMethod() {
		return getterMethod;
	}

	public ArrayList<Method> getSetterMethods() {
		return setterMethods;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
		    "FieldCacheEntry [fieldName=%s, fieldClass=%s, getterMethod=%s, setterMethods=%s, requireGetter=%s, requireSetter=%s, field=%s, classInfo=%s]",
		    fieldName, fieldClass, getterMethod, setterMethods, requireGetter, requireSetter, field, classInfo);
	}
}
