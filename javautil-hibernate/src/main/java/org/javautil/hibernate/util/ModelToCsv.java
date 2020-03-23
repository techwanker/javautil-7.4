package org.javautil.hibernate.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.persistence.EmbeddedId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javautil.reflect.ReflectUtils;

//  TODO move to javautil 
//  generate hibernate reflection 
// ***********
// ** TODO use hibernate ReflectionHelper
// ***********
public class ModelToCsv {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ReflectUtils reflectHelper;

	private static final boolean trace = false;

	public boolean skipField(Class clazz, Field field) {
		boolean retval = false;
		Class<?> type = field.getType();
		if (trace) {
			logger.info("examining field " + field.getName() + " " + type + " ");
		}

		while (true) {
			if (field.getName().startsWith("$SWITCH_TABLE$") ) {
				retval =true;
				break;
			}
			if (field.getName().equals("ut_user_nbr")) {
				retval = true;
				break;
			}
			if (field.getName().equals("lastModDt")) {
				retval = true;
				break;
			}
			if (trace) {
				logger.info("checking assignable");
			}
			if (String.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type)
					|| Date.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) ||
					type.isPrimitive()) {
				retval = false;
				break;
			}
			// logger.info("checking annotation");
			Method method = ReflectUtils.getGetter(clazz, field);
			if (method == null) {
				logger.warn(field.getName() + " does not have a getter ");
			}
			if (method == null) {
				retval = true;
			} else {
				EmbeddedId id = method.getAnnotation(javax.persistence.EmbeddedId.class);
				if (trace) {
					logger.info("id is " + id);
				}
				if (id != null) {
					retval = false;
					break;
				}
			} 
			if (trace) {
				logger.info("no reason not to skip");
			}
			retval = true;
			break;

			// if (type.isAssignableFrom(List.class)) {
			// // trace("is set");
			// retval = true;
			// break;
			// } else {
			// // trace("is not set");
			// }
			// if (getterMethod == null) {
			// trace("no getter, probably another jpa bean");
			// retval = true;
			// break;
			// }
			//
			// if (getterMethod.getAnnotation(OneToMany.class) != null) {
			// trace("isOneToMany");
			// retval = true;
			// break;
			// }
			//
			// if (getterMethod.getAnnotation(ManyToOne.class) != null) {
			// trace("is ManyToOne");
			// retval = true;
			// break;
			// }
			// break;
		}
		if (trace) {
			logger.info("skip " + retval);
		}
		return retval;
	}

	public boolean isEmbedded(Class clazz, Field field) {
		Method method = ReflectUtils.getGetter(clazz, field);
		EmbeddedId id = method.getAnnotation(javax.persistence.EmbeddedId.class);
		return id != null;
	}

	public LinkedHashMap<String, Object> getAttributeNameValues(Object anObject) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

		Class<? extends Object> current = anObject.getClass();

		do {
			for (Field field : current.getDeclaredFields()) {
				if (trace) {
					logger.info("examining " + field.getName());
				}
				if (skipField(current, field)) {
					if (trace) {
						logger.info("skipping " + field.getName());
					}
					continue;
				}
				field.setAccessible(true);
				if (isEmbedded(current, field)) {
					Object idValue;

					try {
						idValue = field.get(anObject);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}

					LinkedHashMap<String, Object> idValues = getAttributeNameValues(idValue);
					if (trace) {
						logger.info("id values: " + idValues);
					}
					map.putAll(idValues);
				} else {
					Object value = null;
					try {
						value = field.get(anObject);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					map.put(field.getName(), value);
				}
			}
			current = current.getSuperclass();
		} while (current.getSuperclass() != null);
		return map;
	}

	public String getAttributeNames(LinkedHashMap<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		boolean notfirst = false;
		for (String k : map.keySet()) {
			if (notfirst) {
				sb.append(",");
			} else {
				notfirst = true;
			}
			sb.append("\"");
			sb.append(k);
			sb.append("\"");

		}
		return sb.toString();
	}

	String formatDate(Date d) {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String result = formatter.format(d);
		return result;
	}

	/**
	 * TODO Need to escape " or use a regular library
	 * 
	 * @param objects
	 * @return
	 */
	public String toCsv(Collection<Object> objects) {
		boolean notfirst = false;
		StringBuilder sb = new StringBuilder();
		for (Object o : objects) {
			if (notfirst) {
				sb.append(",");
			} else {
				notfirst = true;
			}
			sb.append("\"");
			if (o != null) {
				if (o instanceof Date) {
					sb.append(formatDate((Date) o));
				} else {
					sb.append(o.toString());
				}
			}
			sb.append("\"");
		}
		return sb.toString();
	}

}
