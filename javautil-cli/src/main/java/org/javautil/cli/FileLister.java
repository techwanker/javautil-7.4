package org.javautil.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javautil.file.DiskInfo;
import org.javautil.file.FileInfoMarshallerDefault;

public class FileLister {

	private static final String revision = "0.0.1";
	
	private static final Logger logger = LoggerFactory.getLogger(FileLister.class);

	public static String getBuildIdentifier() {
		return revision;
	}

	private void process(FileListerArguments arguments) throws IOException {

		Writer writer;
		boolean closeWriter = true;
		if (arguments.getOutputFile() != null) {
			writer = new FileWriter(arguments.getOutputFile());
		} else {
			writer = new OutputStreamWriter(System.out);
			closeWriter = false;
		}

		FileInfoMarshallerDefault lister = new FileInfoMarshallerDefault(writer);
		lister.setSourceName(arguments.getSourceName());
		lister.setFieldSeparator(",");
		DiskInfo diskInfo = new DiskInfo();
		diskInfo.setGenerateDigest(!arguments.isNoDigest());
		diskInfo.setFileInfoListener(lister);
		diskInfo.process(arguments.getDirectoryMask().get(0));
		writer.flush();
		if (closeWriter) {
			writer.close();
		}

	}

	public static void main(String[] args) throws Exception {
        logger.info("args " + args);
	
		FileLister invocation = new FileLister();
		FileListerArguments arguments = new FileListerArguments();
		arguments.parseArguments(args);
		invocation.process(arguments);
	}
}
