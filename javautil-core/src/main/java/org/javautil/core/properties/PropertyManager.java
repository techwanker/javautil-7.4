package org.javautil.core.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javautil.util.FileNode;

/**
 * Maintain properties, support for definitions and descriptions.
 *
 * Extends the functionality of the java provided property manager.
 *
 * Extensions include
 * <ul>
 * <li>ability to next properties files</li>
 * <li>simplified integration of command line arguments into property
 * manager</li>
 * </ul>
 *
 * @author Jim Schmidt 12/2002 todo stop writing an html file every time
 *         invoked.
 */

public class PropertyManager implements PropertyManagement {

	private static Logger           logger         = LoggerFactory.getLogger(PropertyManager.class.getName());
	private final static int        PLAIN          = 0;

	private final static int        ENDARG         = 1;
	private final static int        QUOTE          = 2;
	private PropertyDefinitionS     definitions    = null;
	private boolean                 traceMessages  = false;
	// properties after applying all recursive properties
	private PropertyValues          propertyValues = new PropertyValues();
	private HashMap<String, String> netProperties  = new HashMap<String, String>();
	private HashMap<String, String> registered     = new HashMap<String, String>();
	String                          cmdAddon       = null;
	String                          fileName       = null;

	/**
	 *
	 * Takes a command line on input and returns an array of command line arguments.
	 * The current algorithm is similar to one used by Microsoft's setargv() routine
	 * (found in C run-time startup) with the following notes: Quote marks and
	 * backslashes are treated specially.
	 * <ul>
	 * <li>A quoted string is passed as a single argument, not including the opening
	 * and closing quote characters.</li>
	 * <li>A quote may be embedded in a string by placing an odd number of
	 * backslashes (`\') before it.</li>
	 * <li>Each pair of backslashes which precedes a quote results in a single
	 * backslash in the resultant string.</li>
	 * </ul>
	 * An even number of backslashes followed by a quote results in half that many
	 * backslashes, and the quote begins or ends the quoted part of the string as is
	 * appropriate. Backslashes not followed by a quote are treated normally.
	 * 
	 * <PRE>
	 *  [""] ==&gt; []
	 *  [\"] ==&gt; ["]
	 *  [" \" "] ==&gt; " ]
	 *  [" \\"] ==&gt; \]
	 *  [" \\ "] ==&gt; [ \\ ]
	 *  [" \\\" "] ==&gt; [ \" ]
	 *  etc.
	 *  ["one two three"] ==&gt; [one two three]
	 *  [one" two "three] ==&gt; [one two three]
	 *  [o"ne two t"hree] ==&gt; [one two three]
	 *  ["one \"two\" three"] ==&gt; [one "two" three]
	 *  ["x\\\"x"] ==&gt; [x\"x]
	 * </PRE>
	 * @param src the source argument
	 * @return an array of Strings containing command line parameters
	 */

	public final static String[] setArgs(String src) {

		StringBuffer tgt = new StringBuffer();
		ArrayList<String> v = new ArrayList<String>(); // Command argument collector
		int state = PLAIN; // main parse state
		int backslashCount = 0;
		int n = src.length();

		for (int i = 0; i < n; i++) {
			char c = src.charAt(i);
			switch (state) {
			case ENDARG:
			case PLAIN:
				if (Character.isWhitespace(c)) {
					if (state == PLAIN) {
						state = ENDARG;
						if (tgt.length() != 0) {
							v.add(tgt.toString());
							tgt.setLength(0);
						}
					}
					break;
				}
				state = PLAIN;

				break;
			case QUOTE:
				if (c == '\"') {
					boolean odd = ((backslashCount % 2) != 0);
					backslashCount /= 2;
					for (; backslashCount > 0; backslashCount--)
						tgt.append('\\');
					if (odd)
						tgt.append('\"');
					else
						state = (state == PLAIN) ? QUOTE : PLAIN;
				} else if (c == '\\') {
					backslashCount++;
				} else {
					for (; backslashCount > 0; backslashCount--)
						tgt.append('\\');
					tgt.append(c);
				}
				break;
			}
		}

		if (tgt.length() != 0)
			v.add(new String(tgt));

		String[] rv = new String[v.size()];
		v.toArray(rv);

		return rv;
	}

