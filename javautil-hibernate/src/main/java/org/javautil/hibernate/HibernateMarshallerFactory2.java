package org.javautil.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Collection;

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
public class HibernateMarshallerFactory2 {

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
		Logger logger = LoggerFactory.getLogger(HibernateMarshallerFactory2.class);
		GsonBuilder gsonNoChildren = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
		//
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				boolean retval = false;
				String fieldName = f.getName();
				String getterName = null;
				Class<?> declaringClass = f.getDeclaringClass();
				String skipReason = null;
				Collection<Annotation> annotations = f.getAnnotations();
				Boolean isFieldInSuper = null;
				Class<?> declaredClass = f.getDeclaredClass();
				logger.info("declaredClass {}", declaredClass);
				System.out.println("declaredClass " + declaredClass + " declaring " + declaringClass);
				while (true) {
					if (declaredClass == DecimalFormat.class  || declaringClass == DecimalFormat.class) {
						skipReason = "DecimalFormat";
						logger.debug("skipping {}",skipReason);
						System.out.println("skipping " + skipReason);
						retval = true;
						break;
					}
//				System.out.println("skipping " + retval + "  declaredClass " + declaredClass + " declaring " + declaringClass);
//					if (f.getAnnotation(Serialize.class) != null) {
//						skipReason = "include has Serialize";
//						break;
//					}
//					if (f.getAnnotation(ManyToMany.class) != null) {
//						skipReason = "field ManyToMany";
//						retval = true;
//						break;
//					}
//					if (f.getAnnotation(OneToOne.class) != null) {
//						skipReason = "field OneToOne";
//						retval = true;
//						break;
//					}
//					if (f.getAnnotation(OneToMany.class) != null) {
//						skipReason = "field OneToMany";
//						retval = true;
//						break;
//					}
//					if (f.getAnnotation(ManyToOne.class) != null) {
//						skipReason = "field ManyToOone";
//						retval = true;
//						break;
//					}
					// try the field
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

						if (getter.getAnnotation(ManyToMany.class) != null) {
							skipReason = String.format("ManyToMany on method %s", getter.getName());
							retval = true;
							break;
						}
						if (getter.getAnnotation(OneToOne.class) != null) {
							skipReason = String.format("OneToOne on method %s", getter.getName());
							retval = true;
							break;
						}
						if (getter.getAnnotation(OneToMany.class) != null) {
							skipReason = String.format("OneToMany on method %s", getter.getName());
							retval = true;
							break;
						}
						if (getter.getAnnotation(ManyToOne.class) != null) {
							skipReason = String.format("ManyToOne on method %s", getter.getName());
							retval = true;
							break;
						}
						Class<?> theClass = f.getDeclaringClass();
						isFieldInSuper =  isFieldInSuperclass(theClass, fieldName);            
						//logger.debug("{} {} isFieldInSuper {}",theClass.getName(),fieldName,isFieldInSuper );
						retval = isFieldInSuper;
					} catch (NoSuchMethodException | SecurityException e) {
						logger.debug("could not get method '{}' for field {} in class {}\n{}", getterName, fieldName,
								declaringClass.getName(), e.getMessage());
						//						e.printStackTrace();
					}
					break;
				}
				logger.debug("skip? {} inSuper? {} class: {} fieldName: {} methodName {} annotations: {} reason: {}",
						retval, isFieldInSuper, declaringClass, fieldName, getterName, annotations, skipReason);
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
				boolean retval = false;
//				if (aClass == DecimalFormat.class) {
//					retval = true;
//				}
//				logger.debug("shouldSkipClass {}", retval);
				return retval;
			}
			
		}).setPrettyPrinting();

		return gsonNoChildren;
	}



}
