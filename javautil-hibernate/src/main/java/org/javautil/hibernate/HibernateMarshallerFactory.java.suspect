package org.javautil.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
/**
 * Marshalls Hibernate pojos, skipping the relations that reference this entity
 * and those referenced.
 * 
 * TODO should get the primary key for the referenced.
 */
public class HibernateMarshallerFactory {
	private static transient final Logger logger = LoggerFactory.getLogger(HibernateMarshallerFactory.class);

	@SuppressWarnings("serial")
	public static HashSet<Class<?>> excludeClasses = new HashSet<Class<?>>() {
		{
			add(DecimalFormat.class);
			add(SimpleDateFormat.class);
		}
	};

	@SuppressWarnings("serial")
	public static HashSet<Class<?>> excludeAnnotations = new HashSet<Class<?>>() {
		{
			add(ManyToMany.class);
			add(OneToOne.class);
			add(OneToMany.class);
			add(ManyToOne.class);
		}
	};

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

	public static GsonBuilder getHibernateGsonBuilder() {
		Logger logger = LoggerFactory.getLogger(HibernateMarshallerFactory.class);
		GsonBuilder gsonNoChildren = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				Boolean retval = false;
				String fieldName = f.getName();
				Class<?> clazz = f.getDeclaringClass();
				String skipReason = null;
				Collection<Annotation> annotations = f.getAnnotations();
				Boolean isFieldInSuper = null;
				for (Annotation annotation : annotations)  {
					if (excludeAnnotations.contains(annotation.getClass())) {
						logger.debug("skipping class {} field {) annotation {}",fieldName,clazz.getName());
						retval = true;
					}
				}
				if (! retval){
					retval = skipFieldGetter(f);
				}
				logger.debug("skip? {} inSuper? {} class: {} fieldName: {}  annotations: {} reason: {}",
						retval, isFieldInSuper, clazz, fieldName,  annotations, skipReason);
				return retval;
			}

			private String getGetter(FieldAttributes f) {
				String fieldName = f.getName();
				String capitalizedAttribute = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				String getterName;
				if (f.getDeclaredType().equals(boolean.class)) {
					getterName = "is" + capitalizedAttribute;
				} else {
					getterName = "get" + capitalizedAttribute;
				}
				return getterName;
			}

			private boolean skipFieldGetter(FieldAttributes f) {
				String fieldName = f.getName();
				Class<?> clazz = f.getDeclaringClass();
				String getterName = getGetter(f);
				boolean retval = false;

				Method getter = null;
				try {
					getter = clazz.getMethod(getterName);
					Annotation[] getterAnnotations =  getter.getAnnotations();
					for (Annotation annotation : getterAnnotations) {
						if (excludeAnnotations.contains(annotation.getClass())) {
							logger.debug("skipping class {} field {) annotation {}",fieldName,clazz.getName());
							retval = true;
							break;
						}
					}
				} catch (NoSuchMethodException | SecurityException e) {
					logger.debug("could not get method '{}' for field {} in class {}\n{}", getterName, fieldName,
							clazz.getName(), e.getMessage());
					//						e.printStackTrace();
				}
				return retval;

			}

			public boolean shouldSkipClass(Class<?> aClass) {
				boolean retval =  excludeClasses.contains(aClass); 
				if (retval) {
					logger.debug("skipping class {}",aClass.getName());
				}
				return retval;
			}
		}
				).setPrettyPrinting();

		return gsonNoChildren;
	}


}