	/**
	 * Process directives in the sequence they are encountered in the source.
	 * Directives
	 * <ul>
	 * <li>$include</li>
	 * <li>$define Define the nature of a property,
	 * <ul>
	 * type
	 * <ul>
	 * <li>fileName</li>
	 * <li>boolean</li>
	 * <li>required</li>
	 * <li>logMask</li>
	 * </ul>
	 * </ul>
	 * </li>
	 * <li>$declare declare a variable for substition or for use in $if statements
	 *
	 * </li>
	 * <li>$set</li> set a variable to a value.
	 * <li>$if</li>
	 * </ul>
	 *
	 * @param p    the parser
	 * @param node the node
	 * @exception java.io.IOException Hopefully not
	 */
	private static void processDirective(PropertyLineParser p, FileNode node) throws java.io.IOException {
		if (p.getKey().equalsIgnoreCase("$include")) {
			String includeFileName = p.getValue();
			node.addChild(new File(includeFileName));

			/*
			 * !!! do not delete until implemented !!! Iterator pit =
			 * m.netProperties.keySet().iterator(); while (pit.hasNext()) { String key =
			 * (String) pit.next(); PropertyValue prop = (PropertyValue)
			 * netProperties.get(key); if (prop != null) { PropertyValue pv =
			 * (PropertyValue) m.netProperties.get(key); prop.addParsers(pv.getParsers()); }
			 * }
			 */
		}
	}

	public PropertyManager(Map<String, String> map) {
		logger.info("populating " + map.size() + " properties");
		netProperties.putAll(map);
	}

	public PropertyManager(String fileName) throws IOException {
		this(fileName, null);
		this.fileName = fileName;
	}

	public PropertyManager(String[][] nameValues) {
		for (String[] pair : nameValues) {
			netProperties.put(pair[0], pair[1]);
		}
	}

	private PropertyManager(String fileName, FileNode parent) throws java.io.IOException {
		// logger.info("processing file " + fileName + " parent " + parent);
		FileNode node = new FileNode(fileName, parent);
		PropertyManagerFile propertyFile = new PropertyManagerFile(fileName);
		PropertyLineParser[] lines = propertyFile.getParsedLines();

		for (int i = 0; i < lines.length; i++) {
			PropertyLineParser p = lines[i];
			if (p.isDirective()) {
				processDirective(p, node);
			}
		}
		// logger.info("loading properties");
		PropertyValue[] pvs = propertyFile.getPropertyValues();
		for (int i = 0; i < pvs.length; i++) {
			trace("net add " + pvs[i].getName());
			String name = pvs[i].getName();
			String value = pvs[i].getValue();
			trace(" netPropertyAdd '" + name + "' '" + value + "'");
			netProperties.put(name, value);
			propertyValues.put(pvs[i]);
		}

	}

