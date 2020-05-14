package org.javautil.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.javautil.core.json.Serialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// https://stackoverflow.com/questions/19782745/converting-hibernate-objects-to-json-using-gson/21151855
// https://stackoverflow.com/questions/13459718/could-not-serialize-object-cause-of-hibernateproxy
// https://www.concretepage.com/google-api/java-gson-serialization-deserialization-json
// consider https://stackoverflow.com/questions/6873020/gson-date-format
public class HibernateMarshallerFactory {
	private static final Class<?>[] skipAnnotationClasses = { Serialize.class, ManyToMany.class, OneToOne.class, OneToMany.class, ManyToOne.class};
	private static final Class<?>[] skipClasses = {SimpleDateFormat.class, DecimalFormat.class};
	private static final HashSet <Class> skipClassSet = new HashSet<Class> (Arrays.asList(skipClasses));
	public static Gson getHibernateGson() {
		return getHibernateGsonWithProxyDateFormatter();
	}

	public static Gson getHibernateGsonWithProxy() {
		GsonBuilder builder = getHibernateGsonBuilder();
		builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
		return builder.create();
	}

	public static Gson getHibernateGsonWithProxyDateFormatter() {
		GsonBuilder builder = getHibernateGsonBuilder();
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
		return builder.create();
	}

	/*
	 * Marshalls Hibernate pojos, skipping the relations that reference this entity
	 * and those referenced.
	 * 
	 * TODO should get the primary key for the referenced.
	 */
	public static GsonBuilder getHibernateGsonBuilder() {
		Logger logger = LoggerFactory.getLogger(HibernateMarshallerFactory.class);
		GsonBuilder gsonNoChildren = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
			//
			@SuppressWarnings("unchecked")
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				boolean retval = false;
				String fieldName = f.getName();
				String getterName = null;
				Class<?> declaringClass = f.getDeclaringClass();
//				Collection<Annotation> annotations = f.getAnnotations();
				Boolean isFieldInSuper = null;
//				Class<?> declaredClass = f.getDeclaredClass();
				while (true) {
					for (@SuppressWarnings("rawtypes") Class clazz : skipAnnotationClasses) {
						if (f.getAnnotation(clazz) != null) {
							logger.debug("skipping fieldName {} because {}", fieldName, clazz) ;
							retval = true;
							break;
						}
					}
					//
					// try the getter
					//
					String capitalizedAttribute = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					if (f.getDeclaredType().equals(boolean.class)) {
						getterName = "is" + capitalizedAttribute;
					} else {
						getterName = "get" + capitalizedAttribute;
					}
					// try the getter
					Method getter = null;
					try {
						getter = declaringClass.getMethod(getterName);
						for (@SuppressWarnings("rawtypes") Class methodClass : skipAnnotationClasses) {
							if (getter.getAnnotation(methodClass) != null) {
								logger.debug("skipping fieldName{} because getter {} has {}", fieldName, getter.getName(), methodClass );
								retval = true;
								break;
							}
						}
						Class<?> theClass = f.getDeclaringClass();
						isFieldInSuper =  isFieldInSuperclass(theClass, fieldName);            
						if (! retval) {
							retval = isFieldInSuper;
						}
					} catch (NoSuchMethodException | SecurityException e) {
						logger.debug("could not get method '{}' for field {} in class {}\n{}", getterName, fieldName,
								declaringClass.getName(), e.getMessage());
					}
					break;
				}
				return retval;
			}

			private boolean isFieldInSuperclass(Class<?> subclass, String fieldName) {
				Class<?> superclass = subclass.getSuperclass();
				Field field = null;

				while(superclass != null) {   
					try {
						field = superclass.getDeclaredField(fieldName);
					} catch (Exception e) {
					}
					if(field != null)
						return true;

					superclass = superclass.getSuperclass();
				}

				return false;
			}
			@Override
			public boolean shouldSkipClass(Class<?> aClass) {
				return skipClassSet.contains(aClass);
			}

		}).setPrettyPrinting();

		return gsonNoChildren;
	}

}
