package org.javautil.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.javautil.core.text.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameValue extends LinkedHashMap<String, Object> {
	private final Logger      logger           = LoggerFactory.getLogger(getClass());
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private boolean           caseInsensitive  = true;

	public NameValue() {
		super();
	}

	public NameValue(Map<String, Object> inMap) {
		super.putAll(inMap);
	}

	public TreeMap<String, Object> getAsTreeMap() {
		return getAsTreeMap(false);
	}

	/**
	 * 
	 * @return with the keys as camel case eg ic_item_mast icItemMast
	 */
	public NameValue asAttributeName() {
		NameValue nv = new NameValue();
		for (String key : this.keySet()) {
			nv.put(StringUtils.asAttributeName(key), get(key));
		}
		return nv;
	}

	/*
	 * Postgres returns column names in lowercase oracle in upper
	 */

	public TreeMap<String, Object> getAsTreeMap(boolean toLowerCase) {
		final TreeMap<String, Object> retval = new TreeMap<>();

		if (toLowerCase) {
			for (final Entry<String, Object> e : entrySet()) {
				retval.put(e.getKey().toLowerCase(), e.getValue());
			}
		} else {
			retval.putAll(this);
		}
		return retval;
	}

	public String getSortedMultilineString() {
		final StringBuilder sb = new StringBuilder();
		final TreeMap<String, Object> sorted = new TreeMap<>();
		sorted.putAll(this);
		for (final Entry<String, Object> e : sorted.entrySet()) {
			final String valueString = e.getValue() == null ? "null" : "'" + e.getValue() + "'";
			sb.append(String.format("'%s': %s\n", e.getKey(), valueString));
		}
		return sb.toString();
	}

	public Object getObject(String name) {
		return get(name);
	}

	public String getString(String name) {
		final Object o = get(name);
		String retval = null;
		if (o != null) {
			retval = o.toString();
		}
		return retval;
	}

	public Long getLongInsensitive(String name) {
		final Object o = getInsensitive(name);
		Long retval = null;
		retval = new BigDecimal(o.toString()).longValue();
		return retval;
	}

	public Long getLong(String name) {
		final Object o = caseInsensitive ? getInsensitive(name) : get(name);
		Long retval = null;
		retval = new BigDecimal(o.toString()).longValue();
		return retval;
	}

	public Integer getInteger(String name) {
		final Object o = get(name);
		Integer retval = null;
		retval = new BigDecimal(o.toString()).intValue();
		return retval;
	}

	// TODO also used in binds s/b in an abstract class
	public Object getInsensitive(String bindName) {
		Object o;
		String searchKey = bindName;
		while (true) {
			if (super.containsKey(searchKey)) {
				o = super.get(bindName);
				break;
			}
			searchKey = bindName.toLowerCase();
			if (super.containsKey(searchKey)) {
				o = super.get(searchKey);
				break;
			}
			searchKey = bindName.toUpperCase();
			if (super.containsKey(searchKey)) {
				o = super.get(searchKey);
				break;
			}

			throw new IllegalArgumentException("key '" + bindName + "' not found in " + super.keySet());
		}
		if (!searchKey.equals(bindName)) {
			logger.debug("specified '" + bindName + "' was '" + searchKey + "'");
		}
		if (o == null) {
			logger.debug("returning null for " + bindName);
		} else {
			logger.debug("returning " + o + " for " + bindName);
		}
		return o;
	}

	public Date getDate(String name) {
		return (Date) get(name);
	}

	public Object get(String name) {
		final Object retval = super.get(name);
		if (retval == null) {
			if (!super.containsKey(name)) {
				throw new IllegalArgumentException(String.format("key: '%s' not found.", name));
			}
		}
		return retval;
	}

	/**
	 * @return the caseInsensitive
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * @param caseInsensitive the caseInsensitive to set
	 */
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}
}
