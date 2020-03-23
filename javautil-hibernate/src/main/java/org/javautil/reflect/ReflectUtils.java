package org.javautil.reflect;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.javautil.core.misc.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtils {
		private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

		
		public static String getSetterMethodName(String fieldName) {
			return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		
		public static String getGetterMethodName(Field field) {
		
			String prefix = "get";
			if (field.getType().equals(boolean.class)) {
				prefix = "is";
			}
			return prefix + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
		}
		@Deprecated // use getGetterMethodName(Field field)
		public static String getGetterMethodName(String fieldName) {
			return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
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
	}

