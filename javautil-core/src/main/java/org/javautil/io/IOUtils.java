package org.javautil.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

public class IOUtils {

	private IOUtils() {
	}

	public static byte[] readBytesFromStream(final InputStream input) throws IOException {
		return IOUtils.readBytesFromStream(input, 4096, true); // 4k
	}

	public static byte[] readBytesFromStream(final InputStream input, final boolean close) throws IOException {
		return IOUtils.readBytesFromStream(input, 4096, close); // 4k
	}

	public static byte[] readBytesFromStream(final InputStream input, final int initialSize, final boolean close)
	    throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("input is null");
		}
		final ByteArrayOutputStream content = new ByteArrayOutputStream(initialSize);
		final byte[] byteArray = new byte[1024];
		int bytesRead = 0;
		while ((bytesRead = input.read(byteArray)) != -1) {
			content.write(byteArray, 0, bytesRead);
		}
		// todo jjs what is the point of closing or not closing
		if (close) {
			content.close();
		}
		return content.toByteArray();
	}

	public static byte[] readBytesFromStream(final InputStream input, final int initialSize) throws IOException {
		return readBytesFromStream(input, initialSize, true);
	}

	public static String readFileAsString(String fileName) throws IOException {
		InputStream is = new FileInputStream(fileName);
		File f = new File(fileName);

		String retval = null;
		try {
			retval = readStringFromStream(is, 1024 * 1024);
		} finally {
			is.close();
		}
		return retval;
	}

	public static String readStringFromStream(final InputStream input, final int initialSize, final boolean close)
	    throws IOException {
		return new String(readBytesFromStream(input, initialSize, close));
	}

	public static String readStringFromStream(final InputStream input, final int initialSize) throws IOException {
		return new String(readBytesFromStream(input, initialSize, true));
	}

	public static String readStringFromStream(final InputStream input, final boolean close) throws IOException {
		return new String(readBytesFromStream(input, 4096, close)); // 4k
	}

	public static String readStringFromStream(final InputStream input) throws IOException {
		return new String(readBytesFromStream(input, 4096, true)); // 4k
	}

	/**
	 * Reads the input data and writes to output until the input is exhausted.
	 * 
	 * @param input  the source
	 * @param output the output
	 * @return number of characters streamed
	 * @throws IOException Not likely
	 */
	public static int pump(final Reader input, final Writer output) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("input is null");
		}
		if (output == null) {
			throw new IllegalArgumentException("output is null");
		}
		final CharBuffer charBuffer = CharBuffer.allocate(1024);
		int charsRead = 0;
		int charsStreamed = 0;
		while ((charsRead = input.read(charBuffer)) != -1) {
			output.write(charBuffer.array(), 0, charsRead);
			charsStreamed += charsRead;
		}
		return charsStreamed;
	}

	/*
	 * Could just use Readable? Reads the input data and writes to output until the
	 * input is exhausted.
	 * 
	 * @param input
	 * 
	 * @param output
	 * 
	 * @throws IOException
	 */
	public static int pump(final InputStream input, final OutputStream output) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("input is null");
		}
		if (output == null) {
			throw new IllegalArgumentException("output is null");
		}
		final byte[] byteArray = new byte[1024];
		int bytesRead = 0;
		int bytesStreamed = 0;
		while ((bytesRead = input.read(byteArray)) != -1) {
			output.write(byteArray, 0, bytesRead);
			bytesStreamed += bytesRead;
		}
		return bytesStreamed;
	}

	public static void makePathForFile(File f) {
		String parent = f.getParent();
		File parentFile = new File(parent);
		parentFile.mkdirs();
	}

	public static void writeString(File fileName, String data) throws IOException {
		Writer writer = new FileWriter(fileName);
		writer.write(data);
		writer.close();
	}

	/**
	 * 
	 * @param readable anything that implements Readable
	 * @return The contents of the input stream as a String
	 */
	public static String inputStreamToString(InputStream readable) {
		java.util.Scanner s = new java.util.Scanner(readable);
		s.useDelimiter("\\A");
		String retval = s.hasNext() ? s.next() : "";
		s.close();
		return retval;
	}

	/**
	 * https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
	 * 
	 * @return
	 */

	/**
	 * @param readable anything that implements Readable
	 * @return The contents of the Readable as a String
	 */
	public static String readableToString(Readable readable) {
		java.util.Scanner s = new java.util.Scanner(readable);
		s.useDelimiter("\\A");
		String retval = s.hasNext() ? s.next() : "";
		s.close();
		return retval;
	}
}
