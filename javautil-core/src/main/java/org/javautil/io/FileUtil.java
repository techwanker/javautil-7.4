package org.javautil.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

	public static List<String> readAllLines(String filePath) throws IOException {
		final Charset charset = Charset.forName("ISO-8859-1");
		final Path traceFilePath = Paths.get(filePath);
		return Files.readAllLines(traceFilePath, charset);
	}

	public static void emitStringList(List<String> lines, OutputStream os) throws IOException {
		final OutputStreamWriter bos = new OutputStreamWriter(os);
		for (final String line : lines) {
			bos.write(line);
		}
	}

	public static String getAsString(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
	}

	/**
	 * @param filePathName The path
	 * @return The filename with the leading directories stripped
	 */
	public static String basename(final String filePathName) {

		String retval;
		final File f = new File(filePathName);
		final String parentDirectoryName = f.getParent();
		if (parentDirectoryName == null) {
			retval = filePathName;
		} else {
			// logger.info("parentDirectoryName: " + parentDirectoryName);
			retval = filePathName.substring(parentDirectoryName.length() + 1);
		}
		return retval;
	}

	// TODO ensure compatibility with bash basename
	public static String basename(final String filePathName, final String extension) {
		String retval = filePathName;
		final String baseFileName = basename(filePathName);
		if (baseFileName.endsWith(extension)) {
			final int endIndex = baseFileName.length() - extension.length();
			retval = baseFileName.substring(0, endIndex);
		}
		return retval;
	}

	public static File file(final String path, final String fileName) {
		return new File(getFilePathName(path, fileName));
	}

	/**
	 * Builds a new path appending the fileName and adding a file separator if
	 * necessary.
	 * 
	 * @param path     the full path
	 * @param fileName the file name
	 * @return path/filename
	 */
	public static String getFilePathName(final String path, final String fileName) {
		final StringBuilder buff = new StringBuilder();
		if (path != null) {
			buff.append(path);
		}
		if (path != null && path.length() > 0) {
			final int lastIndex = path.length() - 1;
			if (path.charAt(lastIndex) != '/') {
				buff.append("/");
			}
		}
		buff.append(fileName);
		return new String(buff);
	}

	/**
	 * Returns the file extension everything after the last "." If there is no "."
	 * returns a zero length String;
	 * 
	 * @param f file
	 * @return the file extension
	 */
	public static String getFileExtension(final File f) {

		String retval = "";
		final String fileName = f.getName();

		final int i = fileName.lastIndexOf(".");
		if (i > -1) {
			retval = fileName.substring(i + 1);

		}
		return retval;
	}

	/**
	 * Returns the name of the file with the path stripped and the extension
	 * stripped.
	 * 
	 * @param f file
	 * @return file name without the extension
	 */
	public static String getFileNameWithoutExtension(final File f) {

		String retval;
		final String name = f.getName();
		final String extension = getFileExtension(f);
		if (extension.length() > 0) {
			final int baseLength = name.length() - extension.length() - 1;
			retval = name.substring(0, baseLength);
		} else {
			retval = name;
		}

		return retval;
	}
}
