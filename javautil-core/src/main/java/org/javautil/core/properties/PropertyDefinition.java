package org.javautil.core.properties;

import java.io.File;
import java.util.HashMap;

import org.javautil.core.text.StringUtils;

/**
 * define a property
 */
class PropertyDefinition {
	public static final int         STRING             = 1;
	public static final int         FILE_NAME          = 2;
	public static final int         INTEGER            = 3;

	public static final int         LOG_FILE           = 4;

	//
	public static final int         REQUIRED           = 5;
	public static final int         OPTIONAL           = 6;

	public static final int         LOG_DIR            = 7;
	public static final int         MANDATORY          = 8;
	public static final int         BOOLEAN            = 9;
	public static final int         DEFAULT            = 10;
	public static final int         WRITE_DIRECTORY    = 11;
	public static final int         READ_FILE          = 12;
	public static final int         CONDITIONAL        = 13;
	public static final int         READ_DIRECTORY     = 14;
	public static final int         IP_PORT            = 15;
	public static final int         HTTP_URL           = 16;
	public static final int         FILE_HANDLER       = 17;
	public static final int         HOST               = 18;
	static HashMap<String, Integer> reservedWords      = new HashMap<String, Integer>();
	static HashMap<Integer, String> reservedWordsIndex = new HashMap<Integer, String>();
	/**
	 * Name of the property. Typically the fully qualified className that uses the
	 * property.
	 */
	private String                  name               = null;
	/*
	*/
	private Boolean                 required           = null;
	/**
	*/
	private int                     type               = -1;
	/**
	 * index for token parsing;
	 */
	private int                     tokenPos           = 0;

	String                          text;
	String                          defaultValue       = null;

	static {
		//
		reserveWord("required", REQUIRED);
		reserveWord("mandatory", REQUIRED);
		reserveWord("optional", OPTIONAL);
		reserveWord("conditional", CONDITIONAL);
		//
		reserveWord("fileName", FILE_NAME);
		reserveWord("logDir", LOG_DIR);
		reserveWord("fileHandler", FILE_HANDLER);
		reserveWord("logFile", LOG_FILE);
		reserveWord("int", INTEGER);
		reserveWord("String", STRING);
		reserveWord("boolean", BOOLEAN);
		reserveWord("default", DEFAULT);
		reserveWord("writeDirectory", WRITE_DIRECTORY);
		reserveWord("readFile", READ_FILE);
		reserveWord("readDirectory", READ_DIRECTORY);
		reserveWord("ipPort", IP_PORT);
		reserveWord("httpUrl", HTTP_URL);
		reserveWord("host", HOST);
	}

	/*
	 * public PropertyDefinition(String name, boolean required, int type) {
	 * this.name = name; this.required = new Boolean(required); switch (type) { case
	 * STRING: case FILE_NAME: case INTEGER: case LOG_FILE: case WRITE_DIRECTORY:
	 * case READ_DIRECTORY: case READ_FILE: case FILE_HANDLER: case IP_PORT: case
	 * LOG_DIR: case HTTP_URL: break; default: throw new
	 * java.lang.IllegalArgumentException("invalid type: " + type); } }
	 */

	private static void reserveWord(String word, int wordNbr) {
		Integer w = new Integer(wordNbr);
		reservedWords.put(word, w);
		reservedWordsIndex.put(w, word);
	}

	public PropertyDefinition(String text) {
		this.text = text;
		String word = null;
		name = getToken();
		while ((word = getToken()) != null) {
			Integer wordNbr = reservedWords.get(word);
			if (wordNbr == null) {
				if (word.startsWith("default")) {
					wordNbr = reservedWords.get("default");
				} else {
					throw new java.lang.IllegalArgumentException("invalid keyword '" + word + "'");
				}
			}
			switch (wordNbr.intValue()) {
			case REQUIRED:
			case MANDATORY:
			case CONDITIONAL: // for now treat as required
				required(new Boolean(true));
				break;
			case OPTIONAL:
				required(new Boolean(false));
				break;
			case BOOLEAN:
				type = BOOLEAN;
				break;
			case STRING:
				type = STRING;
				break;
			case FILE_NAME:
				type = FILE_NAME;
				break;
			case INTEGER:
				type = INTEGER;
				break;
			case LOG_FILE:
				type = LOG_FILE;
				break;
			case WRITE_DIRECTORY:
				type = WRITE_DIRECTORY;
				break;
			case READ_FILE:
				type = READ_FILE;
				break;
			case READ_DIRECTORY:
				type = READ_DIRECTORY;
				break;
			case LOG_DIR:
				type = LOG_DIR;
				break;
			case FILE_HANDLER:
				type = FILE_HANDLER;
				break;
			case IP_PORT:
				type = IP_PORT;
				break;
			case HOST:
				type = HOST;
				break;
			case HTTP_URL:
				type(wordNbr.intValue());
				break;
			case DEFAULT:
				defaultValue = word.substring(8);
				break;
			default:
				throw new java.lang.IllegalStateException("Unknown reserved word '" + word + "'");
			}
		}
		// logger.info("text '" + text + "' type " + type);
	}

