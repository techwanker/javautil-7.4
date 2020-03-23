package org.javautil.hibernate.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javautil.reflect.ReflectUtils;

//https://stackoverflow.com/questions/8524011/java-reflection-how-can-i-get-the-all-getter-methods-of-a-java-class-and-invoke
//Object f = new PropertyDescriptor("field", A.class).getReadMethod().invoke(a);
//https://stackoverflow.com/questions/28697484/how-to-invoke-a-getter-method-by-its-name

public class ModelToMap {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Collection<Class> excludedAnnotations = new HashSet<Class>();

	private Set<String> excludedFieldNames = new HashSet<String>();
	
	private String dateFormat  = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	public ModelToMap() {
		excludedAnnotations.add(OneToMany.class);
		excludedAnnotations.add(ManyToOne.class);

		excludedFieldNames.add("handler");
		excludedFieldNames.add("utUserNbr");
		excludedFieldNames.add("lastModDt");

	}

	public Object getFieldValue(Map<String, Method> methodMap, Object obj, Field field) {
		// throws IntrospectionException, IllegalAccessException,
		// IllegalArgumentException, InvocationTargetException {
		// https://stackoverflow.com/questions/28697484/how-to-invoke-a-getter-method-by-its-name

		Method getter = ReflectUtils.getGetter(methodMap, field);
		Object retval = null;
		if (getter == null) {
			throw new IllegalArgumentException("Field " + field.getName() + " has no getter");
		}
		try {
			retval = getter.invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String message = "While processing field " + field.getName() + " with class " + obj.getClass();
			throw new IllegalArgumentException(message + " " + e.getMessage(), e);
		}

		return retval;
	}

	private static final boolean trace = false;

	/* TODO create a set of Annotations to be excluded); */
	public boolean skipField(Class clazz, Field field) {
		boolean retval = false;
		Class<?> type = field.getType();
		if (trace) {
			logger.debug("examining field " + field.getName() + " " + type + " ");
		}

		while (true) {
			if (field.getName().startsWith("$SWITCH_TABLE$")) {
				retval = true;
				break;
			}
			if (excludedFieldNames.contains(field.getName())) {
				retval = true;
				break;
			}
			if (String.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type)
					|| Date.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)
					|| type.isPrimitive()) {
				retval = false;
				break;
			}
			// logger.debug("checking annotation");
			Method method = ReflectUtils.getGetter(clazz, field);
			if (method == null) {
				//logger.warn(field.getName() + " does not have a getter ");
				retval = false;
				break;
			}
			EmbeddedId id = method.getAnnotation(javax.persistence.EmbeddedId.class);
			if (trace) {
				logger.debug("id is " + id);
			}
			if (id != null) {
				retval = false;
				break;
			}


			if (type.isAssignableFrom(List.class)) {
				retval = true;
				break;
			}
			retval = isExcludedAnnotation(method);
			break;

		}
		//logger.debug("skip " + retval);
		return retval;
	}

	@SuppressWarnings("unchecked")
	public boolean isExcludedAnnotation(Method method) {
		boolean retval = false;
		for (Class annotation : excludedAnnotations) {
			if (method.getAnnotation(annotation) != null) {
				retval = true;
			}
		}
		return retval;
	}

	public boolean isEmbedded(Class clazz, Field field) {
		Method method = ReflectUtils.getGetter(clazz, field);
		boolean retval = false;
		if (method != null && method.getAnnotation(javax.persistence.EmbeddedId.class) != null) {
			retval = true;
		}
		return retval;
	}

	
	public LinkedHashMap<String, Object> getAttributeNameValues(Object anObject) {

		logger.debug("getAttributeNameValues for " + anObject.getClass().toString());
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		Class current = anObject.getClass();

		Map<String, Method> methodMap = ReflectUtils.getGetters(anObject.getClass());

		do {

			for (Field field : current.getDeclaredFields()) {
				if (trace) {
					logger.debug("examining " + field.getName());
				}
				if (skipField(current, field)) {
					if (trace) {
						logger.debug("skipping " + field.getName());
					}
					continue;
				}
				field.setAccessible(true);
				Method getter = ReflectUtils.getGetter(methodMap, field);
				if (isEmbedded(current, field)) {
					Object idValue;

					try {
						idValue = field.get(anObject);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}

					LinkedHashMap<String, Object> idValues = getAttributeNameValues(idValue);
					if (trace) {
						logger.debug("id values: " + idValues);
					}
					map.putAll(idValues);
				} else {
					if (getter != null) {
						Object value;
						try {
							value = getter.invoke(anObject);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							logger.warn("processing field " + field.getName() + " anObject " + anObject.getClass().getName());
							e.printStackTrace();
							throw new RuntimeException(e);
						}
						map.put(field.getName(), value);
						if (trace) {
							logger.debug("#### field: " + field.getName() + " value: " + value);
						}
					}
				}
			}
			current = current.getSuperclass();
		} while (current.getSuperclass() != null);
		return map;
	}


	String formatDate(Date d) {
		String result = formatter.format(d);
		return result;
	}

	/**
	 * TODO Need to escape " or use a regular library
	 * 
	 * @param objects
	 * @return
	 */
	public List<Map<String,Object>> toListOfMaps(Collection<? extends Object> objects) {
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Object o : objects) {
			Map<String,Object> objectMap = getAttributeNameValues(o);
			list.add(objectMap);
		}
		return list;
	}

	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		formatter = new SimpleDateFormat(dateFormat);
	}
}
