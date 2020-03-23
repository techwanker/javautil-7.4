package org.javautil.java;

import java.io.File;

import org.javautil.lang.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs
 * 
 */

public class JavaFile {
	private final Logger        logger        = LoggerFactory.getLogger(getClass());

	private static final String fileSeparator = SystemProperties.getFileSeparator();

	/**
	 * Returns the File that represents the location of the source file.
	 * 
	 * Computes the file path for a qualified class name. If the baseDirectory is
	 * "target/tmp" and the packageClassName is "com.disney.rodents.Mouse"
	 * 
	 * This will attempt to create the directory path, whether or not the return
	 * value is "target/tmp/com/disney/rodents/Mouse".
	 * 
	 * @param baseDirectoryName source root
	 * @param packageName       java package name
	 * @param className         name of class
	 * @return the file of the java
	 */
	public File getJavaFile(final String baseDirectoryName, final String packageName, final String className) {

		final File baseDirectory = new File(baseDirectoryName);
		if (!baseDirectory.exists()) {
			throw new java.lang.IllegalArgumentException("no such directory '" + baseDirectory.getAbsolutePath() + "'");
		}
		final String filePathForClass = packageName.replace(".", SystemProperties.getFileSeparator());
		final String sourceFilePath = baseDirectory.getPath() + fileSeparator + filePathForClass + fileSeparator + className
		    + ".java";
		final File retval = new File(sourceFilePath);
		// create the directory
		final File directory = retval.getParentFile();
		directory.mkdirs();

		if (logger.isDebugEnabled()) {
			final StringBuilder b = new StringBuilder();
			b.append("destinationDirectoryRoot " + baseDirectory.getAbsolutePath() + "\n");
			b.append("packageName " + packageName + "\n");
			b.append("sourceFilePath " + sourceFilePath + "\n");
			b.append("retval absolute " + retval.getAbsolutePath() + "\n");
			logger.debug(b.toString());
		}
		return retval;
	}

}