	public void addDefinitions(PropertyDefinitionS definitions) {
		if (this.definitions == null) {
			this.definitions = new PropertyDefinitionS();
		}
		this.definitions.add(definitions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javautil.property.PropertyManagement#getBooleanProperty(java.lang.String,
	 * boolean)
	 */
	@Override
	public boolean getBooleanProperty(String key, boolean dflt) {
		boolean rc = dflt;
		String val = getProperty(key);
		if (val != null) {
			String normalizedVal = val.trim().toLowerCase();
			if (normalizedVal.equals("true") || normalizedVal.equals("yes")) {
				rc = true;
			} else if (normalizedVal.equals("false") || normalizedVal.equals("no")) {
				rc = false;
			} else {
				throw new java.lang.IllegalArgumentException(
				    "value for key: '" + key + "' is not boolean 'true' | 'yes' | 'false' | 'no'");
			}
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javautil.property.PropertyManagement#getBooleanPropertyNoWarn(java.lang.
	 * String, boolean)
	 */
	@Override
	public boolean getBooleanPropertyNoWarn(String key, boolean dflt) {
		boolean rc = dflt;
		String val = getProperty(key, false);
		if (val != null) {
			String normalizedVal = val.trim().toLowerCase();
			if (normalizedVal.equals("true") || normalizedVal.equals("yes")) {
				rc = true;
			} else if (normalizedVal.equals("false") || normalizedVal.equals("no")) {
				rc = false;
			} else {
				throw new java.lang.IllegalArgumentException(
				    "value for key: '" + key + "' is not boolean 'true' | 'yes' | 'false' | 'no'");
			}
		}
		return rc;
	}

	public String getDefinition(String key) {
		String rc = null;
		logger.debug("entering getDefinition");
		PropertyDefinition def = definitions.get(key);

		if (def != null) {
			rc = def.describe();
		}
		logger.debug("exiting getDefinition");
		return rc;
	}

	public String getDefinitionTable(String key) {
		String rc = getDefinition(key);
		if (rc == null) {
			rc = "&nbsp;"; // html helper
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getMandatoryProperty(java.lang.
	 * String)
	 */
	@Override
	public String getMandatoryProperty(String key) throws java.lang.IllegalStateException {

		String prop = getProperty(key);
		if (prop == null) {
			throw new java.lang.IllegalStateException("mandatory property not found, key: '" + key + "' in " + fileName
			    + " number of properties " + netProperties.size());
		}
		return prop;
	}

	public String getNameValuesAsString() {
		ArrayList<String> nameValues = getNameValues();
		StringBuilder sb = new StringBuilder();
		for (String line : nameValues) {
			sb.append(line);
			sb.append("\n");
		}
		return new String(sb);
	}

	public ArrayList<String> getNameValues() {
		TreeMap<String, String> propertyNameValues = new TreeMap<String, String>();
		ArrayList<String> returnValue = new ArrayList<String>();

		synchronized (netProperties) {
			returnValue.add("netProperties.size() " + netProperties.size() + "\n");
			for (String key : netProperties.keySet()) {
				String value = netProperties.get(key);
				propertyNameValues.put(key, value);
			}
			for (String key : propertyNameValues.keySet()) {
				String pair = key + "=" + propertyNameValues.get(key);
				logger.info(pair);
				returnValue.add(pair);
			}
		}

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		return getProperty(key, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String,
	 * boolean)
	 */
	@Override
	public String getProperty(String key, boolean warn) {
		String rc = null;
		try {
			trace("getProperty key '" + key + "'");
			if (definitions != null && definitions.get(key) == null) {
				if (registered.get(key) == null) {
					if (warn) {
						if (!key.startsWith("var")) {
							// Key is not a variable
							logger.warn("property '" + key + "'" + " has not been defined.");
						}
						registered.put(key, key);
					}
				}
			}
			rc = netProperties.get(key);
			if (rc != null) {
				while (rc.indexOf("$") != -1) {
					int ndxLeftEnds = rc.indexOf("${");
					String left;
					if (ndxLeftEnds == -1) {
						break;
					}

					if (ndxLeftEnds == 0) {
						left = "";
					} else {
						left = rc.substring(0, ndxLeftEnds);
					}

					int ndxRight = rc.indexOf("}", ndxLeftEnds + 1);
					if (ndxRight == -1) {
						break;
					}
					String middle = rc.substring(ndxLeftEnds + 2, ndxRight);
					String right = rc.substring(ndxRight + 1);

					rc = left + getProperty(middle) + right;
				}
			}
			return rc;
		} catch (java.lang.NullPointerException n) {
			if (definitions == null) {
				throw new java.lang.NullPointerException("definitions is null");
			}
			if (registered == null) {
				throw new java.lang.NullPointerException("registered is null");
			}
			if (netProperties == null) {
				throw new java.lang.NullPointerException("netProperties is null");
			}
			if (logger == null) {
				throw new java.lang.NullPointerException("logger is null");
			}
			throw n;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getProperty(String key, String dflt) {
		String rc = null;
		rc = getProperty(key);
		if (rc == null) {
			rc = dflt;
		}
		return rc;
	}

	public String getPropertyFileRoot() {
		return fileName;
	}

	@Override
	public String[] getPropertyNames() {
		TreeMap<String, String> propertyNames = new TreeMap<String, String>();
		String rc[];
		logger.debug("getPropertyNames begins");
		synchronized (netProperties) {
			trace("netProperties.size() " + netProperties.size());
			rc = new String[netProperties.size()];
			Iterator<String> names = netProperties.keySet().iterator();
			while (names.hasNext()) {
				String nm = names.next();
				propertyNames.put(nm, nm);
			}
			Iterator<String> it = propertyNames.values().iterator();
			for (int ndx = 0; it.hasNext(); ndx++) {
				rc[ndx] = it.next();
			}
		}
		trace("getPropertyNames end");
		return rc;
		// return propertyValues.getSortedNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javautil.property.PropertyManagement#getPropertyNoWarn(java.lang.String)
	 */
	@Override
	public String getPropertyNoWarn(String key) {
		return getProperty(key, false);
	}

	public String getValidationMessages() {
		StringBuffer buff = new StringBuffer();
		buff.append("property definition verification\n");
		PropertyDefinition[] defs = definitions.getDefinitions();
		for (int i = 0; i < defs.length; i++) {
			PropertyDefinition def = defs[i];
			String line = def.getPropertyValidationMessage(getProperty(def.getPropertyName()));
			if (line != null && line.length() > 0) {
				buff.append(line);
				buff.append("\n");
			}
		}
		return new String(buff);
	}

	@Override
	public boolean isTrue(String key) {
		return getBooleanProperty(key, false);
	}

	public void setTrace(boolean val) {
		this.traceMessages = val;
	}

	public int size() {
		return netProperties.size();
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("fileName " + fileName + "\n");
		return new String(buff);
	}

	public void trace(String message) {
		if (traceMessages) {
			logger.debug(message);
		}
	}
}
