package org.javautil.io;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHelper {

	private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class);

	/**
	 * @param object       TODO document
	 * @param resourceName should start with "/"
	 * @return the file
	 */
	public static File getResourceAsFile(Object object, String resourceName) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		if (resourceName == null) {
			throw new IllegalArgumentException("resourceName is null");
		}
		final ClassLoader classLoader = object.getClass().getClassLoader();
		final URL resourceUrl = classLoader.getResource(resourceName);
		if (resourceUrl == null) {
			final String message = "can't get resourceUrl for  " + resourceName;
			log.error(message);
			throw new IllegalArgumentException("can't get resourceUrl for  " + resourceName);
		}
		final String fileString = resourceUrl.getFile();
		log.debug("fileString: " + fileString);

		return new File(fileString);
	}

	public static InputStream getResourceAsInputStream(Object object, String resourceName) {
		if (object == null) {
			throw new IllegalArgumentException("object is null");
		}
		if (resourceName == null) {
			throw new IllegalArgumentException("resourceName is null");
		}
		final ClassLoader classLoader = object.getClass().getClassLoader();
		final InputStream is = classLoader.getResourceAsStream(resourceName);
		return is;
	}

}
