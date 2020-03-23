package org.javautil.core.properties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container of Property DefinitionS
 */
public class PropertyDefinitionS {
	private static final String                 className = "com.javautil.property.PropertyDefinitionS";
	private static final Logger                 logger    = LoggerFactory.getLogger(className);
	private HashMap<String, PropertyDefinition> map       = new HashMap<String, PropertyDefinition>();

	public PropertyDefinitionS() {
	}

	/**
	 * Add the definition, replace if it exists.
	 * @param definition the PropertyDefinition to be added
	 */
	public void add(PropertyDefinition definition) {
		map.put(definition.getPropertyName(), definition);
	}

	public void add(PropertyDefinitionS definitions) {
		Iterator it = definitions.map.values().iterator();
		while (it.hasNext()) {
			PropertyDefinition d = (PropertyDefinition) it.next();
			map.put(d.getPropertyName(), d);
		}
	}

	public PropertyDefinition get(String propertyName) {
		return map.get(propertyName);
	}

	public PropertyDefinition[] getDefinitions() {
		PropertyDefinition[] definitions = null;
		synchronized (map) {
			Iterator it = map.values().iterator();
			definitions = new PropertyDefinition[map.size()];
			int i = 0;
			while (it.hasNext()) {
				PropertyDefinition d = (PropertyDefinition) it.next();
				definitions[i++] = d;
			}
		}
		return definitions;
	}

	public void loadFromResource(String resourceName) throws java.io.IOException {
		PropertyDefinition[] definitions = null;
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
			PropertyManagerFile pf = new PropertyManagerFile();
			if (stream == null) {
				throw new java.lang.IllegalArgumentException("irresolvable resource '" + resourceName + "'");
			}
			pf.read(stream);
			logger.info("read complete");
			definitions = pf.getDefinitions();
			logger.info("definitions " + definitions);
			logger.info("appending " + definitions.length + " definitions");
			for (int i = 0; i < definitions.length; i++) {
				map.put(definitions[i].getPropertyName(), definitions[i]);
			}
		} catch (java.io.IOException i) {
			throw new java.io.IOException("Failed to load resource: '" + resourceName + "'\n" + i.getMessage());
		}
	}

}