	public String describe() {
		StringBuffer buff = new StringBuffer();
		// mandatory
		if (required == null || required.booleanValue() == false) {
			buff.append("optional ");
		} else {
			buff.append("mandatory ");
		}

		// type
		if (type == -1) {
			buff.append("unknownType ");
		} else {
			buff.append(reservedWordsIndex.get(new Integer(type)));
		}

		return new String(buff);
	}

	public String getPropertyName() {
		return name;
	}

	public String getPropertyValidationMessage(String propertyValue) {
		String rc = null;
		File f;
		String preamble = "name: '" + name + "'" + " value: '" + propertyValue + "'";
		String message = "";
		boolean error = false;

		if (propertyValue == null) {
			if (required != null && required.booleanValue() == true) {
				message = "required property is null";
				error = true;
			} else {
				message = "property is null";
			}
		} else {
			switch (type) {
			case STRING:
				break;
			case FILE_NAME:
				f = new File(propertyValue);
				if (!f.canRead()) {
					message = "specified file cannot be read in this context, if valid directory, validated user permissions running this process";
					error = true;
				}
				break;
			case INTEGER:
				try {
					Integer.parseInt(propertyValue);
				} catch (java.lang.NumberFormatException n) {
					message = " cannot be parsed to an int";
					error = true;
				}
				break;
			case LOG_FILE:
				f = new File(propertyValue);
				if (!f.canWrite()) {
					message = " specified file cannot be written in this context, if valid directory, validated user permissions running this process";
					error = true;
				}
				break;
			case BOOLEAN:
				if (!(propertyValue.equalsIgnoreCase("true") || propertyValue.equalsIgnoreCase("false"))) {
					message = "boolean property invalid value";
					error = true;
				}
				break;
			case WRITE_DIRECTORY:
				f = new File(propertyValue);
				if (!f.canWrite()) {
					message = " specified directory cannot be written in this context, if valid directory, validated user permissions running this process";
					error = true;
				} else {
					message = " directory " + propertyValue + " is writable";
				}
				break;
			case READ_DIRECTORY:
				f = new File(propertyValue);
				if (!f.canRead()) {
					message = " specified directory cannot be read in this context, if valid directory, validated user permissions running this process";
					error = true;
				}
				break;
			case READ_FILE:
				f = new File(propertyValue);
				if (!f.canRead()) {
					message = " specified file cannot be read in this context, if valid directory, validated user permissions running this process";
					error = true;
				}
				break;
			case FILE_HANDLER:
				break;
			case HOST:
				break;
			case IP_PORT:
				try {
					int i = Integer.parseInt(propertyValue);
					if (i < 0 || i > 65535) {
						message = propertyValue + " is not a valid port number ";
						error = true;
					}
				} catch (java.lang.NumberFormatException n) {
					message = " cannot be parsed to an int";
					error = true;
				}
				break;
			case LOG_DIR:
				f = new File(propertyValue);
				if (!f.canWrite()) {
					message = " specified directory cannot be written in this context, if valid directory, validated user permissions running this process";
					error = true;
				}
				break;
			case HTTP_URL:
				break;
			default:
				throw new java.lang.IllegalArgumentException("invalid type: " + type + " name " + name + " text " + text);
			}
		}
		if (error) {
			rc = preamble + " " + message;
		}
		return rc;
	}

	public int getType() {
		return type;
	}

	public void required(Boolean val) {
		if (required != null) {
			throw new java.lang.IllegalStateException(
			    "attempted to set 'required','mandatory' or 'optional' more than once or conflict");
		}
		required = val;
	}

	public void type(int val) {
		if (type != -1) {
			throw new java.lang.IllegalStateException("type defined more than once");
		}
		type = val;
	}

	private String getToken() {
		String rc = null;
		int tokenStart;
		int tokenEnd;
		//
		tokenStart = StringUtils.firstNotIn(text, " \t\r\n\f", tokenPos);
		if (tokenStart > -1) {
			tokenEnd = StringUtils.indexOfAny(text, " \t\r\n\f", tokenStart);
			if (tokenEnd == -1) {
				tokenEnd = text.length();
			}
			if (tokenEnd > tokenStart) {
				rc = text.substring(tokenStart, tokenEnd);
				tokenPos = tokenEnd;
			}
		}

		return rc;
	}

}
