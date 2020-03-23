/**
 * 
 */
package org.javautil.core.properties;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 * TODO document
 */
public class XmlPropertyManager implements PropertyManagement {
	File     propertyFile;
	Document document;

	public XmlPropertyManager(File propertyFile) throws DocumentException {
		this.propertyFile = propertyFile;

		SAXReader reader = new SAXReader();
		document = reader.read(propertyFile);

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
//	    XPath xpathSelector = DocumentHelper.createXPath("/people/person[@name='James']");
//	    // the second person living in UK
//	    String name = doc.value( "/people[@country='UK']/person[2]" );
//	    
//	    // select people elements which have location attriute with the value "London"
//	    Number count = doc.numberValueOf( "//people[@location='London']" );
//		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getMandatoryProperty(java.lang.
	 * String)
	 */
	@Override
	public String getMandatoryProperty(String key) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc) Return the last property with the specified key, to be
	 * consistent with prior versions.
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String key) {
		String returnValue = getPropertyNoWarn(key);

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String,
	 * boolean)
	 */
	@Override
	public String getProperty(String key, boolean warn) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getProperty(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getProperty(String key, String dflt) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#getPropertyNames()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String[] getPropertyNames() {

		XPath xpathSelector = DocumentHelper.createXPath("//property");

		List results = xpathSelector.selectNodes(document);
		String[] returnValue = new String[results.size()];
		int i = 0;
		for (Element el : (Iterable<Element>) results) {
			returnValue[i++] = el.attribute("name").getValue();
		}

		return returnValue;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javautil.property.PropertyManagement#getPropertyNoWarn(java.lang.String)
	 */
	@Override
	public String getPropertyNoWarn(String key) {
		String returnValue;
		XPath xpathSelector = DocumentHelper.createXPath("/property/[@name = " + key);

		List results = xpathSelector.selectNodes(document);

		Element el = (Element) results.get(results.size());
		// for (Element el : (Iterable<Element>) results) {
		returnValue = el.attribute("value").getValue();
		// }

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.property.PropertyManagement#isTrue(java.lang.String)
	 */
	@Override
	public boolean isTrue(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
