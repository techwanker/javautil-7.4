package org.javautil.core.sql;

import java.util.Map;
import java.util.Map.Entry;

import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Needs unit tests
public class Binds extends NameValue {
	/**
	 *
	 */
	private static final long serialVersionUID = 283357236262161762L;
	private final Logger      logger           = LoggerFactory.getLogger(this.getClass());

	public Binds() {
		super();
	}

	public Binds(Map<String, Object> bindMap) {
		super.putAll(bindMap);
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> e : entrySet()) {
			String className = e.getValue() != null ? e.getValue().getClass().getName() : null;
			String displayValue = e.getValue() != null ? e.getValue().toString() : null;
			
			sb.append(String.format("name: '%s' value: %s class %s\n", e.getKey(), displayValue, className ));
		}
		return sb.toString();
	}


	
}
